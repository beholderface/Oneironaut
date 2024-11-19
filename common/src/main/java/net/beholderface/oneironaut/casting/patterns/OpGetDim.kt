package net.beholderface.oneironaut.casting.patterns

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.NullIota
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.beholderface.oneironaut.casting.iotatypes.DimIota

class OpGetDim (val sent: Boolean) : ConstMediaAction{
    override val argc = 0
    override val mediaCost = if (sent) { MediaConstants.DUST_UNIT / 10 } else { 0 }
    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        return if (!sent){
            val casterWorld : String = ctx.world.registryKey.value.toString()
            listOf(DimIota(casterWorld))
        } else {
            val sentinel = IXplatAbstractions.INSTANCE.getSentinel(ctx.caster)
            if (sentinel.hasSentinel)
                listOf(DimIota(sentinel.dimension.value.toString()))
            else
                listOf(NullIota())
        }


        /*if (!HexConfig.server().canTeleportInThisDimension(ctx.world.registryKey))
            throw MishapLocationTooFarAway(ctx.caster.pos, "bad_dimension")*/


    }
}