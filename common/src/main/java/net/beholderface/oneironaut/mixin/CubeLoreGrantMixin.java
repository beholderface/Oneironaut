package net.beholderface.oneironaut.mixin;

import at.petrak.hexcasting.common.items.magic.ItemCreativeUnlocker;
import net.beholderface.oneironaut.Oneironaut;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;

@Mixin(ItemCreativeUnlocker.class)
public abstract class CubeLoreGrantMixin {
    @ModifyVariable(method = "finishUsing", at = @At(value = "STORE", ordinal = 0))
    private ArrayList<Identifier> addMemoryFragmentNames(ArrayList<Identifier> array){
        //array.addAll(MemoryFragmentItem.NAMES);
        array.add(Oneironaut.id("lore/root"));
        return array;
    }
}
