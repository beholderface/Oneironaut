package net.beholderface.oneironaut.casting.patterns.spells.great

import at.petrak.hexcasting.api.block.circle.BlockCircleComponent
import at.petrak.hexcasting.api.block.circle.BlockEntityAbstractImpetus
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.mod.HexConfig
import at.petrak.hexcasting.api.mod.HexTags
import at.petrak.hexcasting.api.spell.ParticleSpray
import at.petrak.hexcasting.api.spell.RenderedSpell
import at.petrak.hexcasting.api.spell.SpellAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getList
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.Vec3Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.spell.mishaps.MishapLocationTooFarAway
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.beholderface.oneironaut.*
//import net.beholderface.oneironaut.*
import net.beholderface.oneironaut.casting.mishaps.MishapBadCuboid
import net.beholderface.oneironaut.casting.mishaps.MishapNoNoosphere
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.Entity
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.world.ChunkTicketType
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.*
import net.minecraft.world.TeleportTarget
import net.minecraft.world.chunk.ChunkStatus
import java.lang.Exception
import kotlin.math.abs
import kotlin.math.pow

class OpSwapSpace : SpellAction {
    override val argc = 3
    override val isGreat = true
    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>> {
        val destination = args.getDimIota(2, argc)
        val dimKey = destination.dimString
        var destWorld = ctx.world
        var destWorldKey = destWorld.registryKey
        val originWorld = ctx.world
        val originWorldKey = originWorld.registryKey
        val originWorldCuboid = args.getList(0, argc)
        if (originWorldCuboid.size() != 2){
            throw MishapInvalidIota(args[0], 2, Text.translatable("oneironaut.mishap.wrongsizelist"))
        } else if ((originWorldCuboid.getAt(0).type != Vec3Iota.TYPE) || originWorldCuboid.getAt(1).type != Vec3Iota.TYPE){
            throw MishapInvalidIota(args[0], 2, Text.translatable("oneironaut.mishap.twovectorsplease"))
        }
        val destWorldCuboid = args.getList(1, argc)
        if (destWorldCuboid.size() != 2){
            throw MishapInvalidIota(args[1], 1, Text.translatable("oneironaut.mishap.wrongsizelist"))
        } else if ((destWorldCuboid.getAt(0).type != Vec3Iota.TYPE) || destWorldCuboid.getAt(1).type != Vec3Iota.TYPE){
            throw MishapInvalidIota(args[1], 1, Text.translatable("oneironaut.mishap.twovectorsplease"))
        }

        val originCuboidCorner1 = BlockPos((originWorldCuboid.getAt(0) as Vec3Iota).vec3)
        val originCuboidCorner2 = BlockPos((originWorldCuboid.getAt(1) as Vec3Iota).vec3)
        val destCuboidCorner1 = BlockPos((destWorldCuboid.getAt(0) as Vec3Iota).vec3)
        val destCuboidCorner2 = BlockPos((destWorldCuboid.getAt(1) as Vec3Iota).vec3)
        val originBox = Box(BlockPos(originCuboidCorner1), BlockPos(originCuboidCorner2))
        val destBox = Box(BlockPos(destCuboidCorner1), BlockPos(destCuboidCorner2))
        /*val boxCorners = arrayOf(Vec3d(originBox.minX, originBox.minY, originBox.minZ), Vec3d(originBox.maxX, originBox.minY, originBox.minZ),
            Vec3d(originBox.maxX, originBox.maxY, originBox.minZ), Vec3d(originBox.maxX, originBox.maxY, originBox.maxZ),
            Vec3d(originBox.minX, originBox.maxY, originBox.maxZ), Vec3d(originBox.minX, originBox.minY, originBox.maxZ),
            Vec3d(originBox.maxX, originBox.minY, originBox.maxZ), Vec3d(originBox.minX, originBox.maxY, originBox.minZ)
            )*/

        ctx.caster.server?.worlds?.forEach {
            if (it.registryKey.value.toString() == dimKey){
                destWorld = it
                destWorldKey = it.registryKey
            }
        }

        val originCuboidDimensions = Vec3i(abs(originCuboidCorner1.x - originCuboidCorner2.x) + 1,abs(originCuboidCorner1.y - originCuboidCorner2.y) + 1,abs(originCuboidCorner1.z - originCuboidCorner2.z) + 1)
        val destCuboidDimensions = Vec3i(abs(destCuboidCorner1.x - destCuboidCorner2.x) + 1,abs(destCuboidCorner1.y - destCuboidCorner2.y) + 1,abs(destCuboidCorner1.z - destCuboidCorner2.z) + 1)
        if (originCuboidDimensions != destCuboidDimensions){
            throw MishapBadCuboid("mismatch")
        }
        val boxVolume = (originCuboidDimensions.x * originCuboidDimensions.y * originCuboidDimensions.z)
        //cost is equal to the volume of the box in (m^3 / 2) in dust, plus 5 charged
        val cost = (boxVolume / 2.0) + 50
        val boxCorners = getBoxCorners(originBox)
        for (corner in boxCorners) {
            ctx.assertVecInRange(corner)
        }
        if (boxVolume > 80.0.pow(3) || originBox.longestAxisLength() > 384 || destBox.longestAxisLength() > 384){
            throw MishapBadCuboid("toobig")
        }
        //ctx.caster.sendMessage(Text.of(cost.toString()))

        if (!HexConfig.server().canTeleportInThisDimension(destWorldKey))
            throw MishapLocationTooFarAway(Vec3d.ZERO, "bad_dimension")
        if (!HexConfig.server().canTeleportInThisDimension(originWorldKey))
            throw MishapLocationTooFarAway(Vec3d.ZERO, "bad_dimension")

        //require that one end of the transfer be the noosphere if config is set to require that
        if (!(destWorldKey.value.toString() == "oneironaut:noosphere" || originWorldKey.value.toString() == "oneironaut:noosphere")
            && OneironautConfig.server.swapRequiresNoosphere){
            throw MishapNoNoosphere()
        }

        return Triple(
            Spell(originWorld, originBox, destWorld, destBox, originCuboidDimensions, boxVolume),
            (cost.toInt()) * MediaConstants.DUST_UNIT,
            listOf(ParticleSpray.cloud(ctx.caster.pos, 2.0))
        )
    }
    private data class Spell(val originDim : ServerWorld, val originBox : Box,
                             val destDim : ServerWorld, val destBox : Box,
                             val dimensions : Vec3i, val volume : Int) : RenderedSpell {
        override fun cast(ctx: CastingContext) {
            val originLowerCorner = BlockPos(originBox.minX, originBox.minY, originBox.minZ)
            val destLowerCorner = BlockPos(destBox.minX, destBox.minY, destBox.minZ)
            val originEntities = originDim.getOtherEntities(null, originBox) {
                (!it.isLiving || it.type.isIn(getEntityTagKey(Identifier.of("oneironaut","living_interchange_whitelist")!!))) && it.canUsePortals() && !it.type.isIn(HexTags.Entities.CANNOT_TELEPORT)
            }
            val originEntityMap = HashMap<Entity, Vec3d>()
            /*for (entity in originEntities){
                originEntityMap[entity] = originBox.minCorner().relativize(entity.pos)
            }*/

            destDim.chunkManager.getChunk((destBox.center.x / 16).toInt(),
                (destBox.center.z / 16).toInt(), ChunkStatus.FULL, true)
            val destEntities = destDim.getOtherEntities(null, destBox) {
                (!it.isLiving || it.type.isIn(getEntityTagKey(Identifier.of("oneironaut","living_interchange_whitelist")!!))) && it.canUsePortals() && !it.type.isIn(HexTags.Entities.CANNOT_TELEPORT)
            }
            //destDim.loadEntities(destEntities.stream())
            val destEntityMap = HashMap<Entity, Vec3d>()
            /*for (entity in destEntities){
                destEntityMap[entity] = destBox.minCorner().relativize(entity.pos)
            }*/
            var transferOffset: Vec3i?
            var originDimPos: BlockPos?
            var destDimPos: BlockPos?
            var originPointState: BlockState?
            var destPointState: BlockState?
            var originBE: BlockEntity?
            var originBEData : NbtCompound?
            var destBE: BlockEntity?
            var destBEData : NbtCompound?
            val flags = 3//0.and(Block.REDRAW_ON_MAIN_THREAD).and(Block.MOVED).and(Block.NOTIFY_LISTENERS).and(Block.FORCE_STATE)
            val maxdepth = 0
            val isCircle = ctx.spellCircle != null
            for (i in 0 until dimensions.x){
                for (j in 0 until dimensions.y){
                    for (k in 0 until dimensions.z){
                        transferOffset = Vec3i(i, j, k)
                        originDimPos = originLowerCorner.add(transferOffset)
                        destDimPos = destLowerCorner.add(transferOffset)
                        originPointState = originDim.getBlockState(originDimPos)
                        if (originPointState.block is BlockCircleComponent){
                            try {
                                val attemptedState = originPointState.with(BlockCircleComponent.ENERGIZED, false)
                                assert(attemptedState != null)
                                originPointState = attemptedState
                            } catch (e : Exception){
                                //no-op
                            }
                        }
                        destPointState = destDim.getBlockState(destDimPos)
                        if (destPointState.block is BlockCircleComponent){
                            try {
                                val attemptedState = destPointState.with(BlockCircleComponent.ENERGIZED, false)
                                assert(attemptedState != null)
                                destPointState = attemptedState
                            } catch (e : Exception){
                                //no-op
                            }
                        }
                        originBE = originDim.getBlockEntity(originDimPos)
                        destBE = destDim.getBlockEntity(destDimPos)
                        var newBE : BlockEntity?
                        val breakingAllowed = IXplatAbstractions.INSTANCE.isBreakingAllowed(originDim, originDimPos, originPointState, ctx.caster) &&
                                IXplatAbstractions.INSTANCE.isBreakingAllowed(destDim, destDimPos, destPointState, ctx.caster)
                        if (!((originPointState!!.block.hardness == -1f || destPointState!!.block.hardness == -1f)
                                    || ((originPointState.hasBlockEntity() || destPointState.hasBlockEntity()) && !OneironautConfig.server.swapSwapsBEs)
                                    || !breakingAllowed)){
                            if (destBE != null){
                                var state = destBE.cachedState
                                if (destBE is BlockEntityAbstractImpetus){
                                    state = resetImpetus(state, destBE)
                                }
                                destBEData = destBE.createNbt()
                                originDim.removeBlockEntity(originDimPos)
                                originDim.setBlockState(originDimPos, state, flags, maxdepth)
                                newBE = originDim.getBlockEntity(originDimPos)
                                newBE?.readNbt(destBEData)
                                newBE?.markDirty()
                            } else {
                                originDim.removeBlockEntity(originDimPos)
                                originDim.setBlockState(originDimPos, destPointState, flags, maxdepth)
                            }
                            if (originBE != null){
                                var state = originBE.cachedState
                                if (originBE is BlockEntityAbstractImpetus){
                                    state = resetImpetus(state, originBE)
                                }
                                originBEData = originBE.createNbt()
                                destDim.removeBlockEntity(destDimPos)
                                destDim.setBlockState(destDimPos, state, flags, maxdepth)
                                newBE = destDim.getBlockEntity(destDimPos)
                                newBE?.readNbt(originBEData)
                                newBE?.markDirty()
                            } else {
                                destDim.removeBlockEntity(destDimPos)
                                destDim.setBlockState(destDimPos, originPointState, flags, maxdepth)
                            }
                        }
                    }
                }
            }
            for (i in 0 until dimensions.x){
                for (j in 0 until dimensions.y){
                    for (k in 0 until dimensions.z){
                        transferOffset = Vec3i(i, j, k)
                        originDimPos = originLowerCorner.add(transferOffset)
                        destDimPos = destLowerCorner.add(transferOffset)
                        originPointState = originDim.getBlockState(originDimPos)
                        destPointState = destDim.getBlockState(destDimPos)
                        originPointState.updateNeighbors(originDim, originDimPos, 3, 512)
                        destPointState.updateNeighbors(destDim, destDimPos, 3, 512)
                    }
                }
            }
            for (pair in originEntityMap){
                val entity = pair.key
                val offset = pair.value
                FabricDimensions.teleport(entity, destDim, TeleportTarget(destBox.minCorner().add(offset), entity.velocity, entity.headYaw, entity.pitch))
            }
            for (pair in destEntityMap){
                val entity = pair.key
                val offset = pair.value
                FabricDimensions.teleport(entity, originDim, TeleportTarget(originBox.minCorner().add(offset), entity.velocity, entity.headYaw, entity.pitch))
            }
            //without this the impetus gets stuck in an active state and can't be used again without breaking and replacing it
            /*if (circleMovingSelf){
                val circle = ctx.spellCircle!!
                val impetusPos = newImpetusPos
                val impetusBE = destDim.getBlockEntity(impetusPos)
                Oneironaut.LOGGER.info("Spell circle at $impetusPos interchanging itself, attempting to reset activation state.")
                if (impetusBE is BlockEntityAbstractImpetus){
                    val originalCompound = impetusBE.createNbt()
                    val freshCompound = originalCompound.copy()
                    freshCompound.remove(BlockEntityAbstractImpetus.TAG_ACTIVATOR)
                    freshCompound.remove(BlockEntityAbstractImpetus.TAG_COLORIZER)
                    freshCompound.remove(BlockEntityAbstractImpetus.TAG_NEXT_BLOCK)
                    freshCompound.remove(BlockEntityAbstractImpetus.TAG_FOUND_ALL)
                    freshCompound.remove(BlockEntityAbstractImpetus.TAG_TRACKED_BLOCKS)
                    impetusBE.readNbt(freshCompound)
                    destDim.setBlockState(impetusPos, destDim.getBlockState(impetusPos).with(BlockCircleComponent.ENERGIZED, false))
                }
            }*/
            //ctx.caster.sendMessage(Text.of("Origin: ${originDim.registryKey.value.toString()}, ${originBox.toString()}"))
            //ctx.caster.sendMessage(Text.of("Destination: ${destDim.registryKey.value.toString()}, ${destBox.toString()}"))
            //ctx.caster.sendMessage((Text.of(Box(originCorner1, originCorner2).toString())))
        }
    }
}

