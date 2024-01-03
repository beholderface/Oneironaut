package net.oneironaut.mixin;

/*
import at.petrak.hexcasting.api.item.MediaHolderItem;
import at.petrak.hexcasting.fabric.cc.adimpl.CCMediaHolder;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.oneironaut.item.BottomlessMediaItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CCMediaHolder.ItemBased.class)
public class BottomlessMediaPriorityMixin {
    @Shadow @Final private MediaHolderItem mediaHolder;
    @Unique
    private final CCMediaHolder holder = (CCMediaHolder) (Object) this;

    @ModifyReturnValue(method = "getConsumptionPriority", at = @At(value = "RETURN", remap = false), remap = false)
    private int prioritizeBottomless(int original){
        if (this.mediaHolder instanceof BottomlessMediaItem bottomlessMediaItem){
            return BottomlessMediaItem.priority;
        }
        return original;
    }
}
*/
//superseded by the one in the fabric package, because Forge doesn't know what a component is and it was causing issues