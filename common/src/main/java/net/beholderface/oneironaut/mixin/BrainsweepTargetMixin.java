package net.beholderface.oneironaut.mixin;

import at.petrak.hexcasting.common.misc.Brainsweeping;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = Brainsweeping.class)
public abstract class BrainsweepTargetMixin {
    //gotta do this so that things flayed via Mind Render actually stay flayed
    @ModifyReturnValue(method = "isBrainswept", at = @At(value = "RETURN", remap = false), remap = false)
    //why does it even do the isValidTarget thing when the entity may already be brainswept
    private static boolean skipValidation(boolean original, @Local MobEntity mob){
        return IXplatAbstractions.INSTANCE.isBrainswept(mob);
    }
}
