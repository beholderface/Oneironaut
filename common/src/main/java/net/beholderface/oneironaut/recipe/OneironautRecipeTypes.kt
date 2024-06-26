package net.beholderface.oneironaut.recipe

import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeType
import net.minecraft.util.Identifier
import net.beholderface.oneironaut.Oneironaut.MOD_ID
import net.beholderface.oneironaut.Oneironaut.id
import java.util.function.BiConsumer
import net.minecraft.util.registry.Registry;
import net.beholderface.oneironaut.Oneironaut

class OneironautRecipeTypes {
    companion object {
        const val debugMessages = false
        @JvmStatic
        fun registerTypes(r: BiConsumer<RecipeType<*>, Identifier>) {
            for ((key, value) in TYPES) {
                Oneironaut.boolLogger("Attempting to register type $value with key $key", debugMessages)
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
            Oneironaut.boolLogger("Attempting to register type $name, with id ${type.toString()}", debugMessages)
            return type
        }

        public fun <T> bind(registry: Registry<in T>): BiConsumer<T, Identifier> =
            BiConsumer<T, Identifier> { t, id -> Registry.register(registry, id, t) }
    }
}