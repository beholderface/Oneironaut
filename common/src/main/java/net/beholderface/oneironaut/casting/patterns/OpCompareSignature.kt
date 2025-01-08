package net.beholderface.oneironaut.casting.patterns

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment.HeldItemInfo
import at.petrak.hexcasting.api.casting.iota.BooleanIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.utils.getUUID
import net.minecraft.item.Items
import net.beholderface.oneironaut.casting.patterns.spells.OpSignItem
import net.beholderface.oneironaut.getSoulprint
import net.minecraft.text.Text

class OpCompareSignature : ConstMediaAction {
    override val argc = 1
    override val mediaCost = MediaConstants.DUST_UNIT / 10
    override fun execute(args: List<Iota>, ctx: CastingEnvironment): List<Iota> {
        val checkedSignature = args.getSoulprint(0, argc)
        val readItemInfo : HeldItemInfo = ctx.getHeldItemToOperateOn {it.item != Items.AIR} ?: throw MishapBadOffhandItem(null, Text.of("no_item.offhand"))
        val readItem = readItemInfo.stack
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