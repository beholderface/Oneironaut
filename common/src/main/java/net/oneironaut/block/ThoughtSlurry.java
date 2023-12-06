package net.oneironaut.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.oneironaut.Oneironaut;
import net.minecraft.util.registry.Registry;
import net.oneironaut.registry.OneironautItemRegistry;

public class ThoughtSlurry extends FlowableFluid {
    @Override
    public boolean matchesType(Fluid fluid) {
        return fluid == getStill() || fluid == getFlowing();
    }
    public static final Identifier ID =
            Identifier.of(Oneironaut.MOD_ID, "thought_slurry");

    public static ThoughtSlurry.Still STILL_FLUID =
            new ThoughtSlurry.Still();

    public static final Identifier FLOWING_ID =
            Identifier.of(Oneironaut.MOD_ID, "flowing_thought_slurry");

    public static final ThoughtSlurry.Flowing FLOWING_FLUID =
            new ThoughtSlurry.Flowing();

    public static final TagKey<Fluid> TAG =
            TagKey.of(Registry.FLUID_KEY, ThoughtSlurry.ID);

    @Override
    public FluidState getFlowing(int level, boolean falling) {
        return (this.getFlowing().getDefaultState().with(LEVEL, level)).with(FALLING, falling);
        //return ThoughtSlurry.FLOWING_FLUID;
    }

    @Override
    public Fluid getFlowing() {
        return Flowing.FLOWING_FLUID;
    }

    @Override
    public Fluid getStill() {
        return ThoughtSlurry.STILL_FLUID;
    }

    @Override
    protected boolean isInfinite() {
        return true;
    }

    @Override
    protected void beforeBreakingBlock(WorldAccess world, BlockPos pos, BlockState state) {
        final BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
        Block.dropStacks(state, world, pos, blockEntity);
    }

    @Override
    protected int getFlowSpeed(WorldView world) {
        return 3;
    }

    @Override
    protected int getLevelDecreasePerBlock(WorldView world) {
        return 1;
    }

    @Override
    public Item getBucketItem() {
        return OneironautItemRegistry.THOUGHT_SLURRY_BUCKET.get();
    }

    @Override
    protected boolean canBeReplacedWith(FluidState state, BlockView world, BlockPos pos, Fluid fluid, Direction direction) {
        return false;
    }

    @Override
    public int getTickRate(WorldView world) {
        return 5;
    }

    @Override
    protected float getBlastResistance() {
        return 100.0f;
    }

    @Override
    protected BlockState toBlockState(FluidState state) {
        return ThoughtSlurryBlock.INSTANCE.getDefaultState().with(FluidBlock.LEVEL, getBlockStateLevel(state));
    }

    @Override
    public boolean isStill(FluidState state) {
        return false;
    }

    @Override
    public int getLevel(FluidState state) {
        //return state.getLevel();
        return 8;
    }

    public final static class Still extends ThoughtSlurry {

        @Override
        public boolean isStill(FluidState state) {
            return true;
        }

        @Override
        public int getLevel(FluidState state) {
            return 8;
        }

    }

    public final static class Flowing extends ThoughtSlurry {
        @Override
        protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
            super.appendProperties(builder);
            builder.add(FlowableFluid.LEVEL);
        }

        @Override
        public boolean isStill(FluidState state) {
            return false;
        }

        @Override
        public int getLevel(FluidState state) {
            return state.get(FlowableFluid.LEVEL);
        }

    }
}
