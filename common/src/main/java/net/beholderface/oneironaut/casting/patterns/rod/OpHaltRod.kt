package net.beholderface.oneironaut.casting.patterns.rod

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getPositiveInt
import at.petrak.hexcasting.api.spell.iota.Iota
import net.beholderface.oneironaut.casting.mishaps.MishapNoRod
import net.beholderface.oneironaut.isUsingRod
import net.beholderface.oneironaut.item.ReverberationRod

class OpHaltRod(val reset : Int) : ConstMediaAction {
    override val argc = reset
    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        //val rod = OneironautItemRegistry.REVERBERATION_ROD.get()
        if (isUsingRod(ctx)){
            val state = ReverberationRod.getState(ctx.caster)
            if(reset == 1){
                val delay = args.getPositiveInt(0, argc)
                state.setResetCooldown(delay.coerceAtLeast(1).coerceAtMost(100))
            }
            state.stopCasting()
            //ctx.caster.stopUsingItem()
        } else {
            throw MishapNoRod(false)
        }
        return listOf()
    }
}