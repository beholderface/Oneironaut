package net.beholderface.oneironaut.casting.patterns.rod

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getPositiveInt
import at.petrak.hexcasting.api.casting.iota.Iota
import net.beholderface.oneironaut.casting.ReverbRodCastEnv
import net.beholderface.oneironaut.casting.mishaps.MishapNoRod
import net.beholderface.oneironaut.isUsingRod
import net.beholderface.oneironaut.item.ReverberationRod

class OpDelayRod : ConstMediaAction {
    override val argc = 1
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val delay = args.getPositiveInt(0, argc)
        //val rod = OneironautItemRegistry.REVERBERATION_ROD.get()
        if (env is ReverbRodCastEnv){
            env.setDelay(delay)
        } else {
            throw MishapNoRod(false)
        }
        return listOf()
    }
}