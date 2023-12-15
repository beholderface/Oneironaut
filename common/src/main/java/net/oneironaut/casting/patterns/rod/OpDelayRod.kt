package net.oneironaut.casting.patterns.rod

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getPositiveDouble
import at.petrak.hexcasting.api.spell.getPositiveInt
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.NullIota
import at.petrak.hexcasting.api.utils.putDouble
import at.petrak.hexcasting.api.utils.putInt
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.oneironaut.Oneironaut
import net.oneironaut.casting.mishaps.MishapNoRod
import net.oneironaut.registry.OneironautThingRegistry
import kotlin.math.floor

class OpDelayRod : ConstMediaAction {
    override val argc = 1
    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val delay = args.getPositiveDouble(0, argc)
        //Oneironaut.LOGGER.info(delay)
        val mainStack = ctx.caster.getStackInHand(Hand.MAIN_HAND)
        val offStack = ctx.caster.getStackInHand(Hand.OFF_HAND)
        val rod = OneironautThingRegistry.REVERBERATION_ROD.get()
        if (mainStack.item == rod.asItem() || offStack.item == rod.asItem()){
            val activeHand : Hand = if (mainStack.item == rod.asItem()){
                Hand.MAIN_HAND
            } else {
                Hand.OFF_HAND
            }
            ctx.caster.getStackInHand(activeHand).nbt.putDouble("delay", floor(delay))
        } else {
            throw MishapNoRod()
        }
        return listOf()
    }
}