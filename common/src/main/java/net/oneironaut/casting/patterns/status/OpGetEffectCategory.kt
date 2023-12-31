package net.oneironaut.casting.patterns.status

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.BooleanIota
import at.petrak.hexcasting.api.spell.iota.DoubleIota
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.NullIota
import net.minecraft.entity.effect.StatusEffectCategory
import net.oneironaut.getStatusEffect

class OpGetEffectCategory : ConstMediaAction {
    override val argc = 1
    override val mediaCost = 0
    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val effect = args.getStatusEffect(0, argc, true)
        val output = when(effect.category){
            StatusEffectCategory.BENEFICIAL -> DoubleIota(1.0)
            StatusEffectCategory.HARMFUL -> DoubleIota(-1.0)
            StatusEffectCategory.NEUTRAL -> DoubleIota(0.0)
            null -> NullIota()
        }
        return listOf(output)
    }
}