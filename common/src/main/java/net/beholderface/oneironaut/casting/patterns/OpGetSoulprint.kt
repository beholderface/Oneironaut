package net.beholderface.oneironaut.casting.patterns

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import net.minecraft.text.Text
import net.minecraft.util.Pair
import net.beholderface.oneironaut.casting.mishaps.MishapNoStaff
import net.beholderface.oneironaut.casting.iotatypes.SoulprintIota

class OpGetSoulprint : ConstMediaAction{
    override val argc = 0
    override val mediaCost = MediaConstants.DUST_UNIT / 10
    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        if (ctx.source != CastingContext.CastSource.STAFF){
            throw MishapNoStaff(Text.translatable("hexcasting.spell.oneironaut:getsoulprint"))
        }
        return listOf(
            SoulprintIota(
                Pair(
                    ctx.caster.uuid,
                    ctx.caster.name.string
                )
            )
        )
    }
}