package net.beholderface.oneironaut.casting.patterns.spells.idea

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.GarbageIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.Vec3Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadEntity
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.passive.VillagerEntity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.beholderface.oneironaut.casting.IdeaInscriptionManager
import net.beholderface.oneironaut.getSoulprint
import net.beholderface.oneironaut.isPlayerEnlightened
import net.beholderface.oneironaut.casting.iotatypes.SoulprintIota
import net.beholderface.oneironaut.toVec3i

class OpReadIdea : ConstMediaAction {
    override val argc = 1
    override val mediaCost = MediaConstants.DUST_UNIT / 8
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        var output : Iota = GarbageIota()
        val rawKeyIota = args[0]
        val keyEntity : Entity
        val keyPos : BlockPos
        if (rawKeyIota.type == EntityIota.TYPE){
            keyEntity = args.getEntity(0, argc)
            env.assertEntityInRange(keyEntity)
            if (keyEntity.type.equals(EntityType.VILLAGER)){
                if (IXplatAbstractions.INSTANCE.isBrainswept(keyEntity as VillagerEntity)){
                    output = IdeaInscriptionManager.readIota(keyEntity.uuid, env.world)
                }
            } else if (keyEntity.isPlayer){
                if (isPlayerEnlightened(keyEntity as ServerPlayerEntity)){
                    output = IdeaInscriptionManager.readIota(keyEntity.uuid, env.world)
                }
            } else {
                throw MishapBadEntity(keyEntity, Text.translatable("oneironaut.mishap.badentitykey"))
            }
        } else if (rawKeyIota.type == Vec3Iota.TYPE){
            keyPos = BlockPos(args.getVec3(0, argc).toVec3i())
            output = IdeaInscriptionManager.readIota(keyPos, env.world)
        } else if (rawKeyIota.type == SoulprintIota.TYPE){
            val keySoulprint = args.getSoulprint(0, argc).toString() + "soul"
            output = IdeaInscriptionManager.readIota(keySoulprint, env.world)
        } else {
            throw MishapInvalidIota(rawKeyIota, 0, Text.translatable("oneironaut.mishap.invalidideakey"));
        }
        return listOf(output)
    }
}