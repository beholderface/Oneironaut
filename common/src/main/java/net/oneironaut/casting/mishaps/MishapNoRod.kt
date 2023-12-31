package net.oneironaut.casting.mishaps

import at.petrak.hexcasting.api.misc.FrozenColorizer
import at.petrak.hexcasting.api.spell.ParticleSpray
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.Mishap
import net.minecraft.text.Text
import net.minecraft.util.DyeColor
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.explosion.Explosion

class MishapNoRod() : Mishap() {
    override fun accentColor(ctx: CastingContext, errorCtx: Context): FrozenColorizer = dyeColor(DyeColor.CYAN)

    override fun particleSpray(ctx: CastingContext) =
        ParticleSpray.burst(ctx.caster.pos, 1.0)

    override fun errorMessage(ctx: CastingContext, errorCtx: Context): Text =
        error("oneironaut:norod")

    override fun execute(ctx: CastingContext, errorCtx: Context, stack: MutableList<Iota>) {
        yeetHeldItem(ctx, Hand.MAIN_HAND)
        yeetHeldItem(ctx, Hand.OFF_HAND)
        //ctx.world.createExplosion(null, pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, 0.25f, Explosion.DestructionType.NONE)
    }

    companion object {
        @JvmStatic
        fun of(): MishapNoRod {
            return MishapNoRod()
        }
    }

}