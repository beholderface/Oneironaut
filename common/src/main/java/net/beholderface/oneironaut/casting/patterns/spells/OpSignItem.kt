package net.beholderface.oneironaut.casting.patterns.spells

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import net.minecraft.item.Items
import net.minecraft.text.Text
import net.beholderface.oneironaut.casting.mishaps.MishapNoStaff
import java.util.UUID


class OpSignItem : ConstMediaAction {
    override val argc = 0
    override val mediaCost = MediaConstants.DUST_UNIT
    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        if (ctx.source != CastingContext.CastSource.STAFF){
            throw MishapNoStaff(Text.translatable("hexcasting.spell.oneironaut:signitem"))
        }
        //can be used on any item
        val itemToSign = ctx.getHeldItemToOperateOn {it.item != Items.AIR}.first
        val nbt = itemToSign.orCreateNbt
        val existingSignature = if(nbt.contains("soulprint_signature")){
            nbt.getUuid("soulprint_signature")
        } else {
            null
        }
        nbt.putUuid("soulprint_signature", ctx.caster.uuid)
        if (existingSignature != null){
            if (existingSignature.equals(ctx.caster.uuid)){
                nbt.remove("soulprint_signature")
            }
        }
        itemToSign.nbt = nbt
        return listOf()
    }
}