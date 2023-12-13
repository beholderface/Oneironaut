package net.oneironaut.casting.patterns.spells

import at.petrak.hexcasting.api.misc.FrozenColorizer
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.spell.ParticleSpray
import at.petrak.hexcasting.api.spell.RenderedSpell
import at.petrak.hexcasting.api.spell.SpellAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getVec3
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.utils.get
import at.petrak.hexcasting.api.utils.getCompound
import at.petrak.hexcasting.api.utils.getUUID
import at.petrak.hexcasting.common.blocks.BlockConjured
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.oneironaut.block.WispLantern
import net.oneironaut.block.WispLanternEntity
import net.oneironaut.casting.mishaps.MishapNonconjured
import net.oneironaut.registry.OneironautThingRegistry
import ram.talia.hexal.api.spell.iota.ItemTypeIota

class OpSplatoon : SpellAction {
    override val argc = 2
    //override val mediaCost = 1 * MediaConstants.DUST_UNIT
    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>>? {
        val target = args.getVec3(0, argc)
        val colorItem = args[1]
        var colorItemFinal = Items.BARRIER
        var costMultiplier = 1.0f

        ctx.assertVecInRange(target)
        if ((ctx.world.getBlockState(BlockPos(target)).block !is BlockConjured) &&
            (ctx.world.getBlockState(BlockPos(target)).block != OneironautThingRegistry.WISP_LANTERN) &&
            (ctx.world.getBlockState(BlockPos(target)).block != OneironautThingRegistry.WISP_LANTERN_TINTED)){
            throw MishapNonconjured.of(BlockPos(target))
        }

        if (colorItem.type == ItemTypeIota.TYPE){
            if (IXplatAbstractions.INSTANCE.isColorizer((colorItem as ItemTypeIota).item?.defaultStack)){
                colorItemFinal = colorItem.item
            }
        }
        var lantern = true
        if (ctx.world.getBlockState(BlockPos(target)).block is BlockConjured){
            lantern = false
            if (ctx.world.getBlockEntity(BlockPos(target))?.createNbtWithIdentifyingData().getCompound("tag_colorizer").getUUID("owner") == ctx.caster.uuid){
                costMultiplier = 0.1f
            }
        }
        //ctx.caster.sendMessage(Text.of("$costMultiplier"))

        return Triple(
            Spell(BlockPos(target), colorItemFinal, lantern),
            (MediaConstants.DUST_UNIT * costMultiplier).toInt(),
            listOf(ParticleSpray.cloud(target, 2.0))
        )
    }

    private data class Spell(val target: BlockPos, val colorizer : Item?, val lantern : Boolean) : RenderedSpell {
        override fun cast(ctx: CastingContext) {
            if (ctx.world.getBlockState(target).block is BlockConjured) {
                if (colorizer != Items.BARRIER){
                    //val initialColorizer = IXplatAbstractions.INSTANCE.getColorizer(ctx.caster)
                    BlockConjured.setColor(ctx.world, target, FrozenColorizer(colorizer?.defaultStack, ctx.caster.uuid))
                } else {
                    BlockConjured.setColor(ctx.world, target, IXplatAbstractions.INSTANCE.getColorizer(ctx.caster))
                }
            } else {
                //hehe janky workaround go brr
                val prevItemstack = ctx.caster.getStackInHand(Hand.MAIN_HAND)
                val colorizerStack = colorizer?.defaultStack
                ctx.caster.setStackInHand(Hand.MAIN_HAND, colorizerStack)
                ctx.world.getBlockState(target).onUse(ctx.world, ctx.caster, Hand.MAIN_HAND,
                    BlockHitResult(Vec3d(target.x.toDouble() + 0.5, target.y.toDouble() + 0.3, target.z.toDouble() + 0.5), Direction.DOWN, target, true))
                ctx.caster.setStackInHand(Hand.MAIN_HAND, prevItemstack)
                //ctx.world.sendPacket(ctx.world.getBlockEntity(target)?.toUpdatePacket())
                ctx.caster.sendChunkPacket(ChunkPos(target), ctx.world.getBlockEntity(target)?.toUpdatePacket())
                /*val lantern : WispLanternEntity = ctx.world.getBlockEntity(target) as WispLanternEntity
                lantern.setColor(colorizer?.defaultStack, ctx.caster)
                lantern.markDirty()*/
            }
        }
    }
}