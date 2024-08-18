package net.beholderface.oneironaut.casting.patterns.spells.great

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.spell.*
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapBadEntity
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.beholderface.oneironaut.item.MemoryFragmentItem
import net.beholderface.oneironaut.unbrainsweep
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.passive.VillagerEntity
import net.minecraft.text.Text
import net.minecraft.util.Identifier

class OpReviveFlayed : SpellAction {
    override val argc = 1
    override val isGreat = true
    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>>? {
        val patient = args.getLivingEntityButNotArmorStand(0, argc)
        ctx.assertEntityInRange(patient)
        if (patient is MobEntity && IXplatAbstractions.INSTANCE.isBrainswept(patient)){
            val cost = if (patient is VillagerEntity) {
                MediaConstants.CRYSTAL_UNIT * 16
            } else {
                MediaConstants.SHARD_UNIT * 10
            }
            return Triple(Spell(patient), cost, listOf(ParticleSpray.cloud(patient.pos, 1.0)))
        } else {
            throw MishapBadEntity(patient, Text.of("oneironaut.mishap.requiresflayedmob"))
        }
    }

    private data class Spell(val patient : MobEntity) : RenderedSpell {
        override fun cast(ctx: CastingContext) {
            patient.unbrainsweep()
            if (patient is VillagerEntity){
                val tracker = ctx.caster.advancementTracker
                val loader = ctx.world.server.advancementLoader
                val recyclingAdvancement = loader.get(Identifier.of("oneironaut", "unflay"))
                if (!tracker.getProgress(recyclingAdvancement).isDone){
                    tracker.grantCriterion(recyclingAdvancement, MemoryFragmentItem.CRITEREON_KEY)
                }
            }
        }
    }

}