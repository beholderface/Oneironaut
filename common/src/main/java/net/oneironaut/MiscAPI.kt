package net.oneironaut

import at.petrak.hexcasting.api.HexAPI
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.spell.mishaps.MishapNotEnoughArgs
import net.minecraft.block.Blocks
import at.petrak.hexcasting.common.lib.HexBlocks
import ram.talia.hexal.common.lib.HexalBlocks
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.oneironaut.registry.DimIota
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.StructureWorldAccess
import net.oneironaut.registry.OneironautThingRegistry
import java.util.UUID

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
        HexalBlocks.SLIPWAY -> Pair(OneironautThingRegistry.NOOSPHERE_GATE.get().defaultState, 200)
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

fun isUnsafe(world: ServerWorld, pos: BlockPos, up: Boolean) : Boolean{
    val state = world.getBlockState(pos)
    var output = when (state.block){
        Blocks.LAVA -> true
        Blocks.FIRE -> true
        Blocks.SOUL_FIRE -> true
        Blocks.CAMPFIRE -> true
        Blocks.SOUL_CAMPFIRE -> true
        Blocks.MAGMA_BLOCK -> true
        Blocks.CACTUS -> true
        Blocks.SCULK_SHRIEKER -> true
        else -> false
    }
    if (state.isOpaque && up){
        output = true
    }
    return output
}
fun isSolid(world: ServerWorld, pos: BlockPos) : Boolean{
    var output = false
    val state = world.getBlockState(pos)
    if (state.fluidState.isEmpty && !state.isAir && !state.block.canMobSpawnInside()){
        output = true
    } else if (state.block.defaultState.properties.contains(Properties.WATERLOGGED) && !state.isAir){
        if (state.block.defaultState.get(Properties.WATERLOGGED) == true){
            output = true
        }
    }

    /*if (state.isTranslucent(world.getChunkAsView(floor(pos.x / 16.0).toInt(), floor(pos.z / 16.0).toInt()), pos)){
        output = true
    }*/
    return output
}

fun stringToWorld(key : String, player : ServerPlayerEntity) : ServerWorld{
    var output = player.getWorld()
    player.server?.worlds?.forEach {
        if (it.registryKey.value.toString() == key){
            output = it
        }
    }
    return output
}

fun playerUUIDtoServerPlayer(uuid: UUID, server: MinecraftServer): ServerPlayerEntity? {
    //val server = player.server
    return server.playerManager?.getPlayer(uuid)
}

fun genCircle(world : StructureWorldAccess, center : BlockPos, diameter : Int, state : BlockState, replacable : Array<Block>) : Int{
    val realCenter = Vec3d(center.x + 0.5, center.y + 0.5, center.z + 0.5)
    //val area = diameter * diameter
    val radius = diameter.toDouble() / 2
    var offset = Vec3d.ZERO
    val corner = realCenter.add(-(radius + 0.5), 0.0, -(radius + 0.5))
    var current = corner
    var placed = 0;
    for (x in 0 .. diameter){
        for (y in 0 .. diameter){
            offset = Vec3d(x.toDouble(), 0.0, y.toDouble())
            current = corner.add(offset)
            if (current.distanceTo(realCenter) <= radius && replacable.contains(world.getBlockState(BlockPos(current)).block)){
                world.setBlockState(BlockPos(current), state, 0b10)
                placed++
            }
        }
    }
    return placed
    /*for (i in 0 .. (area * 3)){

    }*/
}

fun isPlayerEnlightened(player : ServerPlayerEntity) : Boolean {
    val adv = player.server?.advancementLoader?.get(HexAPI.modLoc("enlightenment"))
    val advs = player.advancementTracker
    val enlightened : Boolean
    if (advs.getProgress(adv) != null){
        enlightened = advs.getProgress(adv).isDone
    } else {
        enlightened = false
    }
    return enlightened;
}

fun isUsingRod(ctx : CastingContext) : Boolean {
    if (ctx.source.equals(CastingContext.CastSource.PACKAGED_HEX)
        && ctx.caster.activeItem.item.equals(OneironautThingRegistry.REVERBERATION_ROD.get().asItem())
        //&& ctx.caster.getStackInHand(ctx.castingHand).equals(OneironautThingRegistry.REVERBERATION_ROD.get().asItem())
        ){
        return true
    } else {
        return false
    }
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
