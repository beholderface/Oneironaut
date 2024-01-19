package net.oneironaut.recipe

import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.util.Identifier
import net.oneironaut.Oneironaut
import java.util.function.BiConsumer

class OneironautRecipeSerializer {
    companion object {
        @JvmStatic
        fun registerSerializers(r: BiConsumer<RecipeSerializer<*>, Identifier>) {
            for ((key, value) in SERIALIZERS) {
                r.accept(value, key)
            }
        }

        private val SERIALIZERS: MutableMap<Identifier, RecipeSerializer<*>> = LinkedHashMap()

        val INFUSE: RecipeSerializer<*> = register("infuse", InfusionRecipe.Serializer())

        private fun <T : Recipe<*>?> register(name: String, rs: RecipeSerializer<T>): RecipeSerializer<T> {
            val old = SERIALIZERS.put(Oneironaut.id(name), rs)
            require(old == null) { "Typo? Duplicate id $name" }
            return rs
        }
    }
}