package net.beholderface.oneironaut.casting.patterns

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.env.StaffCastEnv
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.text.Text
import net.minecraft.util.Pair
import net.beholderface.oneironaut.casting.mishaps.MishapNoStaff
import net.beholderface.oneironaut.casting.iotatypes.SoulprintIota
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity

class OpGetSoulprint : ConstMediaAction {
    override val argc = 0
    override val mediaCost = MediaConstants.DUST_UNIT / 10
    override fun execute(args: List<Iota>, ctx: CastingEnvironment): List<Iota> {
        if (ctx !is StaffCastEnv){
            throw MishapNoStaff(Text.translatable("hexcasting.spell.oneironaut:getsoulprint"))
        }
        if (ctx.castingEntity != null && ctx.castingEntity is LivingEntity){
            throw MishapBadCaster()
        }
        return listOf(
            SoulprintIota(
                Pair(
                    ctx.castingEntity!!.uuid,
                    ctx.castingEntity!!.name.string
                )
            )
        )
    }
}