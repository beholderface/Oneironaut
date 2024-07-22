package net.beholderface.oneironaut.casting.patterns

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapDisallowedSpell
import net.beholderface.oneironaut.OneironautConfig
import net.beholderface.oneironaut.casting.iotatypes.DimIota
import net.minecraft.util.Identifier

class OpSpecificDim(val dim : Identifier, val great : Boolean) : ConstMediaAction {

    override val isGreat = great
    override val argc = 0
    override val mediaCost = MediaConstants.DUST_UNIT / 10
    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        if ((dim.equals(Identifier("minecraft:overworld")) && !OneironautConfig.server.allowOverworldReflection) ||
            (dim.equals(Identifier("minecraft:the_nether")) && !OneironautConfig.server.allowNetherReflection)){
            throw MishapDisallowedSpell()
        }
        return listOf(DimIota(dim.toString()))
    }
}