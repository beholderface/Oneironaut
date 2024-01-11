package net.oneironaut.casting.patterns.rod

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.DoubleIota
import at.petrak.hexcasting.api.spell.iota.NullIota
import at.petrak.hexcasting.api.spell.iota.Vec3Iota
import at.petrak.hexcasting.api.utils.*
import net.oneironaut.casting.mishaps.MishapNoRod
import net.oneironaut.item.ReverberationRod
import net.oneironaut.registry.OneironautItemRegistry

class OpGetInitialRodState(val mode: Int) : ConstMediaAction {
    override val argc = 0

    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val rod = OneironautItemRegistry.REVERBERATION_ROD.get()
        if (ctx.caster.activeItem.item == rod.asItem() && ctx.source == CastingContext.CastSource.PACKAGED_HEX){
            val rodStack  = ctx.caster.activeItem
            val rodNbt = rodStack.nbt
            val state = ReverberationRod.getState(ctx.caster)
            if (rodNbt != null){
                when(mode){
                    1 -> return listOf(Vec3Iota(state.initialLook))
                    2 -> return listOf(Vec3Iota(state.initialPos))
                    3 -> return listOf(DoubleIota(state.timestamp.toDouble()))
                }
            } else {
                return listOf(NullIota())
            }

        } else {
            //throw mishap
            throw MishapNoRod()
        }
        return listOf(NullIota())
    }
}