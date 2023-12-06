package net.oneironaut.casting.mishaps

import at.petrak.hexcasting.api.misc.FrozenColorizer
import at.petrak.hexcasting.api.spell.ParticleSpray
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.Mishap
import net.minecraft.text.Text
import net.minecraft.util.DyeColor
import net.minecraft.util.math.BlockPos
import net.minecraft.world.explosion.Explosion

class MishapBadCuboid() : Mishap() {
    override fun accentColor(ctx: CastingContext, errorCtx: Context): FrozenColorizer = dyeColor(DyeColor.PURPLE)

    override fun particleSpray(ctx: CastingContext) =
        ParticleSpray.burst(ctx.caster.pos, 1.0)

    override fun errorMessage(ctx: CastingContext, errorCtx: Context): Text =
        error("mismatchcubes")

    override fun execute(ctx: CastingContext, errorCtx: Context, stack: MutableList<Iota>) {
        ctx.world.createExplosion(null, ctx.caster.x, ctx.caster.y, ctx.caster.z, 0.25f, Explosion.DestructionType.NONE)
    }

    companion object {
        @JvmStatic
        fun of(pos: BlockPos): MishapBadCuboid {
            return MishapBadCuboid()
        }
    }

}