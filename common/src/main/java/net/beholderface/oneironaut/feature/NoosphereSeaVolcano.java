package net.beholderface.oneironaut.feature;

import com.mojang.serialization.Codec;
import net.beholderface.oneironaut.registry.OneironautBlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.beholderface.oneironaut.block.ThoughtSlurry;
import net.beholderface.oneironaut.registry.OneironautBlockRegistry;
import net.beholderface.oneironaut.registry.OneironautItemRegistry;

import java.util.Arrays;
import java.util.Iterator;

import static net.beholderface.oneironaut.MiscAPIKt.genCircle;

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

        BlockState mainstate = Registries.BLOCK.get(mainID).getDefaultState();
        if (mainstate == null){
            throw new IllegalStateException(mainID + " could not be parsed to a valid block identifier!");
        }
        BlockState corestate = Registries.BLOCK.get(coreID).getDefaultState();
        if (corestate == null){
            throw new IllegalStateException(mainID + " could not be parsed to a valid block identifier!");
        }

        //Vec3i randOffset = new Vec3i((rand.nextInt(10) - 5), 0, (rand.nextInt(10) - 5));
        BlockPos scanPos = new BlockPos((int) ((Math.floor(origin.getX() / 16.0) * 16) + 8), origin.getY(), (int) ((Math.floor(origin.getZ() / 16.0) * 16) + 8));
        //int roll = rand.nextInt(1000);
        if (true){
            for (int y = origin.getY(); y < 32; y++){
                scanPos = scanPos.up();
                if ((world.getFluidState(scanPos).getFluid().equals(ThoughtSlurry.STILL_FLUID) && world.getBlockState(scanPos.up()).isAir())){
                    //make a basalt volcano with pseudoamethyst in the middle
                    //BlockPos currentPos = scanPos;
                    //Vec3i offset;
                    //int area = (int) Math.pow(num, 2);
                    Block[] replaceable = new Block[]{
                            OneironautBlockRegistry.THOUGHT_SLURRY_BLOCK.get(),
                            OneironautBlockRegistry.PSUEDOAMETHYST_BLOCK.get(),
                            OneironautBlockRegistry.NOOSPHERE_BASALT.get(),
                            Blocks.AIR
                    };
                    int y2 = -63;
                    double r = 23;
                    int placedMainBlock = 0;
                    int placedCoreBlock = 0;
                    for (; y2 < 14; y2++, r-=0.25){
                        if (r /*not diameter*/ > 3.75){
                            placedMainBlock += genCircle(world, scanPos.add(new Vec3i(0, y2, 0)), ((int)r*2 + 1), mainstate, replaceable, 1.0);
                            if (r >= 4.5){
                                //neat fact: with a fortune III pick, this whole structure is likely to yield almost 110,000 charged amethyst worth of pseudoamethyst shards
                                placedCoreBlock += genCircle(world, scanPos.add(new Vec3i(0, y2, 0)), ((int)r*2 + 1) - 4, corestate, replaceable, 1.0);
                            } else if (r == 4.25) {
                                genCircle(world, scanPos.add(new Vec3i(0, y2, 0)), 5, Blocks.AIR.getDefaultState(), replaceable, 1.0);
                                genCircle(world, scanPos.add(new Vec3i(0, y2, 0)), 5, OneironautBlockRegistry.PSEUDOAMETHYST_CLUSTER.get().getDefaultState(), replaceable, 1.0/8.0);
                            } else {
                                genCircle(world, scanPos.add(new Vec3i(0, y2, 0)), 7, Blocks.AIR.getDefaultState(), replaceable, 1.0);
                            }
                        }
                    }
                    world.setBlockState(new BlockPos(scanPos.getX(), 9, scanPos.getZ()), OneironautBlockRegistry.NOOSPHERE_GATE.get().getDefaultState(), 0b10);
                    placedCoreBlock--;
                    Iterator<Vec3i> jaggedOffsets = Arrays.stream(new Vec3i[]{
                            new Vec3i(-4, 0, 1), new Vec3i(-4, 0, -2), new Vec3i(-4, 1, -2),
                            new Vec3i(-4, 0, -2), new Vec3i(-3, 0, -3), new Vec3i(-2, 0, -3),
                            new Vec3i(-2, 1, -3), new Vec3i(-2, 0, -4), new Vec3i(1, 0, -4),
                            new Vec3i(3, 0, -3), new Vec3i(4, 0, -1), new Vec3i(4, 0, 1),
                            new Vec3i(2, 0, 3), new Vec3i(0, 0, 4), new Vec3i(-2, 0, 4),
                    }).iterator();
                    placedMainBlock += 15;
                    while (jaggedOffsets.hasNext()){
                        world.setBlockState(new BlockPos(scanPos.getX(), y2-1, scanPos.getZ()).add(jaggedOffsets.next()), mainstate, 0b10);
                    }
                    return true;
                }
            }
        }
        return false;
    }

}

