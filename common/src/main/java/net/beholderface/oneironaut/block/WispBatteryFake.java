package net.beholderface.oneironaut.block;

import net.beholderface.oneironaut.block.blockentity.WispBatteryEntityFake;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ram.talia.hexal.common.entities.WanderingWisp;

import static net.beholderface.oneironaut.block.blockentity.WispBatteryEntity.getColors;

public class WispBatteryFake extends BlockWithEntity {
    public static final BooleanProperty REDSTONE_POWERED = Properties.POWERED;
    public WispBatteryFake(Settings settings){
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(REDSTONE_POWERED, false));
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(REDSTONE_POWERED);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state){
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context){
        VoxelShape lowerHalf = VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, 8.0 / 16.0, 1.0);
        VoxelShape phialBorder = VoxelShapes.cuboid(3.0 / 16, 8.0 / 16, 3.0 / 16, 13.0  / 16, 10.0 / 16, 13.0 / 16);
        VoxelShape phialUpper = VoxelShapes.cuboid(5.0/16, 8.0/16, 5.0/16, 11.0/16, 11.0/16, 11.0/16);
        VoxelShape antenna = VoxelShapes.cuboid(7.0/16, 8.0/16, 7.0/16, 9.0/16, 1.0, 9.0/16);
        return VoxelShapes.union(lowerHalf, phialBorder, phialUpper, antenna);
    }

    @Override
    public void neighborUpdate(BlockState pState, World pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos,
                               boolean pIsMoving) {
        super.neighborUpdate(pState, pLevel, pPos, pBlock, pFromPos, pIsMoving);

        if (!pLevel.isClient) {
            boolean currentlyPowered = pState.get(REDSTONE_POWERED);
            if (currentlyPowered != pLevel.isReceivingRedstonePower(pPos)) {
                pLevel.setBlockState(pPos, pState.with(REDSTONE_POWERED, !currentlyPowered), 2);
            }
        }
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return (_world, _pos, _state, _be) -> ((WispBatteryEntityFake)_be).tick(_world, _pos, _state);
    }

    //it doesn't actually, I just want redstone to point at it
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new WispBatteryEntityFake(pos, state);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext pContext) {
        return this.getDefaultState().with(REDSTONE_POWERED, pContext.getWorld().isReceivingRedstonePower(pContext.getBlockPos()));
    }
}
