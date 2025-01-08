package net.beholderface.oneironaut.casting.patterns.spells.great

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getLivingEntityButNotArmorStand
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import at.petrak.hexcasting.api.casting.mishaps.MishapBadLocation
import at.petrak.hexcasting.api.casting.mishaps.MishapImmuneEntity
import at.petrak.hexcasting.api.casting.mishaps.MishapLocationInWrongDimension
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.mod.HexConfig
import at.petrak.hexcasting.api.mod.HexTags
import at.petrak.hexcasting.common.blocks.BlockConjured
import at.petrak.hexcasting.common.lib.HexBlocks
import at.petrak.hexcasting.xplat.IXplatAbstractions
import dev.architectury.platform.Platform
import net.beholderface.oneironaut.*
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.math.Vec3d
import net.beholderface.oneironaut.casting.iotatypes.DimIota
import net.minecraft.block.Blocks
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.TeleportTarget
import net.beholderface.oneironaut.casting.DepartureEntry
import net.minecraft.util.math.Vec3i
import java.util.HashMap
//import net.oneironaut.registry.OneironautThingRegistry
import kotlin.math.floor




class OpDimTeleport : SpellAction {
    override val argc = 2

    override fun execute(args: List<Iota>, ctx: CastingEnvironment): SpellAction.Result {
        if (ctx.castingEntity == null){
            throw MishapBadCaster()
        }
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
            destination =
                DimIota("oneironaut:noosphere")
        } else {
            destination = args.getDimIota(1, argc)
        }
        val dimKey = destination.dimString
        //val dimKey = destination.serialize().downcast(NbtCompound.TYPE).getString("dim_key")
        //iterate over all the worlds to find the desired one
        target.server?.worlds?.forEach {
            if (it.registryKey.value.toString() == dimKey){
                world = it;
                worldKey = it.registryKey
            }
        }
        //do not do the bad thing
        if (!HexConfig.server().canTeleportInThisDimension(worldKey))
            throw MishapLocationInWrongDimension(worldKey.value)
        if (!target.canUsePortals() || target.type.isIn(HexTags.Entities.CANNOT_TELEPORT))
            throw MishapImmuneEntity(target)
        if (target.isPlayer && target != ctx.caster as LivingEntity && !OneironautConfig.server.planeShiftOtherPlayers){
            throw MishapImmuneEntity(target)
        }

        var departure = false
        if (target == ctx.caster){
            val entry = DepartureEntry.getEntry(ctx, world)
            if (entry != null){
                if (entry.isWithinCylinder(target.pos)){
                    departure = true
                }
            }
        }

        val cost = if(departure){
            5 * MediaConstants.DUST_UNIT
        } else {
            20 * MediaConstants.CRYSTAL_UNIT
        }

