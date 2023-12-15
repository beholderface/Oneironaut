package net.oneironaut.casting.patterns.rod

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.DoubleIota
import at.petrak.hexcasting.api.spell.iota.NullIota
import at.petrak.hexcasting.api.spell.iota.Vec3Iota
import at.petrak.hexcasting.api.utils.*
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.oneironaut.casting.mishaps.MishapNoRod
import net.oneironaut.item.ReverberationRod
import net.oneironaut.registry.OneironautThingRegistry

class OpGetInitialRodState(val mode: Int) : ConstMediaAction {
    override val argc = 0

    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val mainStack = ctx.caster.getStackInHand(Hand.MAIN_HAND)
        val offStack = ctx.caster.getStackInHand(Hand.OFF_HAND)
        val rod = OneironautThingRegistry.REVERBERATION_ROD.get()
        if (mainStack.item == rod.asItem() || offStack.item == rod.asItem()){
            val rodStack : ItemStack = if (mainStack.item == rod.asItem()){
                mainStack
            } else {
                offStack
            }
            val rodNbt = rodStack.nbt
            if (rodNbt != null){
                val initialLook = Vec3Iota(vecFromNBT(rodNbt.getLongArray("initialLook")))
                val initialPos = Vec3Iota(vecFromNBT(rodNbt.getLongArray("initialPos")))
                val initialTime = DoubleIota(rodNbt.getDouble("initialTime"))
                when(mode){
                    1 -> return listOf(initialLook)
                    2 -> return listOf(initialPos)
                    3 -> return listOf(initialTime)
                }
            } else {
                return listOf(NullIota())
            }
            //val initialPos = Vec3Iota(rodStack.nbt?.get("initialPos")?.downcast().)

        } else {
            //throw mishap
            throw MishapNoRod()
        }
        return listOf(NullIota())
    }
}