fun Box.tolerantContains(x: Double, y: Double, z: Double): Boolean {
    return x >= this.minX && x <= this.maxX && y >= this.minY && y <= this.maxY && z >= this.minZ && z <= this.maxZ
}
fun Box.minCorner(): Vec3d {
    return Vec3d(this.minX, this.minY, this.minZ)
}
fun Box.maxCorner(): Vec3d {
    return Vec3d(this.maxX, this.maxY, this.maxZ)
}

private fun resetImpetus(state : BlockState, impetus : BlockEntityAbstractImpetus) : BlockState {
    val originalCompound = impetus.createNbt()
    val freshCompound = originalCompound.copy()
    freshCompound.remove(BlockEntityAbstractImpetus.TAG_ACTIVATOR)
    freshCompound.remove(BlockEntityAbstractImpetus.TAG_COLORIZER)
    freshCompound.remove(BlockEntityAbstractImpetus.TAG_NEXT_BLOCK)
    freshCompound.remove(BlockEntityAbstractImpetus.TAG_FOUND_ALL)
    freshCompound.remove(BlockEntityAbstractImpetus.TAG_TRACKED_BLOCKS)
    impetus.readNbt(freshCompound)
    impetus.markDirty()
    return state.with(BlockCircleComponent.ENERGIZED, false)
}