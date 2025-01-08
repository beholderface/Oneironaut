package net.beholderface.oneironaut.casting.patterns

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapDisallowedSpell
import at.petrak.hexcasting.api.misc.MediaConstants
import net.beholderface.oneironaut.casting.iotatypes.DimIota
import net.minecraft.util.Identifier
import java.util.function.Supplier

class OpSpecificDim(val dim : Identifier, val great : Boolean, val config : Supplier<Boolean>) : ConstMediaAction {

    override val argc = 0
    override val mediaCost = MediaConstants.DUST_UNIT / 10
    override fun execute(args: List<Iota>, ctx: CastingEnvironment): List<Iota> {
        if (!config.get()){
            throw MishapDisallowedSpell()
        }
        return listOf(DimIota(dim.toString()))
    }
}