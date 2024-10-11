package net.beholderface.oneironaut.casting.patterns.spells.idea

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getEntity
import at.petrak.hexcasting.api.spell.getVec3
import at.petrak.hexcasting.api.spell.iota.EntityIota
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.Vec3Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapBadEntity
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.spell.mishaps.MishapLocationTooFarAway
import at.petrak.hexcasting.api.spell.mishaps.MishapOthersName
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

class OpWriteIdea : ConstMediaAction {
    override val argc = 2
    override val mediaCost = MediaConstants.DUST_UNIT / 4
    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val iotaToWrite = args[1]
        val truename = MishapOthersName.getTrueNameFromDatum(iotaToWrite, ctx.caster)
        if (truename != null){
            //if (!(truename.equals(ctx.caster)) || (truename.equals(ctx.caster) && !(ctx.source.equals(CastingContext.CastSource.STAFF))))
            throw MishapOthersName(truename)
        }
        val rawKeyIota = args[0]
        val keyEntity : Entity
        val keyPos : BlockPos
        val ideaState = IdeaInscriptionManager.getServerState(ctx.world.server)
        if (rawKeyIota.type == EntityIota.TYPE){
            keyEntity = args.getEntity(0, argc)
            ctx.assertEntityInRange(keyEntity)
            if (keyEntity.type.equals(EntityType.VILLAGER)){
                if (IXplatAbstractions.INSTANCE.isBrainswept(keyEntity as VillagerEntity)){
                    IdeaInscriptionManager.writeIota(keyEntity.uuid, iotaToWrite, ctx.caster, ctx.world)
                } else {
                    throw MishapBadEntity(keyEntity, Text.translatable("oneironaut.mishap.notbrainswept"))
                }
            } else if (keyEntity.isPlayer){
                if (isPlayerEnlightened(keyEntity as ServerPlayerEntity)){
                    IdeaInscriptionManager.writeIota(keyEntity.uuid, iotaToWrite, ctx.caster, ctx.world)
                } else {
                    throw MishapBadEntity(keyEntity, Text.translatable("oneironaut.mishap.unenlightenedtarget"))
                }
            } else {
                throw MishapBadEntity(keyEntity, Text.translatable("oneironaut.mishap.badentitykey"))
            }
        } else if (rawKeyIota.type == Vec3Iota.TYPE){
            keyPos = BlockPos(args.getVec3(0, argc))
            val worldborder = ctx.world.server.overworld.worldBorder
            if (keyPos.y < -64 || keyPos.y > 320 || !(worldborder.contains(keyPos))){
                throw MishapLocationTooFarAway(args.getVec3(0, argc), "out_of_world")
            }
            IdeaInscriptionManager.writeIota(keyPos, iotaToWrite, ctx.caster, ctx.world)
        }else if (rawKeyIota.type == SoulprintIota.TYPE){
            val keySoulprint = args.getSoulprint(0, argc).toString() + "soul"
            IdeaInscriptionManager.writeIota(keySoulprint, iotaToWrite, ctx.caster, ctx.world)
        } else {
            throw MishapInvalidIota(rawKeyIota, 1, Text.translatable("oneironaut.mishap.invalidideakey"));
        }
        ideaState.markDirty()
        return listOf()
    }
}