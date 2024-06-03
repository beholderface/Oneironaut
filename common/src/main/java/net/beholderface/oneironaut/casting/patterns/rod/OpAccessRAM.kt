package net.beholderface.oneironaut.casting.patterns.rod

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.ListIota
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import net.minecraft.text.Text
import net.beholderface.oneironaut.Oneironaut
import net.beholderface.oneironaut.casting.mishaps.MishapNoRod
import net.beholderface.oneironaut.isUsingRod
import net.beholderface.oneironaut.item.ReverberationRod

class OpAccessRAM(storeInt : Int) : ConstMediaAction {
    override val argc = storeInt
    private final val store = storeInt == 1
    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        if (!isUsingRod(ctx)){
            throw MishapNoRod()
        }
        Oneironaut.boolLogger(ListIota.TYPE.toString(), false)
        val state = ReverberationRod.getState(ctx.caster)
        if (store){
            val iotaToStore = args[0]
            if (iotaToStore.type.equals(ListIota.TYPE) || iotaToStore.type.toString().contains("DictionaryIota", true)){
                throw MishapInvalidIota(iotaToStore, 0, Text.translatable("oneironaut.mishap.nolistsallowed"))
            }
            //not going to check for truenames because it's not like this is persistent storage or anything
            state.setStoredIota(iotaToStore)
            return listOf()
        } else {
            return listOf(state.storedIota)
        }
    }
}