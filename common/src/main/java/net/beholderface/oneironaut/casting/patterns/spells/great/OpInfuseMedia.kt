package net.beholderface.oneironaut.casting.patterns.spells.great

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.beholderface.oneironaut.Oneironaut
import net.beholderface.oneironaut.casting.mishaps.MishapUninfusable
import net.beholderface.oneironaut.getInfuseResult
import net.beholderface.oneironaut.toVec3i

class OpInfuseMedia : SpellAction {
    override val argc = 1
    override fun execute(args: List<Iota>, ctx: CastingEnvironment): SpellAction.Result {
        val target = args.getVec3(0, argc)
        ctx.assertVecInRange(target)
        val targetType = ctx.world.getBlockState(BlockPos(target.toVec3i()))
        val (result, cost, advancement) = getInfuseResult(targetType, ctx.world)
        if (result == Blocks.BARRIER.defaultState){
            throw MishapUninfusable.of(BlockPos(target.toVec3i())/*, "media"*/)
        }
        return SpellAction.Result(
            Spell(BlockPos(target.toVec3i()), result, cost, advancement),
            cost * MediaConstants.DUST_UNIT,
            listOf(ParticleSpray.cloud(target, 2.0))
        )
    }
    private data class Spell(val target: BlockPos, var result: BlockState, val cost: Int, val advancement : String?) : RenderedSpell {
        val debugmessages = false
        override fun cast(ctx: CastingEnvironment) {
            ctx.world.setBlockState(target, result)
            /*if (advancement != null){
                val command = "advancement grant ${ctx.caster.name.string} only $advancement"
                Oneironaut.boolLogger("Executing command $command", debugmessages)
                ctx.world.server.commandManager.executeWithPrefix(ctx.world.server.commandSource.withSilent(), command)
            } else {
                Oneironaut.boolLogger("No advancement found in recipe.", debugmessages)
            }*/
        }
    }
}