package net.oneironaut.mixin;

import at.petrak.hexcasting.api.spell.iota.EntityIota;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.common.casting.operators.selectors.OpGetEntitiesBy;
import net.minecraft.entity.LivingEntity;
import net.oneironaut.registry.OneironautItemRegistry;
import net.oneironaut.registry.OneironautMiscRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ram.talia.hexal.common.casting.actions.OpGetEntitiesByDyn;

import java.util.Collection;

//TIL that I can target multiple classes with one mixin class
@Mixin(value = {OpGetEntitiesBy.class, OpGetEntitiesByDyn.class})
public abstract class ZoneDistImmunityMixin {
    @Redirect(method="execute", at = @At(value = "INVOKE", target = "Ljava/util/Collection;add(Ljava/lang/Object;)Z", remap = false), remap = false)
    private boolean nullImmune(Collection<Iota> instance, Object o){
        EntityIota e = (EntityIota) o;
        if (e.getEntity() instanceof LivingEntity le){
            if (le.hasStatusEffect(OneironautMiscRegistry.DETECTION_RESISTANCE.get())){
                return false;
            }
        }
        return instance.add(e);
    }
}