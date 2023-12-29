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

class OpReadSentinel : ConstMediaAction{
    override val argc = 2
    override val mediaCost = MediaConstants.DUST_UNIT / 10
    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val origin = args.getVec3(0, argc)
        val target = args.getPlayer(1, argc)
        val sentinel = IXplatAbstractions.INSTANCE.getSentinel(target)
        ctx.assertVecInRange(origin)
        if (sentinel.hasSentinel){
            if (sentinel.dimension.equals(ctx.world.registryKey)){
                return listOf(DoubleIota(origin.subtract(sentinel.position).length()))
            }
            return listOf(NullIota())
        }
        return listOf(NullIota())
    }
}