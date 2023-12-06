package net.oneironaut.casting.patterns.spells.great

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.mod.HexConfig
import at.petrak.hexcasting.api.spell.*
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.Vec3Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.spell.mishaps.MishapLocationTooFarAway
import at.petrak.hexcasting.api.utils.asLongArray
import at.petrak.hexcasting.api.utils.downcast
import at.petrak.hexcasting.api.utils.vecFromNBT
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.property.Properties
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i
import net.oneironaut.casting.mishaps.MishapBadCuboid
import net.oneironaut.getDimIota
import kotlin.math.abs
import kotlin.math.max

class OpSwapSpace : SpellAction {
    override val argc = 3
    override val isGreat = true
    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>>? {
        val destination = args.getDimIota(2, argc)
        val dimKey = destination.serialize().downcast(NbtCompound.TYPE).getString("dim_key")
        var destWorld = ctx.world
        var destWorldKey = destWorld.registryKey
        val originWorld = ctx.world
        val originWorldKey = originWorld.registryKey
        val originWorldCuboid = args.getList(0, argc)
        if (originWorldCuboid.size() != 2){
            throw MishapInvalidIota(args[0], 2, Text.literal("List is improperly sized."))
        } else if ((originWorldCuboid.getAt(0).type != Vec3Iota.TYPE) || originWorldCuboid.getAt(1).type != Vec3Iota.TYPE){
            throw MishapInvalidIota(args[0], 2, Text.literal("List does not contain two vectors."))
        }
        val destWorldCuboid = args.getList(1, argc)
        if (destWorldCuboid.size() != 2){
            throw MishapInvalidIota(args[1], 1, Text.literal("List is improperly sized."))
        } else if ((destWorldCuboid.getAt(0).type != Vec3Iota.TYPE) || destWorldCuboid.getAt(1).type != Vec3Iota.TYPE){
            throw MishapInvalidIota(args[1], 1, Text.literal("List does not contain two vectors."))
        }
        val originCuboidCorner1 = BlockPos(vecFromNBT(originWorldCuboid.getAt(0).serialize().asLongArray))
        val originCuboidCorner2 = BlockPos(vecFromNBT(originWorldCuboid.getAt(1).serialize().asLongArray))
        val destCuboidCorner1 = BlockPos(vecFromNBT(destWorldCuboid.getAt(0).serialize().asLongArray))
        val destCuboidCorner2 = BlockPos(vecFromNBT(destWorldCuboid.getAt(1).serialize().asLongArray))

        ctx.caster.server?.worlds?.forEach {
            if (it.registryKey.value.toString() == dimKey){
                destWorld = it;
                destWorldKey = it.registryKey
            }
        }

        val originCuboidDimensions = Vec3i(abs(originCuboidCorner1.x - originCuboidCorner2.x),abs(originCuboidCorner1.y - originCuboidCorner2.y),abs(originCuboidCorner1.z - originCuboidCorner2.z))
        val destCuboidDimensions = Vec3i(abs(destCuboidCorner1.x - destCuboidCorner2.x),abs(destCuboidCorner1.y - destCuboidCorner2.y),abs(destCuboidCorner1.y - destCuboidCorner2.y))
        if (originCuboidDimensions != destCuboidDimensions){
            throw MishapBadCuboid()
        }
        //cost is equal to the volume of the box in m^3 in dust, plus 10 charged
        val cost = (max(originCuboidDimensions.x, 1) * max(originCuboidDimensions.y, 1) * max(originCuboidDimensions.z, 1)) + 100

        if (!HexConfig.server().canTeleportInThisDimension(destWorldKey))
            throw MishapLocationTooFarAway(Vec3d.ZERO, "bad_dimension")
        if (!HexConfig.server().canTeleportInThisDimension(originWorldKey))
            throw MishapLocationTooFarAway(Vec3d.ZERO, "bad_dimension")

        return Triple(
            Spell(originWorld, originCuboidCorner1, originCuboidCorner2, destWorld, destCuboidCorner1, destCuboidCorner2),
            cost * MediaConstants.DUST_UNIT,
            listOf(ParticleSpray.cloud(ctx.caster.pos, 2.0))
        )
    }
    private data class Spell(val originDim : ServerWorld, val originCorner1 : BlockPos, val originCorner2 : BlockPos,
                             val destDim : ServerWorld, val destCorner1 : BlockPos, val destCorner2 : BlockPos) : RenderedSpell {
        override fun cast(ctx: CastingContext) {
            //less useful than I first thought
            //val originDimStateBox = originDim.getStatesInBox(Box(originCorner1, originCorner2)).toList()
            //val destDimStateBox = destDim.getStatesInBox(Box(destCorner1, destCorner2)).toList()
            ctx.caster.sendMessage(Text.of("Origin: ${originDim.registryKey.value.toString()}, ${originCorner1.toString()}, ${originCorner2.toString()}"))
            ctx.caster.sendMessage(Text.of("Destination: ${destDim.registryKey.value.toString()}, ${destCorner1.toString()}, ${destCorner2.toString()}"))
        }
    }
}