package net.beholderface.oneironaut.feature;

import at.petrak.hexcasting.common.misc.AkashicTreeGrower;
import com.mojang.serialization.Codec;
import net.beholderface.oneironaut.registry.OneironautBlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.beholderface.oneironaut.block.ThoughtSlurry;
import net.beholderface.oneironaut.registry.OneironautBlockRegistry;
//import java.lang.reflect.Array;
//import java.util.ArrayList;

import static net.beholderface.oneironaut.MiscAPIKt.genCircle;

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

        BlockState state = Registries.BLOCK.get(blockID).getDefaultState();
        if (state == null){
            throw new IllegalStateException(blockID + " could not be parsed to a valid block identifier!");
        }
        Vec3i randOffset = new Vec3i((rand.nextInt(10) - 5), 0, (rand.nextInt(10) - 5));
        BlockPos scanPos = origin.add(randOffset);
        if (rand.nextInt((int) Math.pow(num, 1.75)) == num){
            for (int y = origin.getY(); y < 32; y++){
                scanPos = scanPos.up();
                if ((world.getFluidState(scanPos).getFluid().equals(ThoughtSlurry.STILL_FLUID) && world.getBlockState(scanPos.up()).isAir())){
                    BlockPos surfaceCenter = scanPos.up();
                    //make a small basalt island
                    Block[] replaceable = new Block[]{
                            OneironautBlockRegistry.THOUGHT_SLURRY_BLOCK.get(),
                            Blocks.AIR
                    };
                    genCircle(world, scanPos, num, state, replaceable, 1.0);
                    boolean generatedTrees = false;
                    if (num >= 19){
                        scanPos = scanPos.down();
                        genCircle(world, scanPos, 11, state, replaceable, 1.0);
                        if (rand.nextBetween(1, 5) == 5){
                            generatedTrees = true;
                            int treeCount = rand.nextBetween(2, 6);
                            for (int i = 0; i < treeCount; i++){
                                BlockPos treeSpot = surfaceCenter.add(rand.nextBetween(-7, 7), 0, rand.nextBetween(-7, 7));
                                world.setBlockState(treeSpot, OneironautBlockRegistry.EDIFIED_TREE_SPAWNER.get().getDefaultState(), 3);
                            }
                        }
                    }
                    if (num >= 11){
                        scanPos = scanPos.down();
                        genCircle(world, scanPos, 7, state, replaceable, 1.0);
                        if (rand.nextBetween(1, 5) == 5 && num == 11){
                            //generatedTrees = true;
                            int treeCount = rand.nextBetween(1, 3);
                            for (int i = 0; i < treeCount; i++){
                                BlockPos treeSpot = surfaceCenter.add(rand.nextBetween(-4, 4), 0, rand.nextBetween(-4, 4));
                                world.setBlockState(treeSpot, OneironautBlockRegistry.EDIFIED_TREE_SPAWNER.get().getDefaultState(), 3);
                            }
                        }
                        /*if (rand.nextBetween(1, generatedTrees ? 3 : 25 - num) == 1){
                            BoatEntity boat = new BoatEntity(EntityType.BOAT, world.toServerWorld());
                            boat.setBoatType(BoatEntity.Type.getType(rand.nextBetween(0, 6)));
                            Vec3d surfaceCenterDouble = new Vec3d(surfaceCenter.getX(), surfaceCenter.getY(), surfaceCenter.getZ());
                            Vec3d boatPos = surfaceCenterDouble.add(new Vec3d(1.0, 0.0, 0.0)
                                    .rotateY((float) Math.toRadians(rand.nextBetween(0, 360))).multiply(rand.nextBetween(num - 3, (int) (num * 1.5))));
                            boat.setPos(boatPos.x, boatPos.y, boatPos.z);
                            boat.setYaw(rand.nextBetween(-180, 180));
                            world.spawnEntity(boat);
                        }*/
                    }
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

