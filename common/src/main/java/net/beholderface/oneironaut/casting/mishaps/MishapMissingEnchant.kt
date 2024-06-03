package net.beholderface.oneironaut.casting.mishaps

import at.petrak.hexcasting.api.misc.FrozenColorizer
import at.petrak.hexcasting.api.spell.ParticleSpray
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.Mishap
import net.minecraft.enchantment.Enchantment
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.DyeColor
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.explosion.Explosion

class MishapMissingEnchant(val stack: ItemStack, val enchant: Enchantment) : Mishap() {
    override fun accentColor(ctx: CastingContext, errorCtx: Context): FrozenColorizer = dyeColor(DyeColor.PURPLE)

    override fun particleSpray(ctx: CastingContext) =
        ParticleSpray.burst(stack.holder?.pos!!, 1.0)

    override fun errorMessage(ctx: CastingContext, errorCtx: Context): Text =
        error("oneironaut:missingenchant", stack.name, Text.translatable(enchant.translationKey))

    override fun execute(ctx: CastingContext, errorCtx: Context, stack: MutableList<Iota>) {
        ctx.caster.setExperienceLevel((ctx.caster.experienceLevel - 3).coerceAtLeast(0))
    }

    companion object {
        @JvmStatic
        fun of(stack: ItemStack, enchant: Enchantment): MishapMissingEnchant {
            return MishapMissingEnchant(stack, enchant)
        }
    }

}