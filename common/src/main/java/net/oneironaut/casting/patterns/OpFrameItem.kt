package net.oneironaut.casting.patterns

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getEntity
import at.petrak.hexcasting.api.spell.getPositiveInt
import at.petrak.hexcasting.api.spell.iota.DoubleIota
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapBadEntity
import at.petrak.hexcasting.api.utils.putInt
import net.minecraft.entity.decoration.ItemFrameEntity
import net.minecraft.text.Text
import net.oneironaut.casting.mishaps.MishapNoRod
import net.oneironaut.registry.OneironautThingRegistry
import ram.talia.hexal.api.spell.iota.ItemTypeIota

class OpFrameItem() : ConstMediaAction {
    override val argc = 1
    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val initialEntity = args.getEntity(0, argc)
        ctx.assertEntityInRange(initialEntity)
        if (initialEntity is ItemFrameEntity){
            val frame = initialEntity as ItemFrameEntity
            return listOf(ItemTypeIota(frame.heldItemStack.item))
        } else {
            throw MishapBadEntity(initialEntity, Text.translatable("oneironaut.mishap.noitemframe"))
        }
    }
}