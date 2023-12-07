package net.oneironaut.casting.patterns.spells

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.spell.ParticleSpray
import at.petrak.hexcasting.api.spell.RenderedSpell
import at.petrak.hexcasting.api.spell.SpellAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getVec3
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.common.blocks.BlockConjured
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.util.math.BlockPos

class OpSplatoon : SpellAction {
    override val argc = 1
    //override val mediaCost = 1 * MediaConstants.DUST_UNIT
    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>>? {
        val target = args.getVec3(0, argc)
        ctx.assertVecInRange(target)

        return Triple(
            Spell(BlockPos(target)),
            MediaConstants.DUST_UNIT,
            listOf(ParticleSpray.cloud(target, 2.0))
        )
    }

    private data class Spell(val target: BlockPos) : RenderedSpell {
        override fun cast(ctx: CastingContext) {
            val colorizer = IXplatAbstractions.INSTANCE.getColorizer(ctx.caster)
            if (ctx.world.getBlockState(target).block is BlockConjured) {
                BlockConjured.setColor(ctx.world, target, colorizer)
            }
        }

    }
}