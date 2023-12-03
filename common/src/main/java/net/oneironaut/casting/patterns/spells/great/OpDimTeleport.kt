package net.oneironaut.casting.patterns.spells.great

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.mod.HexConfig
import at.petrak.hexcasting.api.spell.ParticleSpray
import at.petrak.hexcasting.api.spell.RenderedSpell
import at.petrak.hexcasting.api.spell.SpellAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.NullIota
import at.petrak.hexcasting.api.spell.mishaps.MishapLocationTooFarAway
import at.petrak.hexcasting.api.utils.downcast
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.World
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d
import net.oneironaut.getDimIota
import org.apache.commons.lang3.ObjectUtils.Null

class OpDimTeleport : SpellAction {
    override val argc = 1
    override val isGreat = true
    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>>? {
        val target = ctx.caster
        val origin = target.getWorld()
        val coords = target.pos
        var world = target.getWorld()
        var worldKey = target.getWorld().registryKey
        var noosphere = false
        if (args[0] is NullIota){
            //TODO: make this go to noosphere eventually
            noosphere = true;
        } else {
            val destination = args.getDimIota(0, argc)
            val dimKey = destination.serialize().downcast(NbtCompound.TYPE).getString("dim_key")
            target.server.worlds.forEach {
                if (it.registryKey.value.toString() == dimKey){
                    world = it;
                    worldKey = it.registryKey
                }
            }
        }


        /*if (!HexConfig.server().canTeleportInThisDimension(ctx.world.registryKey))
            throw MishapLocationTooFarAway(coords, "bad_dimension")*/
        if (!HexConfig.server().canTeleportInThisDimension(worldKey))
            throw MishapLocationTooFarAway(coords, "bad_dimension")
        return if (origin == world && !noosphere){
            Triple(
                Spell(target, origin, world, coords, target.yaw, target.pitch, false),
                0 * MediaConstants.CRYSTAL_UNIT,
                listOf(ParticleSpray.cloud(target.pos, 2.0))
            )
        } else {
            Triple(
                Spell(target, origin, world, coords, target.yaw, target.pitch, noosphere),
                20 * MediaConstants.CRYSTAL_UNIT,
                listOf(ParticleSpray.cloud(target.pos, 2.0))
            )
        }
    }

    private data class Spell(val target: ServerPlayerEntity, val origin : ServerWorld, val destination : ServerWorld, val coords : Vec3d, val yaw : Float, val pitch : Float, val noosphere : Boolean) : RenderedSpell {
        override fun cast(ctx: CastingContext) {
            var x = coords.x
            var y = coords.y
            var z = coords.z
            val compressionFactor = origin.dimension.coordinateScale / destination.dimension.coordinateScale
            x *= compressionFactor
            z *= compressionFactor
            if (destination.bottomY > coords.y - 5.0){
                y = ((destination.bottomY + 5).toDouble())
            }
            if (noosphere) {
                ctx.caster.sendMessage(Text.translatable("hexcasting.spell.oneironaut:dimteleport.comingsoon"));
            } else if (origin == destination){
                ctx.caster.sendMessage(Text.translatable("hexcasting.spell.oneironaut:dimteleport.samedim"));
            }else {
                target.teleport(destination, x, y, z, 0.0F, 0.0F)
                target.addStatusEffect(StatusEffectInstance(StatusEffects.SLOW_FALLING, 1200))
            }
            //ctx.caster.sendMessage(Text.of(world.bottomY.toString()));
        }
    }
}