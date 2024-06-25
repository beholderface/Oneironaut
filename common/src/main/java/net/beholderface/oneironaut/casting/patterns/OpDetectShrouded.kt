package net.beholderface.oneironaut.casting.patterns

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.asActionResult
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getPositiveDouble
import at.petrak.hexcasting.api.spell.getVec3
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.Vec3Iota
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.beholderface.oneironaut.registry.OneironautMiscRegistry

class OpDetectShrouded : ConstMediaAction {
    override val argc = 2
    override val mediaCost = MediaConstants.DUST_UNIT / 10;
    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val target = args.getVec3(0, argc)
        ctx.assertVecInRange(target)
        val radius = args.getPositiveDouble(1, argc)
        val box = Box(target.add(Vec3d(-radius, -radius, -radius)), target.add(Vec3d(radius, radius, radius)))
        val entities = ctx.world.getOtherEntities(null, box) {
            isShroudedAndReachable(it, ctx) && it.squaredDistanceTo(target) <= (radius * radius)}.sortedBy { it.squaredDistanceTo(target) }
        val directions = mutableSetOf<Vec3d>()
        for (element in entities){
            directions.add(element.pos.subtract(target).normalize())
        }
        return directions.map(::Vec3Iota).asActionResult
    }
    companion object{
        fun isShroudedAndReachable(e: Entity, ctx : CastingContext) : Boolean{
            if (e.isLiving && ctx.isEntityInRange(e)){
                val le = e as LivingEntity
                return le.hasStatusEffect(OneironautMiscRegistry.DETECTION_RESISTANCE.get())
            } else {
                return false
            }
        }
    }
}