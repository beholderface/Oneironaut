package net.beholderface.oneironaut.casting.patterns.status

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.asActionResult
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getPositiveDouble
import at.petrak.hexcasting.api.spell.getVec3
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.EntityIota
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.beholderface.oneironaut.getStatusEffect
import net.beholderface.oneironaut.registry.OneironautItemRegistry
import net.beholderface.oneironaut.registry.OneironautMiscRegistry

class OpGetEntitiesByStatus(val invert : Boolean) : ConstMediaAction {
    override val argc = 3
    override val mediaCost = 0;
    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val status = args.getStatusEffect(0, argc, false)
        val target = args.getVec3(1, argc)
        ctx.assertVecInRange(target)
        val radius = args.getPositiveDouble(2, argc)
        val box = Box(target.add(Vec3d(-radius, -radius, -radius)), target.add(Vec3d(radius, radius, radius)))
        val entities = ctx.world.getOtherEntities(null, box) {
            isAffectedAndReachable(it, status, ctx, invert) && it.squaredDistanceTo(target) <= (radius * radius)}.sortedBy { it.squaredDistanceTo(target) }
        return entities.map(::EntityIota).asActionResult
    }
    companion object{
        fun isAffectedAndReachable(e: Entity, s : StatusEffect, ctx : CastingContext, invert : Boolean) : Boolean{
            if (e.isLiving && ctx.isEntityInRange(e)){
                val le = e as LivingEntity
                //I would not be so foolish :)
                if (le.hasStatusEffect(OneironautMiscRegistry.DETECTION_RESISTANCE.get())){
                    return false
                }
                var result = le.hasStatusEffect(s)
                if (invert){
                    result = !result
                }
                return result
            } else {
                return false
            }
        }
    }
}