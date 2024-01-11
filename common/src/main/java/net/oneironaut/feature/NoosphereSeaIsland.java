package net.oneironaut.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.oneironaut.block.ThoughtSlurry;
import net.oneironaut.registry.OneironautBlockRegistry;
//import java.lang.reflect.Array;
//import java.util.ArrayList;

import static net.oneironaut.MiscAPIKt.genCircle;

public class NoosphereSeaIsland extends Feature<NoosphereSeaIslandConfig> {
    public NoosphereSeaIsland(Codec<NoosphereSeaIslandConfig> configCodec){
        super (configCodec);
    }

    @Override
    public boolean generate(FeatureContext<NoosphereSeaIslandConfig> context) {
        StructureWorldAccess world = context.getWorld();
        //ServerWorld sworld = context.getWorld().toServerWorld();
        BlockPos origin = context.getOrigin();
        Random rand = context.getRandom();
        NoosphereSeaIslandConfig config = context.getConfig();

        int num = config.size();
        Identifier blockID = config.blockID();

        BlockState state = Registry.BLOCK.get(blockID).getDefaultState();
        if (state == null){
            throw new IllegalStateException(blockID + " could not be parsed to a valid block identifier!");
        }
        Vec3i randOffset = new Vec3i((rand.nextInt(10) - 5), 0, (rand.nextInt(10) - 5));
        BlockPos scanPos = origin.add(randOffset);
        if (rand.nextInt((int) Math.pow(num, 1.75)) == num){
            for (int y = origin.getY(); y < 32; y++){
                scanPos = scanPos.up();
                if ((world.getFluidState(scanPos).getFluid().equals(ThoughtSlurry.STILL_FLUID) && world.getBlockState(scanPos.up()).isAir())){
                    //make a small basalt island
                    //BlockPos currentPos = scanPos;
                    //Vec3i offset;
                    //int area = (int) Math.pow(num, 2);
                    Block[] replaceable = new Block[]{
                            OneironautBlockRegistry.THOUGHT_SLURRY_BLOCK.get(),
                            Blocks.AIR
                    };
                    genCircle(world, scanPos, num, state, replaceable);
                    if (num >= 19){
                        scanPos = scanPos.down();
                        genCircle(world, scanPos, 11, state, replaceable);
                    }
                    if (num >= 11){
                        scanPos = scanPos.down();
                        genCircle(world, scanPos, 7, state, replaceable);
                    }
/*for (int i = 0; i < area; i++){
                    offset = new Vec3i(i % num, 0, i / num );
                    currentPos = currentPos.add(offset);
                    if (currentPos.isWithinDistance(scanPos.add((num / 2.0),(num / 2.0),(num / 2.0)), (double) num / 2)){
                        world.setBlockState(currentPos, state, 0x10);
                    }
                }*/
                    //Oneironaut.LOGGER.info("Successfully placed an island at " + scanPos);
                    return true;
                }
            }
        }
        //Oneironaut.LOGGER.info("Unsuccessfully placed an island at " + scanPos);
        return false;
    }
    //public static ConfiguredFeature<NoosphereSeaIslandConfig, NoosphereSeaIsland> NOOSPHERE_SEA_ISLAND_SMALL = new ConfiguredFeature<>(
    //        (NoosphereSeaIsland) OneironautThingRegistry.NOOSPHERE_SEA_ISLAND,
    //        new NoosphereSeaIslandConfig(11, new Identifier(Oneironaut.MOD_ID, "noosphere_basalt"))
    //        );

}

