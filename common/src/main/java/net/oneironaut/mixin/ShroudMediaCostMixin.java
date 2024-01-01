package net.oneironaut.mixin;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.casting.CastingHarness;
import at.petrak.hexcasting.common.items.ItemStaff;
import at.petrak.hexcasting.common.lib.HexItems;
import net.minecraft.item.ItemStack;
import net.oneironaut.registry.OneironautItemRegistry;
import net.oneironaut.registry.OneironautMiscRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = CastingHarness.class)
public abstract class ShroudMediaCostMixin {

    private final CastingHarness harness = (CastingHarness) (Object) this;
    @ModifyVariable(method = "withdrawMedia", at = @At(value = "STORE"), remap = false)
    private ItemStack fakeStaffIfShrouded(ItemStack stack){
        CastingContext ctx = harness.getCtx();
        if (ctx.getSource().equals(CastingContext.CastSource.STAFF) &&
                !(stack.getItem() instanceof ItemStaff) &&
                ctx.getCaster().hasStatusEffect(OneironautMiscRegistry.DETECTION_RESISTANCE.get())
        ){
            return HexItems.STAFF_EDIFIED.getDefaultStack();
        } else {
            return stack;
        }
    }
}
