package net.beholderface.oneironaut.recipe

import at.petrak.hexcasting.common.recipe.RecipeSerializerBase
import at.petrak.hexcasting.common.recipe.ingredient.StateIngredient
import at.petrak.hexcasting.common.recipe.ingredient.StateIngredientHelper
import com.google.gson.JsonObject
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.recipe.Recipe
import net.minecraft.util.Identifier
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.RecipeType
import net.minecraft.world.World
import net.minecraft.util.JsonHelper

//pretty much all of this is yoinked from hexal
data class InfusionRecipe(val identifier: Identifier, val blockIn : StateIngredient, val blockOut : BlockState, val mediaCost : Int, val advancement : String?) : Recipe<Inventory> {
    override fun matches(inventory: Inventory, world: World) = false

    fun matches(blockIn : BlockState): Boolean = this.blockIn.test(blockIn)

    override fun craft(inventory: Inventory?): ItemStack = ItemStack.EMPTY

    override fun fits(width: Int, height: Int) = false

    override fun getOutput() : ItemStack = ItemStack.EMPTY.copy()

    override fun getId() = identifier

    override fun getSerializer(): RecipeSerializer<*>  = OneironautRecipeSerializer.INFUSE

    override fun getType(): RecipeType<*> = OneironautRecipeTypes.INFUSION_TYPE

    class Serializer : RecipeSerializerBase<InfusionRecipe>() {
        override fun read(recipeID: Identifier, json: JsonObject): InfusionRecipe {
            val blockIn = StateIngredientHelper.deserialize(JsonHelper.getObject(json, "blockIn"))
            val result = StateIngredientHelper.readBlockState(JsonHelper.getObject(json, "resultType"))
            val cost = JsonHelper.getInt(json, "mediaCost")
            val advancement = JsonHelper.getString(json, "advancement", "")
            return InfusionRecipe(recipeID, blockIn, result, cost, advancement)
        }

        override fun write(buf: PacketByteBuf, recipe: InfusionRecipe) {
            recipe.blockIn.write(buf)
            buf.writeVarInt(Block.getRawIdFromState(recipe.blockOut))
            buf.writeInt(recipe.mediaCost)
        }

        override fun read(recipeID: Identifier, buf: PacketByteBuf): InfusionRecipe {
            val blockIn = StateIngredientHelper.read(buf)
            val result = Block.getStateFromRawId(buf.readVarInt())
            val cost = buf.readInt()
            val advancement = buf.readString()
            return InfusionRecipe(recipeID, blockIn, result, cost, advancement)
        }
    }
}