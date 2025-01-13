package net.beholderface.oneironaut.casting.lichdom

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.server.network.ServerPlayerEntity

class OpLichify : SpellAction {
    override val argc = 0
    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        if (env.castingEntity !is ServerPlayerEntity){
            throw MishapBadCaster()
        }
        return SpellAction.Result(Spell(env.castingEntity as ServerPlayerEntity), MediaConstants.QUENCHED_BLOCK_UNIT, listOf(
            ParticleSpray.burst(env.mishapSprayPos(), 1.0, 16)
        ))
    }

    private data class Spell(val target : ServerPlayerEntity) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            LichdomManager.lichifyPlayer(target)
        }

    }

}