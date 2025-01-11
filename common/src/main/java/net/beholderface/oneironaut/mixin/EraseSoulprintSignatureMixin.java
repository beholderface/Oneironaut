package net.beholderface.oneironaut.mixin;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "at.petrak.hexcasting.common.casting.actions.spells.OpErase$Spell")
public class EraseSoulprintSignatureMixin {
    @Final
    @Shadow
    private ItemStack stack;

    @Inject(method = "cast(Lat/petrak/hexcasting/api/casting/eval/CastingEnvironment;)V", at = @At(value = "TAIL", remap = false), remap = false)
    public void eraseSignature(CastingEnvironment ctx, CallbackInfo ci){
        NbtCompound nbt = stack.getOrCreateNbt();
        if (nbt.containsUuid("soulprint_signature")){
            nbt.remove("soulprint_signature");
            stack.setNbt(nbt);
        }
    }
}
