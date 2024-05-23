package net.oneironaut.casting.patterns.spells.great

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.spell.ParticleSpray
import at.petrak.hexcasting.api.spell.RenderedSpell
import at.petrak.hexcasting.api.spell.SpellAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getLivingEntityButNotArmorStand
import at.petrak.hexcasting.api.spell.iota.Iota
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectInstance
import net.oneironaut.Oneironaut
import net.oneironaut.registry.OneironautMiscRegistry
import kotlin.math.max

private val markerEffect: StatusEffect = OneironautMiscRegistry.NOT_MISSING.get()

class OpMarkEntity() : SpellAction {
    override val argc = 1
    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>>? {
        val target = args.getLivingEntityButNotArmorStand(0, argc)
        ctx.assertEntityInRange(target)
        val existingLevel = if (target.hasStatusEffect(markerEffect)) {
            max(target.getStatusEffect(markerEffect)!!.amplifier, 128)
        } else {
            -1
        }
        //Oneironaut.boolLogger("Cost boost: $existingLevel", true)
        val cost = (existingLevel + 2) * MediaConstants.SHARD_UNIT
        return Triple(Spell(target, existingLevel + 1),
            cost,
            listOf(ParticleSpray.cloud(target.pos.add(0.0, target.eyeY / 2, 0.0), 1.0)))
    }

    private class Spell(val target : LivingEntity, val levelToApply : Int) : RenderedSpell{
        override fun cast(ctx: CastingContext) {
            val instance = StatusEffectInstance(markerEffect, 1200, levelToApply)
            target.addStatusEffect(instance)
        }

    }

}