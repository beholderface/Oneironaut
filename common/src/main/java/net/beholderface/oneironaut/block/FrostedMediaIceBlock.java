package net.beholderface.oneironaut.block;

import at.petrak.hexcasting.common.lib.HexBlocks;
import net.beholderface.oneironaut.registry.OneironautBlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FrostedIceBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FrostedMediaIceBlock extends FrostedIceBlock {
    public FrostedMediaIceBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected void melt(BlockState state, World world, BlockPos pos) {
        if (world.getDimension().ultrawarm()) {
            world.removeBlock(pos, false);
            return;
        }
        world.setBlockState(pos, OneironautBlockRegistry.THOUGHT_SLURRY_BLOCK.get().getDefaultState());
        world.updateNeighbor(pos, OneironautBlockRegistry.THOUGHT_SLURRY_BLOCK.get(), pos);
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
        super.afterBreak(world, player, pos, state, blockEntity, stack);
        if (EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, stack) == 0) {
            if (world.getDimension().ultrawarm()) {
                world.removeBlock(pos, false);
                return;
            }
            BlockState material = world.getBlockState(pos.down());
            if (material.blocksMovement() || material.isLiquid()) {
                world.setBlockState(pos, OneironautBlockRegistry.THOUGHT_SLURRY_BLOCK.get().getDefaultState());
            }
        }
    }
}
