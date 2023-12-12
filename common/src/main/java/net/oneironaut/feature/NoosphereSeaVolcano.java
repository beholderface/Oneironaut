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
import net.oneironaut.Oneironaut;
import net.oneironaut.block.ThoughtSlurry;
import net.oneironaut.registry.OneironautThingRegistry;

import java.util.Arrays;
import java.util.Iterator;

import static net.oneironaut.MiscAPIKt.genCircle;

public class NoosphereSeaVolcano extends Feature<NoosphereSeaVolcanoConfig> {
    public NoosphereSeaVolcano(Codec<NoosphereSeaVolcanoConfig> configCodec){
        super (configCodec);
    }

    @Override
    public boolean generate(FeatureContext<NoosphereSeaVolcanoConfig> context) {
        StructureWorldAccess world = context.getWorld();
        //ServerWorld sworld = context.getWorld().toServerWorld();
        BlockPos origin = context.getOrigin();
        Random rand = context.getRandom();
        NoosphereSeaVolcanoConfig config = context.getConfig();

        Identifier mainID = config.mainBlockID();
        Identifier coreID = config.secondaryBlockID();

        BlockState mainstate = Registry.BLOCK.get(mainID).getDefaultState();
        if (mainstate == null){
            throw new IllegalStateException(mainID + " could not be parsed to a valid block identifier!");
        }
        BlockState corestate = Registry.BLOCK.get(coreID).getDefaultState();
        if (corestate == null){
            throw new IllegalStateException(mainID + " could not be parsed to a valid block identifier!");
        }

        //Vec3i randOffset = new Vec3i((rand.nextInt(10) - 5), 0, (rand.nextInt(10) - 5));
        BlockPos chunkCenter = new BlockPos((Math.floor(origin.getX() / 16) * 16) + 8, origin.getY(), (Math.floor(origin.getZ() / 16) * 16) + 8);
        BlockPos scanPos = chunkCenter;
        int roll = rand.nextInt(500);
        if (roll == 250){
            for (int y = origin.getY(); y < 32; y++){
                scanPos = scanPos.up();
                if ((world.getFluidState(scanPos).getFluid().equals(ThoughtSlurry.STILL_FLUID) && world.getBlockState(scanPos.up()).isAir())){
                    //make a basalt volcano with pseudoamethyst in the middle
                    //BlockPos currentPos = scanPos;
                    //Vec3i offset;
                    //int area = (int) Math.pow(num, 2);
                    Block[] replaceable = new Block[]{
                            OneironautThingRegistry.THOUGHT_SLURRY_BLOCK.get(),
                            OneironautThingRegistry.PSUEDOAMETHYST_BLOCK.get(),
                            OneironautThingRegistry.NOOSPHERE_BASALT.get(),
                            Blocks.AIR
                    };
                    int y2 = -63;
                    double r = 23;
                    for (; y2 < 14; y2++, r-=0.25){
                        if (r /*not diameter*/ > 3.75){
                            genCircle(world, scanPos.add(new Vec3i(0, y2, 0)), ((int)r*2 + 1), mainstate, replaceable);
                            if (r >= 4.5){
                                genCircle(world, scanPos.add(new Vec3i(0, y2, 0)), ((int)r*2 + 1) - 4, corestate, replaceable);
                            } else if (r == 4.25) {
                                genCircle(world, scanPos.add(new Vec3i(0, y2, 0)), 5, Blocks.AIR.getDefaultState(), replaceable);
                            } else {
                                genCircle(world, scanPos.add(new Vec3i(0, y2, 0)), 7, Blocks.AIR.getDefaultState(), replaceable);
                            }
                        }
                    }
                    world.setBlockState(new BlockPos(scanPos.getX(), 9, scanPos.getZ()), OneironautThingRegistry.NOOSPHERE_GATE.getDefaultState(), 0b10);
                    Iterator<Vec3i> jaggedOffsets = Arrays.stream(new Vec3i[]{
                            new Vec3i(-4, 0, 1), new Vec3i(-4, 0, -2), new Vec3i(-4, 1, -2),
                            new Vec3i(-4, 0, -2), new Vec3i(-3, 0, -3), new Vec3i(-2, 0, -3),
                            new Vec3i(-2.00, 1.00, -3.00), new Vec3i(-2.00, 0.00, -4.00), new Vec3i(1.00, 0.00, -4.00),
                            new Vec3i(3.00, 0.00, -3.00), new Vec3i(4.00, 0.00, -1.00), new Vec3i(4.00, 0.00, 1.00),
                            new Vec3i(2.00, 0.00, 3.00), new Vec3i(0.00, 0.00, 4.00), new Vec3i(-2.00, 0.00, 4.00),
                    }).iterator();
                    while (jaggedOffsets.hasNext()){
                        world.setBlockState(new BlockPos(scanPos.getX(), y2-1, scanPos.getZ()).add(jaggedOffsets.next()), mainstate, 0b10);
                    }

                    /*genCircle(world, scanPos, num, state, replaceable);
                    if (num >= 19){
                        scanPos = scanPos.down();
                        genCircle(world, scanPos, 11, state, replaceable);
                    }
                    if (num >= 11){
                        scanPos = scanPos.down();
                        genCircle(world, scanPos, 7, state, replaceable);
                    }*/
/*for (int i = 0; i < area; i++){
                    offset = new Vec3i(i % num, 0, i / num );
                    currentPos = currentPos.add(offset);
                    if (currentPos.isWithinDistance(scanPos.add((num / 2.0),(num / 2.0),(num / 2.0)), (double) num / 2)){
                        world.setBlockState(currentPos, state, 0x10);
                    }
                }*/
                    //Oneironaut.LOGGER.info("Successfully placed a volcano at " + scanPos);
                    return true;
                }
            }
        }
        //Oneironaut.LOGGER.info("Unsuccessfully placed a volcano at " + scanPos + ". Roll: " + roll);
        return false;
    }
    //public static ConfiguredFeature<NoosphereSeaIslandConfig, NoosphereSeaIsland> NOOSPHERE_SEA_ISLAND_SMALL = new ConfiguredFeature<>(
    //        (NoosphereSeaIsland) OneironautThingRegistry.NOOSPHERE_SEA_ISLAND,
    //        new NoosphereSeaIslandConfig(11, new Identifier(Oneironaut.MOD_ID, "noosphere_basalt"))
    //        );

}

