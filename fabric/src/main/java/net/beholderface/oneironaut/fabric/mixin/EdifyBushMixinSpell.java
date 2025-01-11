package net.beholderface.oneironaut.fabric.mixin;

import at.petrak.hexcasting.common.casting.actions.spells.OpEdifySapling;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.beholderface.oneironaut.SharedMixinData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(OpEdifySapling.class)
public class EdifyBushMixinSpell {
    @WrapOperation(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isIn(Lnet/minecraft/registry/tag/TagKey;)Z", remap = true), remap = false)
    public boolean allowBush(BlockState state, TagKey<Block> tagKey, Operation<Boolean> original){
        if (state.getBlock() == Blocks.SWEET_BERRY_BUSH){
            SharedMixinData.edifyingBush = true;
            return true;
        }
        return original.call(state, tagKey);
    }
}
