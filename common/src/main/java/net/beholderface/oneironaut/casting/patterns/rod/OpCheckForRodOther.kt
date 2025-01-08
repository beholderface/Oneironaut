package net.beholderface.oneironaut.casting.patterns.rod

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import net.beholderface.oneironaut.isUsingRod
import net.beholderface.oneironaut.item.ReverberationRod

class OpCheckForRodOther : ConstMediaAction {
    override val argc = 0
    override fun execute(args: List<Iota>, ctx: CastingEnvironment): List<Iota> {
        val state = ReverberationRod.getState(ctx.caster)
        return state?.currentlyCasting?.asActionResult ?: false.asActionResult
    }
}