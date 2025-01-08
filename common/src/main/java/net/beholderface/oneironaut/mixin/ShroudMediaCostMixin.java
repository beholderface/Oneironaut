package net.beholderface.oneironaut.mixin;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.env.StaffCastEnv;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.common.items.ItemStaff;
import at.petrak.hexcasting.common.lib.HexItems;
import net.beholderface.oneironaut.registry.OneironautMiscRegistry;
import net.minecraft.item.ItemStack;
import net.beholderface.oneironaut.registry.OneironautItemRegistry;
import net.beholderface.oneironaut.registry.OneironautMiscRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = CastingVM.class)
public abstract class ShroudMediaCostMixin {

    private final CastingVM harness = (CastingVM) (Object) this;
    /*@ModifyVariable(method = "withdrawMedia", at = @At(value = "STORE"), remap = false)
    private ItemStack fakeStaffIfShrouded(ItemStack stack){
        CastingEnvironment ctx = harness.getEnv();
        if (ctx instanceof StaffCastEnv && ctx.getCastingEntity() != null &&
                !(stack.getItem() instanceof ItemStaff) &&
                ctx.getCastingEntity().hasStatusEffect(OneironautMiscRegistry.DETECTION_RESISTANCE.get())
        ){
            return HexItems.STAFF_EDIFIED.getDefaultStack();
        } else {
            return stack;
        }
    }*/
}
