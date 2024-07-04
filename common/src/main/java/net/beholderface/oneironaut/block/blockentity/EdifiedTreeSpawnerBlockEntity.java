package net.beholderface.oneironaut.block.blockentity;

import at.petrak.hexcasting.common.misc.AkashicTreeGrower;
import net.beholderface.oneironaut.registry.OneironautBlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EdifiedTreeSpawnerBlockEntity extends BlockEntity {
    public EdifiedTreeSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(OneironautBlockRegistry.EDIFIED_TREE_SPAWNER_ENTITY.get(), pos, state);
    }
    public void tick(World world, BlockPos pos, BlockState state){
        if (world instanceof ServerWorld serverWorld){
            //world.setBlockState(pos, Blocks.OAK_SAPLING.getDefaultState());
            AkashicTreeGrower.INSTANCE.generate(serverWorld, serverWorld.getChunkManager().getChunkGenerator(), pos, state, world.random);
        }
    }
}
