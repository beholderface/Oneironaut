package net.beholderface.oneironaut.casting.patterns

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import net.beholderface.oneironaut.getDimIota
import net.beholderface.oneironaut.stringToWorld

class OpDimScale : ConstMediaAction {
    override val argc = 1
    override val mediaCost = 0L
    override fun execute(args: List<Iota>, ctx: CastingEnvironment): List<Iota> {
        val dim = stringToWorld(args.getDimIota(0, argc).dimString, ctx.world.server)
        assert(dim != null)
        assert(dim!!.dimension != null)
        return listOf(DoubleIota(dim.dimension.coordinateScale))
    }

}