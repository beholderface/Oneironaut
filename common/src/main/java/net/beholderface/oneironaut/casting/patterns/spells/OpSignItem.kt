package net.beholderface.oneironaut.casting.patterns.spells

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment.HeldItemInfo
import at.petrak.hexcasting.api.casting.eval.env.StaffCastEnv
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.item.Items
import net.minecraft.text.Text
import net.beholderface.oneironaut.casting.mishaps.MishapNoStaff
import net.minecraft.server.network.ServerPlayerEntity
import java.util.UUID


class OpSignItem : ConstMediaAction {
    override val argc = 0
    override val mediaCost = MediaConstants.DUST_UNIT
    override fun execute(args: List<Iota>, ctx: CastingEnvironment): List<Iota> {
        if (ctx.castingEntity == null || ctx.castingEntity !is ServerPlayerEntity){
            throw MishapBadCaster()
        }
        if (ctx !is StaffCastEnv){
            throw MishapNoStaff(Text.translatable("hexcasting.spell.oneironaut:signitem"))
        }
        //can be used on any item
        val heldInfo : HeldItemInfo = ctx.getHeldItemToOperateOn{it != null} ?: throw MishapBadOffhandItem(null, Text.of("no_item.offhand"))
        val itemToSign = heldInfo.stack
        val nbt = itemToSign.orCreateNbt
        val existingSignature = if(nbt.contains("soulprint_signature")){
            nbt.getUuid("soulprint_signature")
        } else {
            null
        }
        nbt.putUuid("soulprint_signature", ctx.castingEntity!!.uuid)
        if (existingSignature != null){
            if (existingSignature.equals(ctx.castingEntity!!.uuid)){
                nbt.remove("soulprint_signature")
            }
        }
        itemToSign.nbt = nbt
        return listOf()
    }
}