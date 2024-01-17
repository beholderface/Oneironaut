package net.oneironaut.casting.patterns.spells

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.spell.*
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapLocationTooFarAway
import net.minecraft.block.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.oneironaut.casting.cell.CellSpellManager
import net.oneironaut.casting.cell.ICellSpell
import net.oneironaut.casting.mishaps.MishapUnhappySlime
import net.oneironaut.getBoxCorners
import net.oneironaut.getPositionsInCuboid
import net.oneironaut.registry.OneironautBlockRegistry

//processCellPattern will eventually be used to tell the spell that it should check the automaton cuboid for patterns that trigger special effects
class OpTriggerAutomaton(val processCellPattern : Boolean) : SpellAction {
    override val argc = 3
    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>> {
        val box = Box(BlockPos(args.getVec3(0, argc)), BlockPos(args.getVec3(1, argc)))
        val corners = getBoxCorners(box)
        for(c in corners){
            ctx.assertVecInRange(c)
        }
        var cellSpellCost = 0
        val cellSpell : com.mojang.datafixers.util.Pair<BlockPos, ICellSpell>?
        if (processCellPattern){
            advanceAutomaton(ctx, box)
            //Oneironaut.LOGGER.info("checking for pattern")
            cellSpell = CellSpellManager.findPattern(ctx, box)
            if (cellSpell != null){
                val cellSpellConditions = cellSpell.second.evaluateConditions(ctx, args, box);
                cellSpellCost = cellSpellConditions.first
                val mishap = cellSpellConditions.second
                //mishap if the cell spell says to mishap
                if (mishap != null){
                    throw MishapUnhappySlime(mishap)
                }
                //Oneironaut.LOGGER.info("pattern found: " + cellSpell.second.translationKey)
            } else {
                //Oneironaut.LOGGER.info("no pattern found")
            }
        } else {
            cellSpell = null
        }
        val cost = ((box.xLength * box.yLength * box.zLength * (MediaConstants.DUST_UNIT * 0.1)) + cellSpellCost).toInt()
        return if (cellSpell == null){
            Triple(
                Spell(box, null, null, args.getList(2, argc).toList(), processCellPattern),
                cost,
                listOf(ParticleSpray.cloud(box.center, 2.0))
            )
        } else {
            Triple(
                Spell(box, cellSpell.first, cellSpell.second, args.getList(2, argc).toList(), processCellPattern),
                cost,
                listOf(ParticleSpray.cloud(box.center, 2.0))
            )
        }
    }
    private data class Spell(val box : Box, val corner : BlockPos?, val cellSpell : ICellSpell?,
                             val args : List<Iota>?, val execute : Boolean) : RenderedSpell {
        override fun cast(ctx: CastingContext) {
            if (corner != null && cellSpell != null && execute){
                cellSpell.execute(ctx, args, box, corner)
            } else if (!execute) {
                advanceAutomaton(ctx, box)
            }
        }
    }
}