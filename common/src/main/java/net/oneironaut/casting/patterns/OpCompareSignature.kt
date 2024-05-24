package net.oneironaut.casting.patterns

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.BooleanIota
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.utils.getUUID
import net.minecraft.item.Items
import net.oneironaut.casting.patterns.spells.OpSignItem
import net.oneironaut.getSoulprint

class OpCompareSignature : ConstMediaAction {
    override val argc = 1
    override val mediaCost = MediaConstants.DUST_UNIT / 10
    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val checkedSignature = args.getSoulprint(0, argc)
        val readItem = ctx.getHeldItemToOperateOn {it.item != Items.AIR}.first
        if (readItem.hasNbt()){
            val nbt = readItem.nbt
            val signature = nbt.getUUID("soulprint_signature")
            if (signature != null){
                if (signature.equals(checkedSignature)){
                    return listOf(BooleanIota(true))
                }
            }
        }
        return listOf(BooleanIota(false))
    }
}