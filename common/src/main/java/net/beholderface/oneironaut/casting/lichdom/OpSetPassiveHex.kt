package net.beholderface.oneironaut.casting.lichdom

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getList
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.server.network.ServerPlayerEntity

class OpSetPassiveHex : ConstMediaAction {
    override val argc = 1
    override val mediaCost = MediaConstants.CRYSTAL_UNIT
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        if (env.castingEntity !is ServerPlayerEntity){
            throw MishapBadCaster()
        }
        val caster = env.castingEntity as ServerPlayerEntity
        if (LichdomManager.isPlayerLich(caster)){
            val lichData = LichdomManager.getLichData(caster)
            val hex : List<Iota> = args.getList(0, argc).toList()
            lichData.passiveHex = hex
        }
        return listOf()
    }
}