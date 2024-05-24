package net.oneironaut.casting.patterns

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getPlayer
import at.petrak.hexcasting.api.spell.getVec3
import at.petrak.hexcasting.api.spell.iota.DoubleIota
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.NullIota
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.text.Text
import net.minecraft.util.Pair
import net.oneironaut.casting.mishaps.MishapNoStaff
import net.oneironaut.registry.SoulprintIota

class OpGetSoulprint : ConstMediaAction{
    override val argc = 0
    override val mediaCost = MediaConstants.DUST_UNIT / 10
    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        if (ctx.source != CastingContext.CastSource.STAFF){
            throw MishapNoStaff(Text.translatable("hexcasting.spell.oneironaut:getsoulprint"))
        }
        return listOf(SoulprintIota(Pair(ctx.caster.uuid, ctx.caster.name.string)))
    }
}