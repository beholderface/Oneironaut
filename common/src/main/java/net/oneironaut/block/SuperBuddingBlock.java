package net.oneironaut.block;

import at.petrak.hexcasting.common.particles.ConjureParticleOptions;
import net.minecraft.block.*;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

import static java.lang.Math.pow;

public class SuperBuddingBlock extends BuddingAmethystBlock {
    public SuperBuddingBlock(Settings settings){
        super(settings
                .ticksRandomly()
                .sounds(BlockSoundGroup.AMETHYST_BLOCK)
                .hardness(3.5f));
    }

    public static final int GROW_CHANCE = 3;
    private static final Direction[] DIRECTIONS = Direction.values();

    //shamelessly stolen from vanilla code
    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (random.nextInt(GROW_CHANCE) == 0) {
            Direction direction = DIRECTIONS[random.nextInt(DIRECTIONS.length)];
            BlockPos blockPos = pos.offset(direction);
            BlockState blockState = world.getBlockState(blockPos);
            Block block = null;
            if (canGrowIn(blockState)) {
                block = Blocks.MEDIUM_AMETHYST_BUD;
            } else if (blockState.isOf(Blocks.MEDIUM_AMETHYST_BUD) && blockState.get(AmethystClusterBlock.FACING) == direction) {
                block = Blocks.LARGE_AMETHYST_BUD;
            } else if (blockState.isOf(Blocks.LARGE_AMETHYST_BUD) && blockState.get(AmethystClusterBlock.FACING) == direction) {
                block = Blocks.AMETHYST_CLUSTER;
            }

            if (block != null) {
                BlockState blockState2 = (BlockState)((BlockState)block.getDefaultState().with(AmethystClusterBlock.FACING, direction)).with(AmethystClusterBlock.WATERLOGGED, blockState.getFluidState().getFluid() == Fluids.WATER);
                world.setBlockState(blockPos, blockState2);
            }
        }
        //stupid ServerWorld thing
        /*
        Vec3d doublePos = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        double gaussX = random.nextGaussian();
        double gaussY = random.nextGaussian();
        double gaussZ = random.nextGaussian();
        while (gaussX == 0){
            gaussX = random.nextGaussian();
        }
        double gaussNormalize = 1 / (pow((pow(gaussX, 2) + pow(gaussY, 2) + pow(gaussZ, 2)), 0.5));
        gaussX = gaussX * gaussNormalize;
        gaussY = gaussY * gaussNormalize;
        gaussZ = gaussZ * gaussNormalize;
        double particlePosX = doublePos.x + gaussX * random.nextDouble();
        double particlePosY = doublePos.y + gaussY * random.nextDouble();
        double particlePosZ = doublePos.z + gaussZ * random.nextDouble();
        world.addParticle(new ConjureParticleOptions(0x6a31d2, true),
                particlePosX, particlePosY, particlePosZ,
                0, 0, 0);*/
    }

    public static boolean canGrowIn(BlockState state) {
        return state.isAir() || state.isOf(Blocks.WATER) && state.getFluidState().getLevel() == 8;
    }
}
