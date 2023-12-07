package net.oneironaut.casting.patterns.spells.great

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.mod.HexConfig
import at.petrak.hexcasting.api.mod.HexTags
import at.petrak.hexcasting.api.spell.ParticleSpray
import at.petrak.hexcasting.api.spell.RenderedSpell
import at.petrak.hexcasting.api.spell.SpellAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getLivingEntityButNotArmorStand
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.NullIota
import at.petrak.hexcasting.api.spell.mishaps.MishapImmuneEntity
import at.petrak.hexcasting.api.spell.mishaps.MishapLocationTooFarAway
import at.petrak.hexcasting.api.utils.downcast
import at.petrak.hexcasting.common.blocks.BlockConjured
import at.petrak.hexcasting.common.lib.HexBlocks
import at.petrak.hexcasting.xplat.IXplatAbstractions
import dev.architectury.platform.Platform
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.math.Vec3d
import net.oneironaut.getDimIota
import net.oneironaut.registry.DimIota
import net.minecraft.block.Blocks
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.TeleportTarget
import net.oneironaut.isSolid
import net.oneironaut.isUnsafe
//import net.oneironaut.registry.OneironautThingRegistry
import kotlin.math.floor


class OpDimTeleport : SpellAction {
    override val argc = 2
    override val isGreat = true
    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>>? {
        val target = args.getLivingEntityButNotArmorStand(0, argc)
        ctx.assertEntityInRange(target)
        val origin = ctx.world
        val coords = target.pos
        var world = ctx.world
        var worldKey = world.registryKey
        var noosphere = false
        val destination : DimIota
        if (args[1] is NullIota){
            noosphere = true;
            destination = DimIota("oneironaut:noosphere")
        } else {
            destination = args.getDimIota(1, argc)
        }
        val dimKey = destination.serialize().downcast(NbtCompound.TYPE).getString("dim_key")
        //iterate over all the worlds to find the desired one
        target.server?.worlds?.forEach {
            if (it.registryKey.value.toString() == dimKey){
                world = it;
                worldKey = it.registryKey
            }
        }
        /*walksonator was wrong
        ctx.caster.sendMessage(Text.of(target.type.toString()))
        ctx.caster.sendMessage(Text.of(ctx.caster.type.toString()))*/
        //do not do the bad thing
        if (!HexConfig.server().canTeleportInThisDimension(worldKey))
            throw MishapLocationTooFarAway(coords, "bad_dimension")
        if (!target.canUsePortals() || target.type.isIn(HexTags.Entities.CANNOT_TELEPORT))
            throw MishapImmuneEntity(target)
        if (target.type.toString() == "entity.minecraft.player" && target != ctx.caster as LivingEntity){
            throw MishapImmuneEntity(target)
        }

        return if (origin == world && !noosphere){
            Triple(
                Spell(target, origin, world, coords, false),
                //don't consume amethyst if trying to teleport to the same dimension you're already in
                0,
                listOf(ParticleSpray.cloud(target.pos, 2.0))
            )
        } else {
            Triple(
                Spell(target, origin, world, coords, noosphere),
                20 * MediaConstants.CRYSTAL_UNIT,
                listOf(ParticleSpray.cloud(target.pos, 2.0))
            )
        }
    }

