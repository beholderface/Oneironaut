package net.beholderface.oneironaut.fabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.beholderface.oneironaut.registry.OneironautBlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.FrostWalkerEnchantment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = FrostWalkerEnchantment.class)
public class SlurryFrostWalkerMixin {

    @Unique private static Block oneironaut$frozenSlurry = null;
    @Unique private static BlockState oneironaut$frozenSlurryState = null;
    @Unique private static void oneironaut$init(){
        oneironaut$frozenSlurry = OneironautBlockRegistry.MEDIA_ICE_FROSTED.get();
        oneironaut$frozenSlurryState = oneironaut$frozenSlurry.getDefaultState();
    }
    @WrapOperation(method = "freezeWater", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z",
            remap = true), remap = true)
    private static boolean noFrostedSlurry(World world, BlockPos pos, BlockState state, Operation<Boolean> original){
        if (oneironaut$frozenSlurryState == null){
            oneironaut$init();
        }
        if (world.getBlockState(pos).getBlock() == OneironautBlockRegistry.THOUGHT_SLURRY_BLOCK.get()){
            boolean output = world.setBlockState(pos, oneironaut$frozenSlurryState);
            world.createAndScheduleBlockTick(pos, oneironaut$frozenSlurry, MathHelper.nextInt(world.getRandom(), 60, 120));
            return output;
        }
        return original.call(world, pos, state);
    }
}
