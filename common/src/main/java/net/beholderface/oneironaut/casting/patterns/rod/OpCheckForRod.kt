package net.beholderface.oneironaut.casting.patterns.rod

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.asActionResult
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import net.beholderface.oneironaut.isUsingRod

class OpCheckForRod : ConstMediaAction {
    override val argc = 0
    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        return isUsingRod(ctx).asActionResult
    }
}