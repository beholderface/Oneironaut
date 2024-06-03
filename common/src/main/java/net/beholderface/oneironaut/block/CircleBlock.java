package net.beholderface.oneironaut.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class CircleBlock extends Block {

    public CircleBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext pContext) {
        return this.getDefaultState().with(Properties.FACING, pContext.getSide());
    }
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context){
        return switch (state.get(Properties.FACING)){
            case DOWN -> VoxelShapes.cuboid(0.0 / 16, 15.0 / 16, 0.0 / 16, 16.0 / 16, 16.0 / 16, 16.0 / 16);
            case UP -> VoxelShapes.cuboid(0.0 / 16, 0.0 / 16, 0.0 / 16, 16.0 / 16, 1.0 / 16, 16.0 / 16);
            case NORTH -> VoxelShapes.cuboid(0.0 / 16, 0.0 / 16, 15.0 / 16, 16.0 / 16, 16.0 / 16, 16.0 / 16);
            case SOUTH -> VoxelShapes.cuboid(0.0 / 16, 0.0 / 16, 0.0 / 16, 16.0 / 16, 16.0 / 16, 1.0 / 16);
            case WEST -> VoxelShapes.cuboid(15.0 / 16, 0.0 / 16, 0.0 / 16, 16.0 / 16, 16.0 / 16, 16.0 / 16);
            case EAST -> VoxelShapes.cuboid(0.0 / 16, 0.0 / 16, 0.0 / 16, 1.0 / 16, 16.0 / 16, 16.0 / 16);
        };
    }
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context){
        return switch (state.get(Properties.FACING)){
            case DOWN -> VoxelShapes.cuboid(0.0 / 16, 15.9 / 16, 0.0 / 16, 16.0 / 16, 16.0 / 16, 16.0 / 16);
            case UP -> VoxelShapes.cuboid(0.0 / 16, 0.0 / 16, 0.0 / 16, 16.0 / 16, 0.1 / 16, 16.0 / 16);
            case NORTH -> VoxelShapes.cuboid(0.0 / 16, 0.0 / 16, 15.9 / 16, 16.0 / 16, 16.0 / 16, 16.0 / 16);
            case SOUTH -> VoxelShapes.cuboid(0.0 / 16, 0.0 / 16, 0.0 / 16, 16.0 / 16, 16.0 / 16, 0.1 / 16);
            case WEST -> VoxelShapes.cuboid(15.9 / 16, 0.0 / 16, 0.0 / 16, 16.0 / 16, 16.0 / 16, 16.0 / 16);
            case EAST -> VoxelShapes.cuboid(0.0 / 16, 0.0 / 16, 0.0 / 16, 0.1 / 16, 16.0 / 16, 16.0 / 16);
        };
    }
}
