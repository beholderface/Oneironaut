package net.beholderface.oneironaut.casting.mishaps

import at.petrak.hexcasting.api.misc.FrozenColorizer
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.Mishap
import net.minecraft.text.Text
import net.minecraft.util.DyeColor
import net.minecraft.util.math.BlockPos

class MishapCellMissingRequirement(val desired : String) : Mishap() {
    override fun accentColor(ctx: CastingContext, errorCtx: Context): FrozenColorizer = dyeColor(DyeColor.PURPLE)

    override fun errorMessage(ctx: CastingContext, errorCtx: Context): Text {
        return error("oneironaut:norequirediota", Text.translatable(desired))
    }

    override fun execute(ctx: CastingContext, errorCtx: Context, stack: MutableList<Iota>) {
    }
    companion object {
        @JvmStatic
        fun of(desired: String): MishapCellMissingRequirement {
            return MishapCellMissingRequirement(desired)
        }
    }

}