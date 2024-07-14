package net.beholderface.oneironaut

import at.petrak.hexcasting.api.HexAPI
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.spell.mishaps.MishapNotEnoughArgs
import net.beholderface.oneironaut.recipe.OneironautRecipeTypes
import net.beholderface.oneironaut.casting.iotatypes.DimIota
import net.beholderface.oneironaut.registry.OneironautItemRegistry
import net.beholderface.oneironaut.casting.iotatypes.SoulprintIota
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.entity.EntityType
import net.minecraft.item.Item
import net.minecraft.recipe.RecipeManager
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.property.Properties
import net.minecraft.state.property.Property
import net.minecraft.tag.TagKey
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.util.registry.Registry
import net.minecraft.world.StructureWorldAccess
import net.minecraft.world.World
import java.util.*
import kotlin.math.absoluteValue

fun List<Iota>.getDimIota(idx: Int, argc: Int = 0): DimIota {
    val x = this.getOrElse(idx) { throw MishapNotEnoughArgs(idx + 1, this.size) }
    if (x is DimIota) {
        return x
    }

    throw MishapInvalidIota.ofType(x, if (argc == 0) idx else argc - (idx + 1), "oneironaut:imprint")
}

/*fun List<Iota>.getStatusEffect(idx: Int, argc: Int = 0, allowShroud : Boolean) : StatusEffect{
    val x = this.getOrElse(idx) { throw MishapNotEnoughArgs(idx + 1, this.size) }
    if (x is PotionIota) {
        if (!allowShroud && (x as PotionIota).getEffect().equals(
                OneironautMiscRegistry.DETECTION_RESISTANCE.get())){
            throw MishapInvalidIota.ofType(x, if (argc == 0) idx else argc - (idx + 1), "oneironaut:visiblestatus")
        }
        return (x as PotionIota).effect
    }

    throw MishapInvalidIota.ofType(x, if (argc == 0) idx else argc - (idx + 1), "oneironaut:status")
}*/

fun List<Iota>.getSoulprint(idx: Int, argc: Int = 0) : UUID {
    val x = this.getOrElse(idx) { throw MishapNotEnoughArgs(idx + 1, this.size) }
    if (x is SoulprintIota) {
        return x.entity
    }

    throw MishapInvalidIota.ofType(x, if (argc == 0) idx else argc - (idx + 1), "oneironaut:soulprint")
}

fun getBlockTagKey(id : Identifier?) : TagKey<Block>?{
    if (id != null){
        return TagKey.of(Registry.BLOCK_KEY, id)
    }
    return null
}
fun getEntityTagKey(id : Identifier) : TagKey<EntityType<*>>{
    return TagKey.of(Registry.ENTITY_TYPE_KEY, id)
}
fun getItemTagKey(id : Identifier) : TagKey<Item>{
    return TagKey.of(Registry.ITEM_KEY, id)
}


fun getInfuseResult(targetState: BlockState, world: World) : Triple<BlockState, Int, String?> {
    //at the moment this when thing is just for the wither rose transmutation, since everything without special behavior is now handled in recipe jsons
    var conversionResult : Triple<BlockState, Int, String?> = when(targetState.block){
        Blocks.WITHER_ROSE -> {
            val smallflowers = arrayOf(Blocks.DANDELION, Blocks.POPPY, Blocks.BLUE_ORCHID, Blocks.ALLIUM, Blocks.AZURE_BLUET, Blocks.RED_TULIP, Blocks.ORANGE_TULIP,
                Blocks.WHITE_TULIP, Blocks.PINK_TULIP, Blocks.CORNFLOWER, Blocks.LILY_OF_THE_VALLEY)
            val flowerIndex = kotlin.random.Random.nextInt(0, smallflowers.size)
            Triple(smallflowers[flowerIndex].defaultState, 5, null)
        }
        else -> Triple(Blocks.BARRIER.defaultState, -1, null)
    }
    val debugMessages = false
    if (conversionResult.second == -1){
        Oneironaut.boolLogger(
            "did not find a hard-coded conversion",
            debugMessages
        )
        val recipeManager : RecipeManager = world.recipeManager
        val infusionRecipes = recipeManager.listAllOfType(OneironautRecipeTypes.INFUSION_TYPE)
        val recipe = infusionRecipes.find { it.matches(targetState) }
        if (recipe != null){
            Oneironaut.boolLogger(
                "found a matching recipe",
                debugMessages
            )
            /*val advancement = recipe.advancement
            val passedAdvancement : String? = if (advancement.equals("")){
                null
            } else {
                advancement
            }*/
            conversionResult = Triple(recipe.blockOut, recipe.mediaCost, null)
        } else {
            Oneironaut.boolLogger(
                "no matching recipe found",
                debugMessages
            )
        }
    } else {
        Oneironaut.boolLogger(
            "found a hard-coded conversion",
            debugMessages
        )
    }
    return Triple(preserveStates(targetState, conversionResult.first), conversionResult.second, conversionResult.third)
}
fun preserveStates(oldState : BlockState, desiredState : BlockState) : BlockState{
    val debugmessages = false
    var newState = desiredState
    if (desiredState != Blocks.BARRIER.defaultState){
        val boolsToKeep : List<Property<Boolean>> = listOf(Properties.WATERLOGGED, Properties.HANGING)
        for (property in boolsToKeep){
            if (oldState.contains(property)){
                val value = oldState.get(property)
                Oneironaut.boolLogger(
                    "property ${property.name} has value $value",
                    debugmessages
                )
                newState = newState.with(property, value)
            }
        }
        val intsToKeep : List<Property<Int>> = listOf(Properties.ROTATION)
        for (property in intsToKeep){
            if (oldState.contains(property)){
                val value = oldState.get(property)
                Oneironaut.boolLogger(
                    "property ${property.name} has value $value",
                    debugmessages
                )
                newState = newState.with(property, value)
            }
        }
        val dirsToKeep : List<Property<Direction>> = listOf(Properties.FACING, Properties.HORIZONTAL_FACING)
        for (property in dirsToKeep) {
            if (oldState.contains(property)) {
                val value = oldState.get(property)
                Oneironaut.boolLogger(
                    "property ${property.name} has value $value",
                    debugmessages
                )
                newState = newState.with(property, value)
            }
        }
    }
    return newState
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

fun genCircle(world : StructureWorldAccess, center : BlockPos, diameter : Int, state : BlockState, replacable : Array<Block>, fillPortion : Double) : Int{
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
            if (world.random.nextBetween(0, 999) / 10.0 <= fillPortion * 100)
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
    return getPositionsInCuboid(
        corner1,
        corner2,
        listOf(corner2.add((corner1.x - corner2.x).absoluteValue + 20, 0, 0))
    )
}

fun getBoxCorners(box : Box) : List<Vec3d>{
    return listOf(Vec3d(box.minX, box.minY, box.minZ), Vec3d(box.maxX, box.minY, box.minZ),
        Vec3d(box.maxX, box.maxY, box.minZ), Vec3d(box.maxX, box.maxY, box.maxZ),
        Vec3d(box.minX, box.maxY, box.maxZ), Vec3d(box.minX, box.minY, box.maxZ),
        Vec3d(box.maxX, box.minY, box.maxZ), Vec3d(box.minX, box.maxY, box.minZ)
    )
}

fun vecProximity(a: Vec3d, b: Vec3d): Double {
    //I'm not sure what the best way to do this is, but this way works for hexes so it's what I tried first
    return a.normalize().subtract(b.normalize()).length()
}

fun vecProximity(a: Direction, b: Vec3d): Double {
    return vecProximity(Vec3d.of(a.vector), b)
}