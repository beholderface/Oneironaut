package net.beholderface.oneironaut.casting.patterns.spells.idea

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.Vec3Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadEntity
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.beholderface.oneironaut.casting.IdeaInscriptionManager
import net.beholderface.oneironaut.getSoulprint
import net.beholderface.oneironaut.casting.iotatypes.SoulprintIota
import net.beholderface.oneironaut.toVec3i

class OpGetIdeaTimestamp : ConstMediaAction {
    override val argc = 1
    override val mediaCost = 0L
    override fun execute(args: List<Iota>, ctx: CastingEnvironment): List<Iota> {
        var output : Double = -1.0
        val rawKeyIota = args[0]
        val keyEntity : Entity
        val keyPos : BlockPos
        if (rawKeyIota.type == EntityIota.TYPE){
            keyEntity = args.getEntity(0, argc)
            ctx.assertEntityInRange(keyEntity)
            if (keyEntity.isPlayer || keyEntity.type.equals(EntityType.VILLAGER)){
                output = IdeaInscriptionManager.getIotaTimestamp(keyEntity.uuid, ctx.world)
            } else {
                throw MishapBadEntity(keyEntity, Text.translatable("oneironaut.mishap.badentitykey"))
            }
        } else if (rawKeyIota.type == Vec3Iota.TYPE){
            keyPos = BlockPos(args.getVec3(0, argc).toVec3i())
            output = IdeaInscriptionManager.getIotaTimestamp(keyPos, ctx.world)
        } else if (rawKeyIota.type == SoulprintIota.TYPE){
            val keySoulprint = args.getSoulprint(0, argc).toString() + "soul"
            output = IdeaInscriptionManager.getIotaTimestamp(keySoulprint, ctx.world)
        } else {
            throw MishapInvalidIota(rawKeyIota, 0, Text.translatable("oneironaut.mishap.invalidideakey"));
        }
        return listOf(DoubleIota(output))
    }
}