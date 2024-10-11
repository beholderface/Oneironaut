package net.beholderface.oneironaut.casting.patterns.rod

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.ListIota
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import net.beholderface.oneironaut.casting.mishaps.MishapNoRod
import net.beholderface.oneironaut.item.ReverberationRod
import net.minecraft.text.Text

class OpAccessRAMRemote(val store : Boolean) : ConstMediaAction {
    override val argc = if (store) { 1 } else { 0 }
    override val mediaCost = MediaConstants.DUST_UNIT / 100
    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val state = ReverberationRod.getState(ctx.caster)
        if (state != null && state.currentlyCasting){
            if (store){
                val iotaToStore = args[0]
                if (iotaToStore.type.equals(ListIota.TYPE) || iotaToStore.type.toString().contains("DictionaryIota", true)){
                    throw MishapInvalidIota(iotaToStore, 0, Text.translatable("oneironaut.mishap.nolistsallowed"))
                }
                state.setStoredIota(iotaToStore)
                return listOf()
            } else {
                return listOf(state.storedIota)
            }
        } else {
            throw MishapNoRod(true)
        }
    }
}