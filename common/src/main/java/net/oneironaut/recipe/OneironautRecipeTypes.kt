package net.oneironaut.recipe

import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeType
import net.minecraft.util.Identifier
import net.oneironaut.Oneironaut.MOD_ID
import net.oneironaut.Oneironaut.id
import java.util.function.BiConsumer

class OneironautRecipeTypes {
    companion object {
        @JvmStatic
        fun registerTypes(r: BiConsumer<RecipeType<*>, Identifier>) {
            for ((key, value) in TYPES) {
                r.accept(value, key)
            }
        }

        private val TYPES: MutableMap<Identifier, RecipeType<*>> = LinkedHashMap()

        var INFUSION_TYPE: RecipeType<InfusionRecipe> = registerType("infuse")

        private fun <T : Recipe<*>> registerType(name: String): RecipeType<T> {
            val type: RecipeType<T> = object : RecipeType<T> {
                override fun toString(): String {
                    return "$MOD_ID:$name"
                }
            }
            // never will be a collision because it's a new object
            TYPES[id(name)] = type
            return type
        }
    }
}