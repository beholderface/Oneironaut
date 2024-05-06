package net.oneironaut.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.oneironaut.registry.OneironautMiscRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class GlowAmbitEffectGlowMixin {

    /*@Unique
    private final LivingEntity oneironaut$entity = (LivingEntity) (Object) this;*/

    @Shadow public abstract boolean hasStatusEffect(StatusEffect effect);

    @ModifyReturnValue(method = "isGlowing()Z", at = @At("RETURN"))
    public boolean makeSpecialGlowingWork(boolean original){
        if (!original){
            if (this.hasStatusEffect(OneironautMiscRegistry.NOT_MISSING.get())){
                return true;
            }
        }
        return original;
    }
}
