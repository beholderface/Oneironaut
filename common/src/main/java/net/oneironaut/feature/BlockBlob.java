package net.oneironaut.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.oneironaut.Oneironaut;
import net.oneironaut.block.ThoughtSlurry;
import net.oneironaut.registry.OneironautBlockRegistry;

import static net.oneironaut.MiscAPIKt.genCircle;

public class BlockBlob extends Feature<BlockBlobConfig> {
    public BlockBlob(Codec<BlockBlobConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<BlockBlobConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos origin = context.getOrigin();
        Random rand = context.getRandom();
        BlockBlobConfig config = context.getConfig();
        Identifier mainID = config.mainBlockID();
        BlockState mainState = Registry.BLOCK.get(mainID).getDefaultState();
        int rarity = config.rarity();
        int size = config.size();
        double squish = 1.0 / config.squish();
        int falloff = config.falloff();
        int roll = rand.nextBetweenExclusive(0, rarity + 1);
        int immersion = config.immersion();
        if (roll == rarity){
            Vec3i randOffset = new Vec3i((rand.nextInt(10) - 5), 0, (rand.nextInt(10) - 5));
            BlockPos scanPos = origin.add(randOffset);
            for (int y = origin.getY(); y < 32; y++){
                scanPos = scanPos.up();
                if ((world.getFluidState(scanPos).getFluid().equals(ThoughtSlurry.STILL_FLUID) && world.getBlockState(scanPos.up()).isAir())){
                    //immerse it a bit
                    origin = scanPos.add(0, -rand.nextBetweenExclusive(0, immersion), 0);
                    //Oneironaut.LOGGER.info("Attempting to place a blob at " + origin.toShortString());
                    Vec3i cuboidDimensions = new Vec3i((size * 2) + 1, ((size * 2) + 1) * squish, (size * 2) + 1);
                    BlockPos cuboidOrigin = origin.add(-size, -(size * squish), -size);
                    for (int i = 0; i < cuboidDimensions.getX(); i++){
                        for (int j = 0; j < cuboidDimensions.getY(); j++){
                            for (int k = 0; k < cuboidDimensions.getZ(); k++){
                                Vec3i offset = new Vec3i(i, j, k);
                                Vec3i deSquishedOffset = new Vec3i(i, j / squish, k);
                                BlockPos target = cuboidOrigin.add(offset);
                                BlockPos deSquishedTarget = cuboidOrigin.add(deSquishedOffset);
                                if (deSquishedTarget.getManhattanDistance(origin) < rand.nextBetweenExclusive(0, (cuboidDimensions.getX() + cuboidDimensions.getZ() + cuboidDimensions.getZ()) / falloff)){
                                    world.setBlockState(target, mainState, 0b10);
                                }
                            }
                        }
                    }
                    return true;
                }
            }
            return true;
        }
        return false;
    }
}
