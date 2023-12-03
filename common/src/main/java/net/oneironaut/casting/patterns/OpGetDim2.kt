package net.oneironaut.casting.patterns

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.mod.HexConfig
import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.NullIota
import at.petrak.hexcasting.api.spell.mishaps.MishapLocationTooFarAway
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.oneironaut.registry.DimIota

class OpGetDim2 : ConstMediaAction{
    override val argc = 0
    override val mediaCost = MediaConstants.DUST_UNIT / 10
    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val sentinel = IXplatAbstractions.INSTANCE.getSentinel(ctx.caster)
        //val casterWorld : String = ctx.world.registryKey.value.toString()

        /*if (!HexConfig.server().canTeleportInThisDimension(sentinel.dimension))
            throw MishapLocationTooFarAway(ctx.caster.pos, "bad_dimension")*/

        return if (sentinel.hasSentinel)
            listOf(DimIota(sentinel.dimension.value.toString()))
        else
            listOf(NullIota())
    }
}