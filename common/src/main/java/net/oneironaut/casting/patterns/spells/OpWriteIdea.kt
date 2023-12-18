package net.oneironaut.casting.patterns.spells

import at.petrak.hexcasting.api.HexAPI
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getPlayer
import at.petrak.hexcasting.api.spell.getVec3
import at.petrak.hexcasting.api.spell.iota.EntityIota
import at.petrak.hexcasting.api.spell.iota.GarbageIota
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.Vec3Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.spell.mishaps.MishapLocationTooFarAway
import at.petrak.hexcasting.api.spell.mishaps.MishapOthersName
import net.minecraft.entity.Entity
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.oneironaut.casting.IdeaInscriptionManager
import net.oneironaut.isPlayerEnlightened

class OpWriteIdea : ConstMediaAction {
    override val argc = 2
    override val mediaCost = MediaConstants.DUST_UNIT
    /*override val isGreat = true
    override val alwaysProcessGreatSpell = false
    override val causesBlindDiversion = false*/
    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val iotaToWrite = args[1]
        val truename = MishapOthersName.getTrueNameFromDatum(iotaToWrite, ctx.caster)
        if (truename != null){
            throw MishapOthersName(truename)
        }
        val rawKeyIota = args[0]
        val keyEntity : Entity
        val keyPos : BlockPos
        val ideaState = IdeaInscriptionManager.getServerState(ctx.world.server)
        if (rawKeyIota.type.equals(EntityIota.TYPE)){
            keyEntity = args.getPlayer(0, argc)
            ctx.assertEntityInRange(keyEntity)
            if (isPlayerEnlightened(keyEntity)){
                if (iotaToWrite.type.equals(GarbageIota.TYPE)){
                    IdeaInscriptionManager.eraseIota(keyEntity.uuid)
                } else {
                    IdeaInscriptionManager.writeIota(keyEntity.uuid, iotaToWrite, ctx.caster, ctx.world)
                }
            }
        } else if (rawKeyIota.type.equals(Vec3Iota.TYPE)){
            keyPos = BlockPos(args.getVec3(0, argc))
            val worldborder = ctx.world.server.overworld.worldBorder
            if (keyPos.y < -64 || keyPos.y > 320 || !(worldborder.contains(keyPos))){
                throw MishapLocationTooFarAway(args.getVec3(0, argc), "out_of_world")
            }
            if (iotaToWrite.type.equals(GarbageIota.TYPE)){
                IdeaInscriptionManager.eraseIota(keyPos)
            } else {
                IdeaInscriptionManager.writeIota(keyPos, iotaToWrite, ctx.caster, ctx.world)
            }
        }/* else if (rawKeyIota.type.equals(NullIota.TYPE) && ctx.caster.hasPermissionLevel(3)) {
            //
        } */else {
            throw MishapInvalidIota(rawKeyIota, 1, Text.translatable("oneironaut.mishap.novecorentity"));
        }
        ideaState.markDirty()
        return listOf()
    }
}