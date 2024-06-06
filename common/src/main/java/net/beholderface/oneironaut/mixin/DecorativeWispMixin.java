package net.beholderface.oneironaut.mixin;

import at.petrak.hexcasting.api.misc.MediaConstants;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import net.beholderface.oneironaut.components.BoolComponent;
import net.beholderface.oneironaut.registry.OneironautComponents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ram.talia.hexal.common.entities.WanderingWisp;

@Mixin(WanderingWisp.class)
public class DecorativeWispMixin {
    @Unique
    WanderingWisp wisp = (WanderingWisp) (Object) this;

    @Inject(method = "getMedia()I", at = @At(value = "HEAD", remap = false), cancellable = true, remap = false)
    public void nomedia(CallbackInfoReturnable<Integer> cir){
        //thank you [
        ComponentKey<BoolComponent> decorative = OneironautComponents.WISP_DECORATIVE;
        BoolComponent decoComponent = decorative.get(wisp);
        if (decoComponent.getValue()){
            //approximately net-zero media from consuming it rather than just eating a shard for no media
            cir.setReturnValue(MediaConstants.SHARD_UNIT);
        }
    }
}
