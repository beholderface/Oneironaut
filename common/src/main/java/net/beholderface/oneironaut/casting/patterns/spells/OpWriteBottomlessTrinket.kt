package net.beholderface.oneironaut.casting.patterns.spells

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.spell.*
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapBadOffhandItem
import at.petrak.hexcasting.api.spell.mishaps.MishapOthersName
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.beholderface.oneironaut.registry.OneironautItemRegistry
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

class OpWriteBottomlessTrinket  : SpellAction {
    override val argc = 1
    private val itemType: Item = OneironautItemRegistry.BOTTOMLESS_CASTING_ITEM.get()
    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>> {
        val patterns = args.getList(0, argc).toList()

        val (handStack, hand) = ctx.getHeldItemToOperateOn {
            val hexHolder = IXplatAbstractions.INSTANCE.findHexHolder(it)
            it.item == itemType && hexHolder != null && !hexHolder.hasHex()
        }
        val hexHolder = IXplatAbstractions.INSTANCE.findHexHolder(handStack)
        if (handStack.item != itemType) {
            throw MishapBadOffhandItem(handStack, hand, itemType.name)
        } else if (hexHolder == null || hexHolder.hasHex()) {
            throw MishapBadOffhandItem.of(handStack, hand, "iota.write")
        }

        val trueName = MishapOthersName.getTrueNameFromArgs(patterns, ctx.caster)
        if (trueName != null)
            throw MishapOthersName(trueName)

        return Triple(Spell(patterns, handStack), MediaConstants.CRYSTAL_UNIT * 10, listOf(ParticleSpray.burst(ctx.caster.pos, 0.5)))
    }

    private inner class Spell(val patterns: List<Iota>, val stack: ItemStack) : RenderedSpell {
        override fun cast(ctx: CastingContext) {
            val hexHolder = IXplatAbstractions.INSTANCE.findHexHolder(stack)
            if (hexHolder != null
                && !hexHolder.hasHex()
            ) {
                hexHolder.writeHex(patterns, 1000)
            }
        }
    }
}