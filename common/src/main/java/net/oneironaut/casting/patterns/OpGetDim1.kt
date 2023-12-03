package net.oneironaut.casting.patterns

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.mod.HexConfig
import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapLocationTooFarAway
import net.minecraft.util.Identifier
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.World
import net.oneironaut.registry.DimIota

class OpGetDim1 : ConstMediaAction{
    override val argc = 0
    override val mediaCost = MediaConstants.DUST_UNIT / 100
    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val casterWorld : String = ctx.world.registryKey.value.toString()

        /*if (!HexConfig.server().canTeleportInThisDimension(ctx.world.registryKey))
            throw MishapLocationTooFarAway(ctx.caster.pos, "bad_dimension")*/

        return listOf(DimIota(casterWorld))
    }
}