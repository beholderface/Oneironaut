package net.oneironaut.casting.patterns.spells

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.spell.ParticleSpray
import at.petrak.hexcasting.api.spell.RenderedSpell
import at.petrak.hexcasting.api.spell.SpellAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getVec3
import at.petrak.hexcasting.api.spell.iota.Iota
import net.minecraft.block.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.oneironaut.getBoxCorners
import net.oneironaut.getPositionsInCuboid
import net.oneironaut.registry.OneironautBlockRegistry

//processCellPattern will eventually be used to tell the spell that it should check the automaton cuboid for patterns that trigger special effects
class OpAdvanceAutomaton(val processCellPattern : Boolean) : SpellAction {
    override val argc = 2
    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>> {
        val box = Box(args.getVec3(0, argc), args.getVec3(1, argc))
        val corners = getBoxCorners(box)
        for(c in corners){
            ctx.assertVecInRange(c)
        }
        val cost = (box.xLength * box.yLength * box.zLength * (MediaConstants.DUST_UNIT * 0.1)).toInt()
        return Triple(
            Spell(box),
            cost,
            listOf(ParticleSpray.cloud(box.center, 2.0))
        )
    }
    private data class Spell(val box : Box) : RenderedSpell {
        override fun cast(ctx: CastingContext) {
            val world = ctx.world
            val corner1 = BlockPos(box.minX, box.minY, box.minZ)
            val corner2 = BlockPos(box.maxX, box.maxY, box.maxZ)
            val positions = getPositionsInCuboid(corner1, corner2)
            val cellsToKill : MutableList<BlockPos> = mutableListOf()
            val cellsToSpawn : MutableList<BlockPos> = mutableListOf()
            val cellsToVerify : MutableList<BlockPos> = mutableListOf()
            for(pos in positions){
                val isCell = world.getBlockState(pos).block.equals(OneironautBlockRegistry.CELL.get())
                val isAir = world.getBlockState(pos).isAir
                val neighbors = getPositionsInCuboid(pos.add(1, 1, 1), pos.add(-1, -1, -1), pos)
                var foundCells = 0
                for (neighbor in neighbors){
                    if (world.getBlockState(neighbor).block.equals(OneironautBlockRegistry.CELL.get())){
                        foundCells++
                    }
                }
                if (isCell){
                    if (foundCells in 5..7){
                        //cell survives, mark it as having done so
                        cellsToVerify.add(pos)
                    } else {
                        //mark cell as submissive and killable
                        cellsToKill.add(pos)
                    }
                } else if (isAir && foundCells == 6){
                    //mark position as free real estate
                    cellsToSpawn.add(pos)
                    cellsToVerify.add(pos)
                }
            }
            for (pos in cellsToKill){
                world.setBlockState(pos, Blocks.AIR.defaultState)
            }
            for (pos in cellsToSpawn){
                world.setBlockState(pos, OneironautBlockRegistry.CELL.get().defaultState)
            }
            for (pos in cellsToVerify){
                val mayBE = world.getBlockEntity(pos, OneironautBlockRegistry.CELL_ENTITY.get())
                if (mayBE.isPresent){
                    if (!mayBE.get().verified){
                        mayBE.get().verified = true
                    }
                }
            }
        }
    }
}