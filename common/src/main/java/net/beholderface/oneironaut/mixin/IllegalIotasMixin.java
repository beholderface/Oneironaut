package net.beholderface.oneironaut.mixin;

import at.petrak.hexcasting.api.casting.iota.GarbageIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import net.beholderface.oneironaut.casting.iotatypes.DimIota;
import net.beholderface.oneironaut.casting.iotatypes.SoulprintIota;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//@Mixin(targets = "ram.talia.hexal.api.spell.mishaps.MishapIllegalInterworldIota$Companion")
public class IllegalIotasMixin {
    /*@Inject(method = "replaceInNestedIota", at = @At(value = "HEAD", remap = false), remap = false, cancellable = true)
    public void illegal(Iota iota, CallbackInfoReturnable<Iota> cir){
        if (iota instanceof DimIota || iota instanceof SoulprintIota){
            cir.setReturnValue(new GarbageIota());
        }
    }*/
}
