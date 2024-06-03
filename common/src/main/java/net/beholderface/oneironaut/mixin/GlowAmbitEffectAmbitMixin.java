package net.beholderface.oneironaut.mixin;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.beholderface.oneironaut.registry.OneironautMiscRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CastingContext.class)
public abstract class GlowAmbitEffectAmbitMixin {

    @Unique
    private final CastingContext oneironaut$ctx = (CastingContext) (Object) this;

    @ModifyReturnValue(method = "isEntityInRange(Lnet/minecraft/entity/Entity;)Z", at = @At("RETURN"))
    public boolean makeSpecialAmbitWork(boolean original, @Local Entity entity){
        if (!original){
            //not sure if the dimension check is strictly necessary, but it can't hurt
            if (oneironaut$ctx.getCaster().world == entity.world && entity instanceof LivingEntity living){
                return living.hasStatusEffect(OneironautMiscRegistry.NOT_MISSING.get());
            }
        }
        return original;
    }
}
