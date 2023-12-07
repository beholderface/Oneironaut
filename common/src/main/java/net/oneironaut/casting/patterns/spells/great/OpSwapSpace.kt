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
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.pow

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

        ctx.assertVecInRange(originCuboidCorner1)
        ctx.assertVecInRange(originCuboidCorner2)
        //ctx.assertVecInRange(destCuboidCorner1)
        //ctx.assertVecInRange(destCuboidCorner2)

        ctx.caster.server?.worlds?.forEach {
            if (it.registryKey.value.toString() == dimKey){
                destWorld = it;
                destWorldKey = it.registryKey
            }
        }

        val originCuboidDimensions = Vec3i(abs(originCuboidCorner1.x - originCuboidCorner2.x) + 1,abs(originCuboidCorner1.y - originCuboidCorner2.y) + 1,abs(originCuboidCorner1.z - originCuboidCorner2.z) + 1)
        val destCuboidDimensions = Vec3i(abs(destCuboidCorner1.x - destCuboidCorner2.x) + 1,abs(destCuboidCorner1.y - destCuboidCorner2.y) + 1,abs(destCuboidCorner1.z - destCuboidCorner2.z) + 1)
        if (originCuboidDimensions != destCuboidDimensions){
            throw MishapBadCuboid()
        }
        val originBox = Box(BlockPos(originCuboidCorner1), BlockPos(originCuboidCorner2))
        val destBox = Box(BlockPos(destCuboidCorner1), BlockPos(destCuboidCorner2))
        //cost is equal to the volume of the box in m^3 in dust, plus 10 charged
        val boxVolume = (originCuboidDimensions.x * originCuboidDimensions.y * originCuboidDimensions.z)
        val cost = boxVolume + 100
        //ctx.caster.sendMessage(Text.of(cost.toString()))

        if (!HexConfig.server().canTeleportInThisDimension(destWorldKey))
            throw MishapLocationTooFarAway(Vec3d.ZERO, "bad_dimension")
        if (!HexConfig.server().canTeleportInThisDimension(originWorldKey))
            throw MishapLocationTooFarAway(Vec3d.ZERO, "bad_dimension")

        return Triple(
            Spell(originWorld, originBox, destWorld, destBox, originCuboidDimensions, boxVolume),
            cost * MediaConstants.DUST_UNIT,
            listOf(ParticleSpray.cloud(ctx.caster.pos, 2.0))
        )
    }
    private data class Spell(val originDim : ServerWorld, val originBox : Box,
                             val destDim : ServerWorld, val destBox : Box,
                             val dimensions : Vec3i, val volume : Int) : RenderedSpell {
        override fun cast(ctx: CastingContext) {
            val originLowerCorner = BlockPos(originBox.minX, originBox.minY, originBox.minZ)
            val destLowerCorner = BlockPos(destBox.minX, destBox.minY, destBox.minZ)
            var transferOffset = Vec3i.ZERO
            var originPointState = originDim.getBlockState(originLowerCorner)
            var destPointState = destDim.getBlockState(destLowerCorner)
            /*for (i in 1 .. volume){
                ctx.caster.sendMessage(Text.of("$i: " + originLowerCorner.add(transferOffset).toString()))
                if (!((originPointState.block.hardness == -1f || destPointState.block.hardness == -1f) || (originPointState.hasBlockEntity() ||destPointState.hasBlockEntity()))){
                    originDim.setBlockState(originLowerCorner.add(transferOffset), destPointState)
                    destDim.setBlockState(destLowerCorner.add(transferOffset), originPointState)
                }
                transferOffset = Vec3i(i % dimensions.x, (i / (dimensions.x * dimensions.z)), (i / dimensions.x) % (dimensions.x))
                originPointState = originDim.getBlockState(originLowerCorner.add(transferOffset))
                destPointState = destDim.getBlockState(destLowerCorner.add(transferOffset))
            }*/
            for (i in 0 .. dimensions.x - 1){
                for (j in 0 .. dimensions.y - 1){
                    for (k in 0 .. dimensions.z - 1){
                        transferOffset = Vec3i(i, j, k)
                        originPointState = originDim.getBlockState(originLowerCorner.add(transferOffset))
                        destPointState = destDim.getBlockState(destLowerCorner.add(transferOffset))
                        if (!((originPointState.block.hardness == -1f || destPointState.block.hardness == -1f) || (originPointState.hasBlockEntity() ||destPointState.hasBlockEntity()))){
                            originDim.setBlockState(originLowerCorner.add(transferOffset), destPointState)
                            destDim.setBlockState(destLowerCorner.add(transferOffset), originPointState)
                        }
                    }
                }
            }
            //ctx.caster.sendMessage(Text.of("Origin: ${originDim.registryKey.value.toString()}, ${originBox.toString()}"))
            //ctx.caster.sendMessage(Text.of("Destination: ${destDim.registryKey.value.toString()}, ${destBox.toString()}"))
            //ctx.caster.sendMessage((Text.of(Box(originCorner1, originCorner2).toString())))
        }
    }
}