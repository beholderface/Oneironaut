package net.oneironaut.casting.patterns.rod

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getPositiveInt
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.utils.putInt
import net.oneironaut.casting.mishaps.MishapNoRod
import net.oneironaut.registry.OneironautThingRegistry

class OpDelayRod : ConstMediaAction {
    override val argc = 1
    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val delay = args.getPositiveInt(0, argc)
        val rod = OneironautThingRegistry.REVERBERATION_ROD.get()
        if (ctx.caster.activeItem.item == rod.asItem()){
            ctx.caster.activeItem.nbt.putInt("delay", delay)
        } else {
            throw MishapNoRod()
        }
        return listOf()
    }
}