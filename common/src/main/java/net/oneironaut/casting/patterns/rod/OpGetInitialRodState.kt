package net.oneironaut.casting.patterns.rod

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.NullIota
import net.minecraft.util.Hand
import net.oneironaut.casting.mishaps.MishapNoRod
import net.oneironaut.registry.OneironautThingRegistry

class OpGetInitialRodState(val mode: Int) : ConstMediaAction {
    override val argc = 0

    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val mainStack = ctx.caster.getStackInHand(Hand.MAIN_HAND)
        val offStack = ctx.caster.getStackInHand(Hand.OFF_HAND)
        val rod = OneironautThingRegistry.REVERBERATION_ROD.get()
        if (mainStack.item == rod.asItem() || offStack.item == rod.asItem()){
            when(mode){
                1 -> return listOf(rod.initialLook)
                2 -> return listOf(rod.initialPos)
                3 -> return listOf(rod.initialTimestamp)
            }
        } else {
            //throw mishap
            throw MishapNoRod()
        }
        return listOf(NullIota())
    }
}