package net.beholderface.oneironaut.casting.patterns.rod

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getPositiveInt
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.utils.putInt
import net.beholderface.oneironaut.casting.RodState
import net.beholderface.oneironaut.casting.mishaps.MishapNoRod
import net.beholderface.oneironaut.isUsingRod
import net.beholderface.oneironaut.item.ReverberationRod
import net.beholderface.oneironaut.registry.OneironautItemRegistry

class OpDelayRod : ConstMediaAction {
    override val argc = 1
    override fun execute(args: List<Iota>, ctx: CastingEnvironment): List<Iota> {
        val delay = args.getPositiveInt(0, argc)
        //val rod = OneironautItemRegistry.REVERBERATION_ROD.get()
        if (isUsingRod(ctx)){
            //ReverberationRod.DELAY_MAP.put(ctx.caster.uuid, delay);
            /*val state = */ReverberationRod.getState(ctx.caster).setDelay(delay)
            //ctx.caster.activeItem.nbt.putInt("delay", delay)
        } else {
            throw MishapNoRod(false)
        }
        return listOf()
    }
}