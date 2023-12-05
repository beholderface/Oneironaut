package net.oneironaut

import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.spell.mishaps.MishapNotEnoughArgs
import net.minecraft.block.Blocks
import at.petrak.hexcasting.common.lib.HexBlocks
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.oneironaut.registry.DimIota
import net.minecraft.state.property.Properties
import net.minecraft.state.property.Property
import net.minecraft.util.math.BlockPos
import java.lang.reflect.TypeVariable

fun List<Iota>.getDimIota(idx: Int, argc: Int = 0): DimIota {
    val x = this.getOrElse(idx) { throw MishapNotEnoughArgs(idx + 1, this.size) }
    if (x is DimIota) {
        return x
    }

    throw MishapInvalidIota.ofType(x, if (argc == 0) idx else argc - (idx + 1), "imprint")
}

fun getInfuseResult(targetType: Block) : Pair<BlockState, Int> {
    //val block = BlockPos(target)
    //val targetType = ctx.world.getBlockState(block).block
    //BlockTags.SMALL_FLOWERS
    val conversionResult : Pair<BlockState, Int> = when(targetType){
        Blocks.SCULK_SHRIEKER -> Pair(Blocks.SCULK_SHRIEKER.defaultState.with(Properties.CAN_SUMMON, true), 100)
        Blocks.RESPAWN_ANCHOR -> Pair(Blocks.RESPAWN_ANCHOR.defaultState.with(Properties.CHARGES, 4), 100)
        //"fuck you" *uncries your obsidian*
        Blocks.CRYING_OBSIDIAN -> Pair(Blocks.OBSIDIAN.defaultState, 10)
        Blocks.CHORUS_FLOWER -> Pair(Blocks.CHORUS_FLOWER.defaultState, 5)
        Blocks.WITHER_SKELETON_SKULL -> Pair(Blocks.SKELETON_SKULL.defaultState, 5)
        Blocks.WITHER_SKELETON_WALL_SKULL -> Pair(Blocks.SKELETON_WALL_SKULL.defaultState, 5)
        Blocks.WITHER_ROSE -> {
            val smallflowers = arrayOf(Blocks.DANDELION, Blocks.POPPY, Blocks.BLUE_ORCHID, Blocks.ALLIUM, Blocks.AZURE_BLUET, Blocks.RED_TULIP, Blocks.ORANGE_TULIP,
                Blocks.WHITE_TULIP, Blocks.PINK_TULIP, Blocks.CORNFLOWER, Blocks.LILY_OF_THE_VALLEY)
            val flowerIndex = kotlin.random.Random.nextInt(0, smallflowers.size)
            Pair(smallflowers[flowerIndex].defaultState, 5)
        }
        HexBlocks.AVENTURINE_EDIFIED_LEAVES -> Pair(HexBlocks.AMETHYST_EDIFIED_LEAVES.defaultState.with(Properties.PERSISTENT, true), 1)
        HexBlocks.AMETHYST_EDIFIED_LEAVES -> Pair(HexBlocks.CITRINE_EDIFIED_LEAVES.defaultState.with(Properties.PERSISTENT, true), 1)
        HexBlocks.CITRINE_EDIFIED_LEAVES -> Pair(HexBlocks.AVENTURINE_EDIFIED_LEAVES.defaultState.with(Properties.PERSISTENT, true), 1)
        Blocks.SOUL_CAMPFIRE -> Pair(Blocks.CAMPFIRE.defaultState, 5)
        Blocks.SOUL_SAND -> Pair(Blocks.SAND.defaultState, 5)
        Blocks.SOUL_TORCH -> Pair(Blocks.TORCH.defaultState, 5)
        Blocks.SOUL_WALL_TORCH -> Pair(Blocks.WALL_TORCH.defaultState, 5)
        Blocks.SOUL_SOIL -> Pair(Blocks.DIRT.defaultState, 5)
        Blocks.SOUL_LANTERN -> Pair(Blocks.LANTERN.defaultState, 5)
        else -> Pair(Blocks.BARRIER.defaultState, -1)
    }
    return conversionResult
}
//tried to write a smoother way to keep important blockstate values, didn't work
/*
fun keepImportantStates(ctx: CastingContext, target: BlockPos, desired: BlockState) : BlockState{
    val state = ctx.world.getBlockState(target)
    val statesToKeep = listOf(Properties.HORIZONTAL_FACING, Properties.WATERLOGGED, Properties.ROTATION, Properties.HANGING, Properties.LIT, Properties.SIGNAL_FIRE)
    var modifiedState = desired
    var current = statesToKeep[1]
    for (item in statesToKeep){
        current = item
        if (state.properties.contains(item) && desired.properties.contains(item)){
            modifiedState = modifiedState.with(item, state.get(current))
        }
    }
    return modifiedState
}
*/
