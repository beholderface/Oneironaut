package net.oneironaut.casting.patterns.spells

import at.petrak.hexcasting.api.misc.FrozenColorizer
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.spell.ParticleSpray
import at.petrak.hexcasting.api.spell.RenderedSpell
import at.petrak.hexcasting.api.spell.SpellAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getVec3
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.NullIota
import at.petrak.hexcasting.common.blocks.BlockConjured
import at.petrak.hexcasting.common.lib.HexItems
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.oneironaut.casting.mishaps.MishapNonconjured
import ram.talia.hexal.api.getItemType
import ram.talia.hexal.api.spell.iota.ItemTypeIota

class OpSplatoon : SpellAction {
    override val argc = 2
    //override val mediaCost = 1 * MediaConstants.DUST_UNIT
    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>>? {
        val target = args.getVec3(0, argc)
        val colorItem = args[1]

        ctx.assertVecInRange(target)
        if (ctx.world.getBlockState(BlockPos(target)).block !is BlockConjured){
            throw MishapNonconjured.of(BlockPos(target))
        }

        val noColorizerReturn = Triple(
            Spell(BlockPos(target), Items.BARRIER),
            MediaConstants.DUST_UNIT,
            listOf(ParticleSpray.cloud(target, 2.0))
        )

        return if (colorItem.type == ItemTypeIota.TYPE) {
            val colorItemStack = (colorItem as ItemTypeIota).item?.defaultStack
            if (IXplatAbstractions.INSTANCE.isColorizer(colorItemStack)){
                Triple(
                    Spell(BlockPos(target), colorItemStack?.item),
                    MediaConstants.DUST_UNIT,
                    listOf(ParticleSpray.cloud(target, 2.0))
                )
            } else {
                noColorizerReturn
            }
        } else {
            noColorizerReturn
        }
    }

    private data class Spell(val target: BlockPos, val colorizer : Item?) : RenderedSpell {
        override fun cast(ctx: CastingContext) {
            if (ctx.world.getBlockState(target).block is BlockConjured) {
                if (colorizer != Items.BARRIER){
                    //val initialColorizer = IXplatAbstractions.INSTANCE.getColorizer(ctx.caster)
                    BlockConjured.setColor(ctx.world, target, FrozenColorizer(colorizer?.defaultStack, ctx.caster.uuid))
                } else {
                    BlockConjured.setColor(ctx.world, target, IXplatAbstractions.INSTANCE.getColorizer(ctx.caster))
                }
            }
        }

    }
}