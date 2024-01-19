package net.oneironaut

import at.petrak.hexcasting.api.HexAPI
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.spell.mishaps.MishapNotEnoughArgs
import at.petrak.hexcasting.common.lib.HexBlocks
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.entity.EntityType
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.item.Item
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.property.Properties
import net.minecraft.tag.TagKey
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.util.registry.BuiltinRegistries
import net.minecraft.util.registry.Registry
import net.minecraft.world.StructureWorldAccess
import net.oneironaut.registry.*
import ram.talia.hexal.common.lib.HexalBlocks
import java.util.*
import kotlin.math.absoluteValue

fun List<Iota>.getDimIota(idx: Int, argc: Int = 0): DimIota {
    val x = this.getOrElse(idx) { throw MishapNotEnoughArgs(idx + 1, this.size) }
    if (x is DimIota) {
        return x
    }

    throw MishapInvalidIota.ofType(x, if (argc == 0) idx else argc - (idx + 1), "imprint")
}

fun List<Iota>.getStatusEffect(idx: Int, argc: Int = 0, allowShroud : Boolean) : StatusEffect{
    val x = this.getOrElse(idx) { throw MishapNotEnoughArgs(idx + 1, this.size) }
    if (x is PotionIota) {
        if (!allowShroud && (x as PotionIota).getEffect().equals(OneironautMiscRegistry.DETECTION_RESISTANCE.get())){
            throw MishapInvalidIota.ofType(x, if (argc == 0) idx else argc - (idx + 1), "detectable status effect")
        }
        return (x as PotionIota).effect
    }

    throw MishapInvalidIota.ofType(x, if (argc == 0) idx else argc - (idx + 1), "status effect")
}

fun getBlockTagKey(id : Identifier) : TagKey<Block>{
    return TagKey.of(Registry.BLOCK_KEY, id)
}
fun getEntityTagKey(id : Identifier) : TagKey<EntityType<*>>{
    return TagKey.of(Registry.ENTITY_TYPE_KEY, id)
}
fun getItemTagKey(id : Identifier) : TagKey<Item>{
    return TagKey.of(Registry.ITEM_KEY, id)
}


fun getInfuseResult(targetType: Block) : Pair<BlockState, Int> {
    //val block = BlockPos(target)
    //val targetType = ctx.world.getBlockState(block).block
    //BlockTags.SMALL_FLOWERS
    val conversionResult : Pair<BlockState, Int> = when(targetType){
        HexalBlocks.SLIPWAY -> Pair(OneironautBlockRegistry.NOOSPHERE_GATE.get().defaultState, 200)
        Blocks.SCULK_SHRIEKER -> Pair(Blocks.SCULK_SHRIEKER.defaultState.with(Properties.CAN_SUMMON, true), 100)
        Blocks.RESPAWN_ANCHOR -> Pair(Blocks.RESPAWN_ANCHOR.defaultState.with(Properties.CHARGES, 4), 100)
        //"fuck you" *uncries your obsidian*
        Blocks.CRYING_OBSIDIAN -> Pair(Blocks.OBSIDIAN.defaultState, 10)
        Blocks.CHORUS_FLOWER -> Pair(Blocks.CHORUS_FLOWER.defaultState.with(OneironautBlockRegistry.ETERNAL, true), 5)
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
    if (ctx.source == CastingContext.CastSource.PACKAGED_HEX && ctx.caster.activeItem.item == OneironautItemRegistry.REVERBERATION_ROD.get().asItem()
        //&& ctx.caster.getStackInHand(ctx.castingHand).equals(OneironautThingRegistry.REVERBERATION_ROD.get().asItem())
        ){
        return true
    } else {
        return false
    }
}

fun getPositionsInCuboid(corner1 : BlockPos, corner2 : BlockPos, pointsToExclude : List<BlockPos>) : List<BlockPos>{
    val cuboid = Box(corner1, corner2)
    val lowerCorner = BlockPos(cuboid.minX, cuboid.minY, cuboid.minZ)
    val outputList : MutableList<BlockPos> = mutableListOf()
    var currentPos : BlockPos
    for (i in 0 .. cuboid.xLength.toInt()){
        for (j in 0 .. cuboid.yLength.toInt()){
            for (k in 0 .. cuboid.zLength.toInt()){
                currentPos = lowerCorner.add(i, j, k)
                if (!pointsToExclude.contains(currentPos)){
                    outputList.add(currentPos)
                }
            }
        }
    }
    return outputList.toList()
}

fun getPositionsInCuboid(corner1 : BlockPos, corner2 : BlockPos, pointToExclude : BlockPos) : List<BlockPos>{
    return getPositionsInCuboid(corner1, corner2, listOf(pointToExclude))
}

fun getPositionsInCuboid(corner1 : BlockPos, corner2 : BlockPos) : List<BlockPos>{
    return getPositionsInCuboid(corner1, corner2, listOf(corner2.add((corner1.x - corner2.x).absoluteValue + 20, 0, 0)))
}

fun getBoxCorners(box : Box) : List<Vec3d>{
    return listOf(Vec3d(box.minX, box.minY, box.minZ), Vec3d(box.maxX, box.minY, box.minZ),
        Vec3d(box.maxX, box.maxY, box.minZ), Vec3d(box.maxX, box.maxY, box.maxZ),
        Vec3d(box.minX, box.maxY, box.maxZ), Vec3d(box.minX, box.minY, box.maxZ),
        Vec3d(box.maxX, box.minY, box.maxZ), Vec3d(box.minX, box.maxY, box.minZ)
    )
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
