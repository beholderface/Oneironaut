package net.beholderface.oneironaut.casting.patterns.spells

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getList
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem
import at.petrak.hexcasting.api.casting.mishaps.MishapOthersName
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.beholderface.oneironaut.registry.OneironautItemRegistry
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

class OpWriteBottomlessTrinket  : SpellAction {
    override val argc = 1
    private val itemType: Item = OneironautItemRegistry.BOTTOMLESS_CASTING_ITEM.get()
    override fun execute(args: List<Iota>, ctx: CastingEnvironment): SpellAction.Result {
        val patterns = args.getList(0, argc).toList()

        val handStackInfo = ctx.getHeldItemToOperateOn() {
            val hexHolder = IXplatAbstractions.INSTANCE.findHexHolder(it)
            it.item == itemType && hexHolder != null && !hexHolder.hasHex()
        } ?: throw MishapBadOffhandItem(null, Text.of("no_item.offhand"))
        val handStack = handStackInfo.stack
        val hexHolder = IXplatAbstractions.INSTANCE.findHexHolder(handStack)
        if (handStack.item != itemType || (hexHolder == null || hexHolder.hasHex())) {
            throw MishapBadOffhandItem(handStack, Text.of("bad_item.offhand"))
        }

        val trueName = MishapOthersName.getTrueNameFromArgs(patterns, ctx.caster)
        if (trueName != null)
            throw MishapOthersName(trueName)

        return SpellAction.Result(Spell(patterns, handStack), MediaConstants.CRYSTAL_UNIT * 10, listOf(ParticleSpray.burst(ctx.mishapSprayPos(), 0.5)))
    }

    private inner class Spell(val patterns: List<Iota>, val stack: ItemStack) : RenderedSpell {
        override fun cast(ctx: CastingEnvironment) {
            val hexHolder = IXplatAbstractions.INSTANCE.findHexHolder(stack)
            val pigment = if (ctx.castingEntity != null && ctx.castingEntity is ServerPlayerEntity){
                IXplatAbstractions.INSTANCE.getPigment(ctx.castingEntity as ServerPlayerEntity)
            } else {
                null
            }
            if (hexHolder != null
                && !hexHolder.hasHex()
            ) {
                hexHolder.writeHex(patterns, pigment,1000)
            }
        }
    }
}