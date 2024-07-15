package net.beholderface.oneironaut.casting.patterns.spells.great

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.spell.ParticleSpray
import at.petrak.hexcasting.api.spell.RenderedSpell
import at.petrak.hexcasting.api.spell.SpellAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getEntity
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.EnchantmentLevelEntry
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.Entity
import net.minecraft.item.EnchantedBookItem
import net.minecraft.item.ItemStack
import net.beholderface.oneironaut.network.ItemUpdatePacket
import net.beholderface.oneironaut.casting.mishaps.MishapMissingEnchant
import net.beholderface.oneironaut.registry.OneironautMiscRegistry
import ram.talia.hexal.api.getItemEntityOrItemFrame
import kotlin.math.pow

class OpApplyOvercastDamage : SpellAction {
    override val argc = 1
    override val isGreat = true
    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>> {
        val holder = args.getEntity(0, argc)
        val target = args.getItemEntityOrItemFrame(0, argc)
        ctx.assertEntityInRange(holder)
        val stack : ItemStack = if (!(target.left().isEmpty)){
            target.left().get().stack
        } else {
            target.right().get().heldItemStack
        }
        val level : Int
        if (stack.item is EnchantedBookItem){
            val stackEnchants = EnchantmentHelper.fromNbt(EnchantedBookItem.getEnchantmentNbt(stack))
            if (stackEnchants.contains(Enchantments.SHARPNESS)){
                level = stackEnchants[Enchantments.SHARPNESS]!!
            } else {
                throw MishapMissingEnchant(stack, Enchantments.SHARPNESS)
            }
            return Triple(
                Spell(stack, level, true, stackEnchants, holder),
                (level.toDouble().pow(2) * MediaConstants.CRYSTAL_UNIT * 15).toInt(),
                listOf(ParticleSpray.cloud(holder.pos, 2.0))
            )

        } else {
            val stackEnchants = EnchantmentHelper.fromNbt(stack.enchantments)
            if (stackEnchants.contains(Enchantments.SHARPNESS)){
                level = stackEnchants[Enchantments.SHARPNESS]!!
            } else {
                throw MishapMissingEnchant(stack, Enchantments.SHARPNESS)
            }
            return Triple(
                Spell(stack, level, false, stackEnchants, holder),
                (level.toDouble().pow(2) * MediaConstants.CRYSTAL_UNIT * 10).toInt(),
                listOf(ParticleSpray.cloud(holder.pos, 2.0))
            )
        }
    }

    private data class Spell(val stack : ItemStack, val level : Int, val book : Boolean, val existingEnchantments : Map<Enchantment, Int>, val holder : Entity) : RenderedSpell{
        override fun cast(ctx: CastingContext) {
            val overcastdamage = OneironautMiscRegistry.OVERCAST_DAMAGE_ENCHANT.get()
            val enchantsToKeep = HashMap<Enchantment, Int>()
            existingEnchantments.map {
                if(overcastdamage.canAccept(it.key)){
                    enchantsToKeep.put(it.key, it.value)
                }
            }
            val nbt = stack.nbt
            val enchantKey = if(book){
                EnchantedBookItem.STORED_ENCHANTMENTS_KEY
            } else {
                "Enchantments"
            }
            nbt!!.remove(enchantKey)
            if(book){
                EnchantedBookItem.addEnchantment(stack, EnchantmentLevelEntry(overcastdamage, level))
                enchantsToKeep.map {
                    EnchantedBookItem.addEnchantment(stack, EnchantmentLevelEntry(it.key, it.value))
                }
            } else {
                stack.addEnchantment(overcastdamage, level)
                enchantsToKeep.map {
                    stack.addEnchantment(it.key, it.value)
                }
            }
            IXplatAbstractions.INSTANCE.sendPacketNear(stack.holder?.pos, 128.0, ctx.world, ItemUpdatePacket(stack, holder))
        }

    }
}