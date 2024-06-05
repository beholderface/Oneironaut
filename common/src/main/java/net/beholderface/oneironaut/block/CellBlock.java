package net.beholderface.oneironaut.block;

import net.beholderface.oneironaut.block.blockentity.CellEntity;
import net.beholderface.oneironaut.registry.OneironautBlockRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CellBlock extends BlockWithEntity {
    public CellBlock(Settings settings){
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CellEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return !(world.isClient) ? (_world, _pos, _state, _be) -> ((CellEntity)_be).tick(_world, _pos, _state) : null;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state){
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState,
                                                WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        Optional<CellEntity> be = world.getBlockEntity(pos, OneironautBlockRegistry.CELL_ENTITY.get());
        if (be.isPresent()){
            be.get().updateNeighborMap();
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }
}
