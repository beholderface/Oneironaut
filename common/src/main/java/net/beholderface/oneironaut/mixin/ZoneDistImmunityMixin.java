package net.beholderface.oneironaut.mixin;

import at.petrak.hexcasting.api.spell.iota.EntityIota;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.common.casting.operators.selectors.OpGetEntitiesBy;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.beholderface.oneironaut.registry.OneironautMiscRegistry;
import net.minecraft.entity.LivingEntity;
import net.beholderface.oneironaut.registry.OneironautMiscRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import ram.talia.hexal.common.casting.actions.OpGetEntitiesByDyn;

import java.util.Collection;

//TIL that I can target multiple classes with one mixin class
@Mixin(value = {OpGetEntitiesBy.class, OpGetEntitiesByDyn.class})
public abstract class ZoneDistImmunityMixin {
    /*@Redirect(method="execute", at = @At(value = "INVOKE", target = "Ljava/util/Collection;add(Ljava/lang/Object;)Z", remap = false), remap = false)
    private boolean nullImmune(Collection<Iota> instance, Object o){
        EntityIota e = (EntityIota) o;
        if (e.getEntity() instanceof LivingEntity le){
            if (le.hasStatusEffect(OneironautMiscRegistry.DETECTION_RESISTANCE.get())){
                return false;
            }
        }
        return instance.add(e);
    }*/
    //does the same thing but more friendly to other mixins, I think
    @WrapOperation(method = "execute", at = @At(value = "INVOKE", target = "Ljava/util/Collection;add(Ljava/lang/Object;)Z", remap = false), remap = false)
    private boolean ignoreImmune(Collection<Iota> instance, Object o, Operation<Boolean> original){
        EntityIota e = (EntityIota) o;
        if (e.getEntity() instanceof LivingEntity le){
            if (le.hasStatusEffect(OneironautMiscRegistry.DETECTION_RESISTANCE.get())){
                return false;
            }
        }
        return original.call(instance, o);
    }
}