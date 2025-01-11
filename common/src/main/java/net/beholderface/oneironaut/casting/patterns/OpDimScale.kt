package net.beholderface.oneironaut.casting.patterns

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import net.beholderface.oneironaut.getDimIota

class OpDimScale : ConstMediaAction {
    override val argc = 1
    override val mediaCost = 0L
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val dim = args.getDimIota(0, argc).toWorld(env.world.server)
        assert(dim != null)
        assert(dim!!.dimension != null)
        return listOf(DoubleIota(dim.dimension.coordinateScale))
    }

}