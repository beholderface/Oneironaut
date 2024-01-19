package net.oneironaut.casting.patterns.spells.great

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.spell.ParticleSpray
import at.petrak.hexcasting.api.spell.RenderedSpell
import at.petrak.hexcasting.api.spell.SpellAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getVec3
import at.petrak.hexcasting.api.spell.iota.Iota
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.oneironaut.casting.mishaps.MishapUninfusable
import net.oneironaut.getInfuseResult

class OpInfuseMedia : SpellAction {
    override val argc = 1
    override val isGreat = true
    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>>? {
        val target = args.getVec3(0, argc)
        ctx.assertVecInRange(target)
        val targetType = ctx.world.getBlockState(BlockPos(target))
        val (result, cost) = getInfuseResult(targetType, ctx)
        if (result == Blocks.BARRIER.defaultState){
            throw MishapUninfusable.of(BlockPos(target)/*, "media"*/)
        }
        return Triple(
            Spell(BlockPos(target), result, cost),
            cost * MediaConstants.DUST_UNIT,
            listOf(ParticleSpray.cloud(target, 2.0))
        )
    }
    private data class Spell(val target: BlockPos, var result: BlockState, val cost: Int) : RenderedSpell {
        override fun cast(ctx: CastingContext) {
            if (ctx.world.getBlockState(target).properties.contains(Properties.WATERLOGGED)){
                result = result.with(Properties.WATERLOGGED, ctx.world.getBlockState(target).get(Properties.WATERLOGGED))
            }
            if (ctx.world.getBlockState(target).properties.contains(Properties.ROTATION)){
                result = result.with(Properties.ROTATION, ctx.world.getBlockState(target).get(Properties.ROTATION))
            }
            if (ctx.world.getBlockState(target).properties.contains(Properties.HORIZONTAL_FACING)){
                result = result.with(Properties.HORIZONTAL_FACING, ctx.world.getBlockState(target).get(Properties.HORIZONTAL_FACING))
            }
            if (ctx.world.getBlockState(target).properties.contains(Properties.HANGING)){
                result = result.with(Properties.HANGING, ctx.world.getBlockState(target).get(Properties.HANGING))
            }
            //ctx.caster.sendMessage(Text.of("$result costs ${cost / 10} charged amethyst"))
            ctx.caster.world.setBlockState(target, result)
        }
    }
}