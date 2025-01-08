package net.beholderface.oneironaut.casting.patterns.rod

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.casting.iota.Vec3Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import at.petrak.hexcasting.api.utils.*
import net.beholderface.oneironaut.casting.mishaps.MishapNoRod
import net.beholderface.oneironaut.isUsingRod
import net.beholderface.oneironaut.item.ReverberationRod
import net.beholderface.oneironaut.registry.OneironautItemRegistry

class OpGetInitialRodState(val mode: Int) : ConstMediaAction {
    override val argc = 0

    override fun execute(args: List<Iota>, ctx: CastingEnvironment): List<Iota> {
        if (ctx.castingEntity == null){
            throw MishapBadCaster()
        }
        if (isUsingRod(ctx)){
            val rodStack  = ctx.castingEntity!!.activeItem
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
            throw MishapNoRod(false)
        }
        return listOf(NullIota())
    }
}