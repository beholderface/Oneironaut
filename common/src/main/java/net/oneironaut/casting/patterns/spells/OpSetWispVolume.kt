package net.oneironaut.casting.patterns.spells

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.spell.*
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import dev.onyxstudios.cca.api.v3.component.ComponentKey
import net.minecraft.entity.Entity
import net.oneironaut.Oneironaut
import net.oneironaut.casting.DoubleComponent
import ram.talia.hexal.api.getBaseCastingWisp
import ram.talia.hexal.common.entities.BaseCastingWisp
import java.lang.IllegalStateException
import kotlin.math.abs



//doesn't work because SoundInstance objects can't have their volume field changed >:(
class OpSetWispVolume() : SpellAction {
    //public static final ComponentKey<DoubleComponent> WISP_VOLUME = ComponentRegistry.getOrCreate(new Identifier("oneironaut", "wisp_volume"), DoubleComponent.class);
    override val argc = 2
    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>>? {
        val volumeComponent = DoubleComponent.VOLUME
        //idk why lower bound is exclusive but upper bound isn't
        val newVolume = args.getDoubleBetween(1, 0.1999, 5.0, argc)
        val wisp = args.getBaseCastingWisp(0, argc)
        val wispVolumeMaybe = volumeComponent.maybeGet(wisp)
        if (wispVolumeMaybe.isPresent){
            val wispVolume = wispVolumeMaybe.get()
            val oldVolume = wispVolume.value
            val cost = (abs(oldVolume - newVolume) * MediaConstants.DUST_UNIT).toInt()
            return Triple(Spell(wisp, newVolume, wispVolume, volumeComponent), cost, listOf(ParticleSpray.burst((wisp as Entity).pos, 1.5, 32)))
        } else {
            //idk how this works or if this will ever come up under normal conditions
            Oneironaut.LOGGER.info("no component? *megamind face*")
            throw IllegalStateException()
        }
    }

    private data class Spell(val wisp : BaseCastingWisp, val newVolume : Double, val volumeComponent: DoubleComponent, val componentKey : ComponentKey<DoubleComponent>) : RenderedSpell {
        override fun cast(ctx: CastingContext) {
            Oneironaut.LOGGER.info("Set volume component value to $newVolume")
            volumeComponent.value = newVolume
        }
    }
}