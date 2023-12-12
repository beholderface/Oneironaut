package net.oneironaut.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.oneironaut.Oneironaut;

public class BlockVein extends Feature<BlockVeinConfig> {

    public BlockVein(Codec<BlockVeinConfig> configCodec){
        super (configCodec);
    }
    @Override
    public boolean generate(FeatureContext<BlockVeinConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos origin = context.getOrigin();
        Random rand = context.getRandom();
        BlockVeinConfig config = context.getConfig();

        Identifier veinID = config.mainBlockID();
        BlockState veinState = Registry.BLOCK.get(veinID).getDefaultState();
        Identifier carvedID = config.carvedBlockID();
        Block carvedBlock = Registry.BLOCK.get(carvedID);
        int roll = rand.nextBetweenExclusive(0, 11);

        if (/*roll == 8*/ true){
            //Vec3i randOffset = new Vec3i((rand.nextInt(10) - 5), 0, (rand.nextInt(10) - 5));

            Vec3d direction = new Vec3d(rand.nextDouble() -0.5, rand.nextDouble() -0.5, rand.nextDouble() -0.5).normalize();
            BlockPos scanPos = new BlockPos(origin.getX(), world.getBottomY(), origin.getZ());
            //determine depth of vein start
            while (world.getBlockState(scanPos).getBlock() != carvedBlock){
                scanPos = scanPos.up();
                if (scanPos.getY() > world.getTopY()){
                    //Oneironaut.LOGGER.info("Failed to find a carvable block to spawn vein in.");
                    return false;
                }
            }
            int lowestY = scanPos.getY();
            while (world.getBlockState(scanPos).getBlock() == carvedBlock){
                scanPos = scanPos.up();
                if (scanPos.getY() > world.getTopY()){
                    //Oneironaut.LOGGER.info("Failed to find the top of the carvable blocks.");
                    break;
                }
            }
            scanPos = scanPos.down();
            int highestY = scanPos.getY();
            int columnHeight = highestY - lowestY;
            int height = (int)(rand.nextGaussian() * columnHeight) + (lowestY);
            int length = (int)(rand.nextGaussian() * 15);
            Vec3d carvePoint = new Vec3d(origin.getX(), height, origin.getZ());
            for (int i = 0; i < length; i++){
                if (world.isValidForSetBlock(new BlockPos(carvePoint)) && world.getBlockState(new BlockPos(carvePoint)).getBlock() == carvedBlock && carvePoint.y > world.getBottomY()){
                    world.setBlockState(new BlockPos(carvePoint), veinState, 0b10);
                    //Oneironaut.LOGGER.info("Allegedly placed a vein block at " + carvePoint + direction);
                }
                carvePoint = carvePoint.add(direction);
            }
            //Oneironaut.LOGGER.info("Allegedly generated a vein at " + origin.getX() +", "+ origin.getZ());
            return true;
        }
        //Oneironaut.LOGGER.info("Failed to generate a vein. " + roll);
        return false;
    }
}
