package net.beholderface.oneironaut.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.beholderface.oneironaut.registry.OneironautBlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.FrostWalkerEnchantment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = FrostWalkerEnchantment.class)
public class SlurryFrostWalkerMixin {

    @Unique private static BlockState oneironaut$frozenSlurry = null;
    @Unique private static void oneironaut$init(){
        oneironaut$frozenSlurry = OneironautBlockRegistry.MEDIA_ICE_FROSTED.get().getDefaultState();
    }
    @WrapOperation(method = "freezeWater(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z",
            remap = true), remap = true)
    private static boolean noFrostedSlurry(World world, BlockPos pos, BlockState state, Operation<Boolean> original){
        if (oneironaut$frozenSlurry == null){
            oneironaut$init();
        }
        if (world.getBlockState(pos).getBlock() == OneironautBlockRegistry.THOUGHT_SLURRY_BLOCK.get()){
            return world.setBlockState(pos, oneironaut$frozenSlurry);
        }
        return original.call(world, pos, state);
    }
}
