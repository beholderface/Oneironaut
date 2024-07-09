package net.beholderface.oneironaut.block;

import net.beholderface.oneironaut.block.blockentity.HoverElevatorBlockEntity;
import net.beholderface.oneironaut.registry.OneironautBlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class HoverElevatorBlock extends BlockWithEntity {
    public static final DirectionProperty FACING = Properties.FACING;
    public static final BooleanProperty POWERED = Properties.POWERED;
    public HoverElevatorBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(POWERED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        builder.add(POWERED);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new HoverElevatorBlockEntity(pos, state);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext pContext) {
        BlockState output = this.getDefaultState().with(POWERED, pContext.getWorld().isReceivingRedstonePower(pContext.getBlockPos()));
        if (pContext.getPlayer() != null){
            return output.with(Properties.FACING, !pContext.getPlayer().isSneaking() ? pContext.getSide() : pContext.getSide().getOpposite());
        } else {
            return output.with(Properties.FACING, pContext.getSide());
        }
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return (_world, _pos, _state, _be) -> ((HoverElevatorBlockEntity)_be).tick(_world, _pos, _state);
    }

    @Override
    public void neighborUpdate(BlockState pState, World pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos,
                               boolean pIsMoving) {
        super.neighborUpdate(pState, pLevel, pPos, pBlock, pFromPos, pIsMoving);
        if (!pLevel.isClient) {
            boolean currentlyPowered = pState.get(POWERED);
            if (currentlyPowered != pLevel.isReceivingRedstonePower(pPos)) {
                pLevel.setBlockState(pPos, pState.with(POWERED, !currentlyPowered), 2);
            }
        }
    }

    @Override
    public boolean hasComparatorOutput(BlockState pState) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        int output = 0;
        Optional<HoverElevatorBlockEntity> blockEntityMaybe = world.getBlockEntity(pos, OneironautBlockRegistry.HOVER_ELEVATOR_ENTITY.get());
        if (blockEntityMaybe.isPresent()){
            output = blockEntityMaybe.get().getLevel();
        }
        return output;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state){
        return BlockRenderType.MODEL;
    }
}
