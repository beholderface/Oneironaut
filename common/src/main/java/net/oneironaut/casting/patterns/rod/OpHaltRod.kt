package net.oneironaut.casting.patterns.rod

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getPositiveInt
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.utils.putInt
import net.oneironaut.casting.mishaps.MishapNoRod
import net.oneironaut.registry.OneironautThingRegistry

class OpHaltRod(val reset : Int) : ConstMediaAction {
    override val argc = reset
    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val rod = OneironautThingRegistry.REVERBERATION_ROD.get()
        if (ctx.caster.activeItem.item == rod.asItem() && ctx.source == CastingContext.CastSource.PACKAGED_HEX){
            val delay = args.getPositiveInt(0, argc)
            if(reset == 1 && delay <= 19){
                ctx.caster.activeItem.nbt.putInt("resetDelay", delay)
            }
            ctx.caster.stopUsingItem()
        } else {
            throw MishapNoRod()
        }
        return listOf()
    }
}