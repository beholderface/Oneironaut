package net.beholderface.oneironaut.block;

import net.beholderface.oneironaut.Oneironaut;
import net.minecraft.block.*;
import net.minecraft.entity.ai.pathing.NavigationType;
//import net.minecraft.fluid.Fluid;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.beholderface.oneironaut.Oneironaut;
//import net.oneironaut.block.ThoughtSlurry;
//import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
//import software.bernie.shadowed.eliotlash.mclib.math.functions.classic.Abs;

public class ThoughtSlurryBlock extends FluidBlock {
    public static final Identifier ID =
            Identifier.of(Oneironaut.MOD_ID, "thought_slurry");
    public static final AbstractBlock.Settings SETTINGS =
            AbstractBlock.Settings.copy(Blocks.WATER).nonOpaque();
    public static final ThoughtSlurryBlock INSTANCE =
            new ThoughtSlurryBlock(ThoughtSlurry.STILL_FLUID, SETTINGS);

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