        return if (origin == world && !noosphere){
            SpellAction.Result(
                Spell(target, origin, world, coords, false),
                //don't consume amethyst if trying to teleport to the same dimension you're already in
                0,
                listOf(ParticleSpray.cloud(target.pos, 2.0))
            )
        } else {
            SpellAction.Result(
                Spell(target, origin, world, coords, noosphere),
                cost,
                listOf(ParticleSpray.cloud(target.pos, 2.0))
            )
        }
    }

    private data class Spell(var target: LivingEntity, val origin: ServerWorld, val destination: ServerWorld, val coords: Vec3d, val noosphere: Boolean) : RenderedSpell {
        override fun cast(ctx: CastingEnvironment) {
            var x = coords.x
            var y = floor(coords.y)
            var z = coords.z
            val border = destination.worldBorder
            val compressionFactor = origin.dimension.coordinateScale / destination.dimension.coordinateScale
            x *= compressionFactor
            z *= compressionFactor
            var isFlying = false
            if (target is ServerPlayerEntity){
                val playerTarget = target as ServerPlayerEntity
                isFlying = playerTarget.abilities.flying
                if (target == ctx.caster){
                    DepartureEntry(ctx, origin)
                    val entry = DepartureEntry.getEntry(ctx, destination)
                    if (entry != null){
                        if (entry.isWithinCylinder(Vec3d(x, 0.0, z))){
                            //Oneironaut.LOGGER.info("Found an existing departure, teleporting there.")
                            val entryOrigin = entry.originPos
                            playerTarget.teleport(destination, entryOrigin.x, entryOrigin.y, entryOrigin.z, target.yaw, target.pitch)
                            playerTarget.abilities.flying = isFlying
                            playerTarget.sendAbilitiesUpdate()
                            return
                        }
                    }
                    //Oneironaut.LOGGER.info("No existing departure found, behaving as normal.")
                }
                playerTarget.sendAbilitiesUpdate()
            }
            var floorSpot : BlockPos = BlockPos(Vec3i.ZERO)
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
            var scanPoint = BlockPos(Vec3d(x, y+1, z).toVec3i())
            if (!isFlying){
                while(!isSolid(destination, scanPoint)){
                    //ctx.caster.sendMessage(Text.of(destination.getBlockState(scanPoint).block.toString()))
                    scanPoint = BlockPos(Vec3d(x, scanPoint.y.toDouble() - 1, z).toVec3i())
                    //check for void
                    if (scanPoint.y < destination.bottomY || isUnsafe(destination, scanPoint, false)){
                        //ctx.caster.sendMessage(Text.of("scanpoint: ${scanPoint.y}, bottomY: ${destination.bottomY}, safety: ${isUnsafe(destination, scanPoint)}"))
                        scanPoint = BlockPos(Vec3d(x, y+1, z).toVec3i())
                        break
                    }
                }
            }
            //try to avoid putting your head in solid rock or something
            while(isUnsafe(destination, scanPoint, true) || isSolid(destination, scanPoint)){
                //ctx.caster.sendMessage(Text.of(destination.getBlockState(scanPoint).block.toString()))
                scanPoint = BlockPos(Vec3d(x, scanPoint.y.toDouble() + 1, z).toVec3i())
                //check for ceiling
                if (destination.getBlockState(scanPoint).block.equals(Blocks.BEDROCK)){
                    break
                }
            }
            if (!(destination.getBlockState(scanPoint).block.equals(Blocks.BEDROCK))){
                if (isUnsafe(destination, BlockPos(Vec3d(x, (scanPoint.y - 1).toDouble(), z).toVec3i()), true) || !isSolid(destination, BlockPos(Vec3d(x, (scanPoint.y - 1).toDouble(), z).toVec3i()))){
                    y = (scanPoint.y + 1).toDouble()
                    if (!isSolid(destination, BlockPos(Vec3d(x, (scanPoint.y - 1).toDouble(), z).toVec3i()))){
                        floorNeeded = true
                        floorSpot = BlockPos(Vec3d(x, (scanPoint.y - 1).toDouble(), z).toVec3i())
                    }
                }
                y = (scanPoint.y).toDouble()
            }
            val colorizer = ctx.pigment
            if (origin == destination){
                ctx.caster!!.sendMessage(Text.translatable("hexcasting.spell.oneironaut:dimteleport.samedim"));
            } else {
                if (target is ServerPlayerEntity){
                    val playerTarget = target as ServerPlayerEntity
                    playerTarget.teleport(destination, x, y, z, target.yaw, target.pitch)
                    playerTarget.abilities.flying = isFlying
                    playerTarget.sendAbilitiesUpdate()

                    //FabricDimensions.teleport(target, destination, TeleportTarget(Vec3d(x, y, z), Vec3d.ZERO, target.yaw, target.pitch))
                    if (noosphere){
                        target.addStatusEffect(StatusEffectInstance(StatusEffects.NAUSEA, 200))
                        target.addStatusEffect(StatusEffectInstance(StatusEffects.BLINDNESS, 100))
                    }
                    if (floorNeeded && !isFlying){
                        destination.setBlockState((floorSpot), HexBlocks.CONJURED_BLOCK.defaultState)
                        BlockConjured.setColor(destination, floorSpot, colorizer)
                    }
                } else {
                    FabricDimensions.teleport(target, destination, TeleportTarget(Vec3d(x, y, z), target.velocity, target.yaw, target.pitch))
                    if (floorNeeded && !isFlying){
                        destination.setBlockState((floorSpot), HexBlocks.CONJURED_BLOCK.defaultState)
                        BlockConjured.setColor(destination, floorSpot, colorizer)
                    }
                }
                if (!isFlying){
                    target.addStatusEffect(StatusEffectInstance(StatusEffects.SLOW_FALLING, 1200))
                }
            }
        }
    }
}