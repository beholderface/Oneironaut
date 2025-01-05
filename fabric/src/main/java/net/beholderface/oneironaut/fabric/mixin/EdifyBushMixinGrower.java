package net.beholderface.oneironaut.fabric.mixin;

import net.beholderface.oneironaut.SharedMixinData;
import net.beholderface.oneironaut.registry.OneironautBlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SaplingGenerator.class)
public class EdifyBushMixinGrower {
    @Inject(method = "generate", at = @At(value = "HEAD", remap = true), cancellable = true, remap = true)
    public void produceBush(ServerWorld world, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state, Random random, CallbackInfoReturnable<Boolean> cir){
        if (state.getBlock() == Blocks.SWEET_BERRY_BUSH && SharedMixinData.edifyingBush){
            world.setBlockState(pos, OneironautBlockRegistry.RENDER_BUSH.get().getDefaultState().with(SweetBerryBushBlock.AGE, 1));
            SharedMixinData.edifyingBush = false;
            cir.setReturnValue(true);
        }
    }
}
