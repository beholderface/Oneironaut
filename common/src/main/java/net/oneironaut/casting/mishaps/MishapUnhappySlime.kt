package net.oneironaut.casting.mishaps

import at.petrak.hexcasting.api.misc.FrozenColorizer
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.Mishap
import net.minecraft.text.Text
import net.minecraft.util.DyeColor
import net.minecraft.util.math.BlockPos

class MishapUnhappySlime(val mishap : Mishap) : Mishap() {
    override fun accentColor(ctx: CastingContext, errorCtx: Context): FrozenColorizer = dyeColor(DyeColor.PURPLE)

    override fun errorMessage(ctx: CastingContext, errorCtx: Context): Text {
        return error("oneironaut:unhappyslime", mishap.errorMessage(ctx, errorCtx))
    }

    override fun execute(ctx: CastingContext, errorCtx: Context, stack: MutableList<Iota>) {
        mishap.execute(ctx, errorCtx, stack)
    }
    companion object {
        @JvmStatic
        fun of(mishap: Mishap): MishapUnhappySlime {
            return MishapUnhappySlime(mishap)
        }
    }

}