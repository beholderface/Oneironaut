package net.oneironaut.casting.patterns.spells

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getLivingEntityButNotArmorStand
import at.petrak.hexcasting.api.spell.getPlayer
import at.petrak.hexcasting.api.spell.getVec3
import at.petrak.hexcasting.api.spell.iota.EntityIota
import at.petrak.hexcasting.api.spell.iota.GarbageIota
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.Vec3Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import net.minecraft.entity.Entity
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.oneironaut.casting.IdeaInscriptionManager
import net.oneironaut.isPlayerEnlightened

class OpReadIdea : ConstMediaAction {
    override val argc = 1
    override val mediaCost = MediaConstants.DUST_UNIT;
    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        var output : Iota = GarbageIota()
        val rawKeyIota = args[0]
        val keyEntity : Entity
        val keyPos : BlockPos
        if (rawKeyIota.type.equals(EntityIota.TYPE)){
            keyEntity = args.getPlayer(0, argc)
            ctx.assertEntityInRange(keyEntity)
            if (isPlayerEnlightened(keyEntity)){
                output = IdeaInscriptionManager.readIota(keyEntity.uuid, ctx.world)
            }
        } else if (rawKeyIota.type.equals(Vec3Iota.TYPE)){
            keyPos = BlockPos(args.getVec3(0, argc))
            output = IdeaInscriptionManager.readIota(keyPos, ctx.world)
        } else {
            throw MishapInvalidIota(rawKeyIota, 0, Text.translatable("oneironaut.mishap.novecorentity"));
        }
        return listOf(output)
    }
}