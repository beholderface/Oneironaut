package net.beholderface.oneironaut.fabric.mixin;

import at.petrak.hexcasting.api.item.MediaHolderItem;
import at.petrak.hexcasting.fabric.cc.adimpl.CCMediaHolder;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.beholderface.oneironaut.item.BottomlessMediaItem;
import net.beholderface.oneironaut.item.BottomlessMediaItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import dev.onyxstudios.cca.api.v3.item.ItemComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentInitializer;
import dev.onyxstudios.cca.api.v3.item.ItemComponent;

@Mixin(CCMediaHolder.ItemBased.class)
public class BottomlessMediaPriorityMixin {
    @Shadow(remap = false) @Final private MediaHolderItem mediaHolder;
    /*@Unique
    private final CCMediaHolder holder = (CCMediaHolder) (Object) this;*/

    @ModifyReturnValue(method = "getConsumptionPriority", at = @At(value = "RETURN", remap = false), remap = false)
    private int prioritizeBottomless(int original){
        if (this.mediaHolder instanceof BottomlessMediaItem bottomlessMediaItem){
            return BottomlessMediaItem.priority;
        }
        return original;
    }
}
