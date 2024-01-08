package net.oneironaut.mixin;

import at.petrak.hexcasting.common.misc.Brainsweeping;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = Brainsweeping.class)
public abstract class BrainsweepTargetMixin {
    //gotta do this so that things flayed via Mind Render actually stay flayed
    @ModifyReturnValue(method = "isValidTarget", at = @At(value = "RETURN", remap = false), remap = false)
    private static boolean alwaysValid(boolean original){
        return true;
    }
}
