package net.oneironaut.casting.patterns.spells

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.spell.ParticleSpray
import at.petrak.hexcasting.api.spell.RenderedSpell
import at.petrak.hexcasting.api.spell.SpellAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getInt
import at.petrak.hexcasting.api.spell.iota.Iota
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.oneironaut.registry.OneironautThingRegistry
import kotlin.math.max
import kotlin.math.min

class OpCircle : SpellAction {
    override val argc = 1
    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>>? {
        val quantity = min(args.getInt(0, argc), 64)
        val cost = quantity * MediaConstants.DUST_UNIT
        return Triple(
            Spell(quantity),
            cost,
            listOf(ParticleSpray.cloud(ctx.caster.pos, 2.0))
        )
    }
    private data class Spell(val quantity : Int) : RenderedSpell{
        override fun cast(ctx: CastingContext){
            ctx.caster.giveItemStack(ItemStack(OneironautThingRegistry.CIRCLE_ITEM.get(), quantity))
        }
    }
}