    private data class Spell(var target: LivingEntity, val origin: ServerWorld, val destination: ServerWorld, val coords: Vec3d, val noosphere: Boolean) : RenderedSpell {
        override fun cast(ctx: CastingContext) {
            var x = coords.x
            var y = floor(coords.y)
            var z = coords.z
            val border = destination.worldBorder
            val compressionFactor = origin.dimension.coordinateScale / destination.dimension.coordinateScale
            x = floor(x * compressionFactor) + 0.5
            z = floor(z * compressionFactor) + 0.5
            var floorSpot : BlockPos = BlockPos(Vec3d.ZERO)
            var floorNeeded = false
            //make sure you don't end up under the nether or something
            if (destination.bottomY > coords.y - 5.0){
                y = ((destination.bottomY + 5).toDouble())
            }
            //make sure you don't end up outside the world border
            if (x > border.boundEast){
                x = border.boundEast - 2
            } else if (x < border.boundWest){
                x = border.boundWest + 2
            }
            if (z > border.boundSouth){
                z = border.boundSouth - 2
            } else if (z < border.boundNorth){
                z = border.boundNorth + 2
            }
            //actually put you on the floor if possible
            var scanPoint = BlockPos(Vec3d(x, y+1, z))
            while(!isSolid(destination, scanPoint)){
                //ctx.caster.sendMessage(Text.of(destination.getBlockState(scanPoint).block.toString()))
                scanPoint = BlockPos(Vec3d(x, scanPoint.y.toDouble() - 1, z))
                //check for void
                if (scanPoint.y < destination.bottomY || isUnsafe(destination, scanPoint, false)){
                    //ctx.caster.sendMessage(Text.of("scanpoint: ${scanPoint.y}, bottomY: ${destination.bottomY}, safety: ${isUnsafe(destination, scanPoint)}"))
                    scanPoint = BlockPos(Vec3d(x, y+1, z))
                    break
                }
            }
            //try to avoid putting your head in solid rock or something
            while(isUnsafe(destination, scanPoint, true) || isSolid(destination, scanPoint)){
                //ctx.caster.sendMessage(Text.of(destination.getBlockState(scanPoint).block.toString()))
                scanPoint = BlockPos(Vec3d(x, scanPoint.y.toDouble() + 1, z))
                //check for ceiling
                if (destination.getBlockState(scanPoint).block.equals(Blocks.BEDROCK)){
                    break
                }
            }
            if (!(destination.getBlockState(scanPoint).block.equals(Blocks.BEDROCK))){
                if (isUnsafe(destination, BlockPos(Vec3d(x, (scanPoint.y - 1).toDouble(), z)), true) || !isSolid(destination, BlockPos(Vec3d(x, (scanPoint.y - 1).toDouble(), z)))){
                    y = (scanPoint.y + 1).toDouble()
                    if (!isSolid(destination, BlockPos(Vec3d(x, (scanPoint.y - 1).toDouble(), z)))){
                        floorNeeded = true
                        floorSpot = BlockPos(Vec3d(x, (scanPoint.y - 1).toDouble(), z))
                    }
                }
                y = (scanPoint.y).toDouble()
            }
            val colorizer = IXplatAbstractions.INSTANCE.getColorizer(ctx.caster)
            if (origin == destination){
                ctx.caster.sendMessage(Text.translatable("hexcasting.spell.oneironaut:dimteleport.samedim"));
            }else {
                if (target.type.toString() == "entity.minecraft.player"){
                    (target as ServerPlayerEntity).teleport(destination, x, y, z, target.yaw, target.pitch)
                    //FabricDimensions.teleport(target, destination, TeleportTarget(Vec3d(x, y, z), Vec3d.ZERO, target.yaw, target.pitch))
                    target.addStatusEffect(StatusEffectInstance(StatusEffects.SLOW_FALLING, 1200))
                    if (noosphere){
                        target.addStatusEffect(StatusEffectInstance(StatusEffects.NAUSEA, 200))
                        target.addStatusEffect(StatusEffectInstance(StatusEffects.BLINDNESS, 100))
                    }
                    if (floorNeeded){
                        destination.setBlockState((floorSpot), HexBlocks.CONJURED_BLOCK.defaultState)
                        BlockConjured.setColor(destination, floorSpot, colorizer)
                    }
                } else {
                    target.addStatusEffect(StatusEffectInstance(StatusEffects.SLOW_FALLING, 1200))
                    if (Platform.isForge()){
                        //for some reason I couldn't get any other method of teleportation to work for non-players on forge
                        val destString = destination.registryKey.value.toString()
                        val command = "execute in $destString as ${target.uuid.toString()} run tp $x $y $z"
                        val executor = target.server?.commandManager
                        executor?.executeWithPrefix(target.server?.commandSource?.withSilent(), command)
                    } else {
                        FabricDimensions.teleport(target, destination, TeleportTarget(Vec3d(x, y, z), target.velocity, target.yaw, target.pitch))
                    }

                    if (floorNeeded){
                        destination.setBlockState((floorSpot), HexBlocks.CONJURED_BLOCK.defaultState)
                        BlockConjured.setColor(destination, floorSpot, colorizer)
                    }
                    //now I can :)
                    //but should I? forge support and all
                    //FabricDimensions.teleport(target, destination, TeleportTarget(Vec3d(x, y, z), target.velocity, target.yaw, target.pitch))
                }
            }
        }
    }
}