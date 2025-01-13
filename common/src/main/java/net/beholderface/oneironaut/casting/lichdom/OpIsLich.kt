package net.beholderface.oneironaut.casting.lichdom

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getPlayer
import at.petrak.hexcasting.api.casting.iota.Iota

class OpIsLich : ConstMediaAction {
    override val argc = 1
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val target = args.getPlayer(0, argc)
        return LichdomManager.isPlayerLich(target).asActionResult
    }
}