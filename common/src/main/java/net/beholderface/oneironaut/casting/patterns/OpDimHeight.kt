package net.beholderface.oneironaut.casting.patterns

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import net.beholderface.oneironaut.getDimIota
import net.beholderface.oneironaut.stringToWorld
import java.lang.AssertionError

class OpDimHeight : ConstMediaAction {
    override val argc = 1
    override val mediaCost = 0L
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val dimString = args.getDimIota(0, argc).dimString
        val dim = stringToWorld(dimString, env.world.server)
        if (dim != null) {
            return listOf(DoubleIota(dim.bottomY.toDouble()), DoubleIota(dim.topY.toDouble() - 1))
        } else {
            throw AssertionError("could not find dimension corresponding to $dimString")
        }
    }
}