package net.beholderface.oneironaut.casting.patterns

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.beholderface.oneironaut.casting.iotatypes.DimIota
import net.minecraft.server.network.ServerPlayerEntity

class OpGetDim (val sent: Boolean) : ConstMediaAction {
    override val argc = 0
    override val mediaCost = if (sent) { MediaConstants.DUST_UNIT / 10 } else { 0 }
    override fun execute(args: List<Iota>, ctx: CastingEnvironment): List<Iota> {
        return if (!sent){
            val casterWorld : String = ctx.world.registryKey.value.toString()
            listOf(DimIota(casterWorld))
        } else {
            if (ctx.castingEntity is ServerPlayerEntity){
                val sentinel = IXplatAbstractions.INSTANCE.getSentinel(ctx.caster)
                if (sentinel != null)
                    listOf(DimIota(sentinel.dimension.value.toString()))
            }
            listOf(NullIota())
        }


        /*if (!HexConfig.server().canTeleportInThisDimension(ctx.world.registryKey))
            throw MishapLocationTooFarAway(ctx.caster.pos, "bad_dimension")*/


    }
}