package net.oneironaut.block;

import net.minecraft.block.*;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.oneironaut.Oneironaut;
//import net.oneironaut.block.ThoughtSlurry;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

/*
public class ThoughtSlurryBlock extends FluidBlock {
    public static final Identifier ID =
            Identifier.of(Oneironaut.MOD_ID, "thought_slurry");
    public static final AbstractBlock.Settings SETTINGS =
            FabricBlockSettings.copy(Blocks.LAVA);
    public static final Block INSTANCE =
            new ThoughtSlurryBlock(new ThoughtSlurry.Still(), SETTINGS);

    public ThoughtSlurryBlock(ThoughtSlurry thoughtSlurry, AbstractBlock.Settings settings) {
        super(thoughtSlurry, settings);
    }

    //@Override
    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return true;
    }
}
*/
