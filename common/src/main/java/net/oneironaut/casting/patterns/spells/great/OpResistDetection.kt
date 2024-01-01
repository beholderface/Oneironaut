package net.oneironaut.casting.patterns.spells.great

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.spell.*
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.oneironaut.registry.OneironautItemRegistry
import net.oneironaut.registry.OneironautMiscRegistry
import kotlin.math.floor

class OpResistDetection : SpellAction {
    override val argc = 2
    override val isGreat = true
    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>>? {
        val target = args.getLivingEntityButNotArmorStand(0, argc)
        ctx.assertEntityInRange(target)
        val duration = args.getPositiveDouble(1, argc)
        val cost = duration * MediaConstants.DUST_UNIT * 2

        return Triple(
            Spell(target, floor(duration * 20).toInt()),
            (cost).toInt(),
            listOf(ParticleSpray.cloud(target.pos, 2.0))
        )
    }

    private data class Spell(val target: LivingEntity, val duration : Int) : RenderedSpell {
        override fun cast(ctx: CastingContext) {
            if (duration > 0){
                target.addStatusEffect(StatusEffectInstance(OneironautMiscRegistry.DETECTION_RESISTANCE.get(), duration), ctx.caster)
            }
        }
    }
}