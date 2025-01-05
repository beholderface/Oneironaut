package net.beholderface.oneironaut.casting.patterns

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.DoubleIota
import at.petrak.hexcasting.api.spell.iota.Iota
import net.beholderface.oneironaut.getDimIota
import net.beholderface.oneironaut.stringToWorld

class OpDimScale : ConstMediaAction {
    override val argc = 1
    override val mediaCost = 0
    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val dim = stringToWorld(args.getDimIota(0, argc).dimString, ctx.caster)
        return listOf(DoubleIota(dim.dimension.coordinateScale))
    }

}