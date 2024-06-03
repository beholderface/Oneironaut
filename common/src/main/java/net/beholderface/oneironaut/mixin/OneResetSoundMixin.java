package net.beholderface.oneironaut.mixin;

import at.petrak.hexcasting.api.spell.casting.sideeffects.OperatorSideEffect;
import at.petrak.hexcasting.common.items.ItemStaff;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.beholderface.oneironaut.item.GeneralNoisyStaff;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.beholderface.oneironaut.item.GeneralNoisyStaff;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;


@SuppressWarnings("ConstantConditions")
@Mixin(value = ItemStaff.class)
public abstract class OneResetSoundMixin {

    @WrapOperation(method = "use", at = @At(value="INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;playSound(Lnet/minecraft/sound/SoundEvent;FF)V", remap = false), remap = false)
    private void dontSoundIfNoisyStaff(PlayerEntity instance, SoundEvent sound, float volume, float pitch, Operation<Void> original, @Local PlayerEntity player, @Local Hand hand){
        if (!(player.getStackInHand(hand).getItem() instanceof GeneralNoisyStaff)){
            original.call(instance, sound, volume, pitch);
        }
    }
}
