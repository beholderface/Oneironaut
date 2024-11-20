package net.beholderface.oneironaut.mixin;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.iota.NullIota;
import at.petrak.hexcasting.api.spell.iota.Vec3Iota;
import at.petrak.hexcasting.common.items.magic.ItemCreativeUnlocker;
import com.llamalad7.mixinextras.sugar.Local;
import kotlin.collections.CollectionsKt;
import net.beholderface.oneironaut.Oneironaut;
import net.beholderface.oneironaut.item.MemoryFragmentItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.beholderface.oneironaut.MiscAPIKt;
import net.beholderface.oneironaut.Oneironaut;
import net.beholderface.oneironaut.item.MemoryFragmentItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(ItemCreativeUnlocker.class)
public abstract class CubeLoreGrantMixin {
    @ModifyVariable(method = "finishUsing", at = @At(value = "STORE", ordinal = 0))
    private ArrayList<Identifier> addMemoryFragmentNames(ArrayList<Identifier> array){
        //array.addAll(MemoryFragmentItem.NAMES);
        array.add(Oneironaut.id("lore/root"));
        return array;
    }
}
