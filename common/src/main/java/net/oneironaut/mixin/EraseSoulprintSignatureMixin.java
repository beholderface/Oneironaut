package net.oneironaut.mixin;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "at.petrak.hexcasting.common.casting.operators.spells.OpErase$Spell")
public class EraseSoulprintSignatureMixin {
    @Final
    @Shadow
    private ItemStack stack;

    @Inject(method = "cast", at = @At(value = "TAIL", remap = false), remap = false)
    public void eraseSignature(CastingContext ctx, CallbackInfo ci){
        NbtCompound nbt = stack.getOrCreateNbt();
        if (nbt.containsUuid("soulprint_signature")){
            nbt.remove("soulprint_signature");
            stack.setNbt(nbt);
        }
    }
}
