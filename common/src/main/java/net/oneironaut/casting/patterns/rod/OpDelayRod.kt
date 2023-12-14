package net.oneironaut.casting.patterns.rod

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getPositiveInt
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.NullIota
import net.minecraft.util.Hand
import net.oneironaut.casting.mishaps.MishapNoRod
import net.oneironaut.registry.OneironautThingRegistry

class OpDelayRod : ConstMediaAction {
    override val argc = 1
    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val delay = args.getPositiveInt(0, argc)
        val mainStack = ctx.caster.getStackInHand(Hand.MAIN_HAND)
        val offStack = ctx.caster.getStackInHand(Hand.OFF_HAND)
        val rod = OneironautThingRegistry.REVERBERATION_ROD.get()
        if (mainStack.item == rod.asItem() || offStack.item == rod.asItem()){
            rod.delay = delay
        } else {
            throw MishapNoRod()
        }
        return listOf()
    }
}