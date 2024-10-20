package net.beholderface.oneironaut

import net.minecraft.util.Identifier

object OneironautConfig {

        interface CommonConfigAccess { }

        interface ClientConfigAccess { }

        interface ServerConfigAccess {
            //allow Noetic Gateway to teleport other players
            val planeShiftOtherPlayers : Boolean
            //Idea Inscription expiration time, in ticks
            val ideaLifetime : Int
            val swapRequiresNoosphere : Boolean
            val swapSwapsBEs : Boolean
            val impulseRedirectsFireball : Boolean
            val infusionEternalChorus : Boolean
            val allowOverworldReflection : Boolean
            val allowNetherReflection : Boolean

            companion object {
                const val DEFAULT_ALLOW_PLANESHIFT_OTHERS = false
                const val DEFAULT_IDEA_LIFETIME = 20 * 60 * 60 //one hour
                const val DEFAULT_SWAP_NOOSPHERE = true
                const val DEFAULT_SWAP_BES = true
                const val DEFAULT_REDIRECT_FIREBALL = true
                const val DEFAULT_INFUSE_CHORUS = true
                const val DEFAULT_OVERWORLD_REFLECTION = true
                const val DEFAULT_NETHER_REFLECTION = true
            }
        }

        // Simple extensions for resource location configs
        @JvmStatic
        fun anyMatch(keys: MutableList<out String>, key: Identifier): Boolean {
            for (s in keys) {
                if (Identifier.isValid(s)) {
                    val rl = Identifier(s)
                    if (rl == key) {
                        return true
                    }
                }
            }
            return false
        }

        @JvmStatic
        fun noneMatch(keys: MutableList<out String>, key: Identifier): Boolean {
            return !anyMatch(keys, key)
        }

        private object DummyCommon : CommonConfigAccess {  }
        private object DummyClient : ClientConfigAccess {  }
        private object DummyServer : ServerConfigAccess {
            override val planeShiftOtherPlayers: Boolean
                get() = throw IllegalStateException("Attempted to access property of Dummy Config Object")
            override val ideaLifetime: Int
                get() = throw IllegalStateException("Attempted to access property of Dummy Config Object")
            override val swapRequiresNoosphere: Boolean
                get() = throw IllegalStateException("Attempted to access property of Dummy Config Object")
            override val swapSwapsBEs: Boolean
                get() = throw IllegalStateException("Attempted to access property of Dummy Config Object")
            override val impulseRedirectsFireball: Boolean
                get() = throw IllegalStateException("Attempted to access property of Dummy Config Object")
            override val infusionEternalChorus: Boolean
                get() = throw IllegalStateException("Attempted to access property of Dummy Config Object")
            override val allowOverworldReflection: Boolean
                get() = throw IllegalStateException("Attempted to access property of Dummy Config Object")
            override val allowNetherReflection: Boolean
                get() = throw IllegalStateException("Attempted to access property of Dummy Config Object")
        }

        @JvmStatic
        var common: CommonConfigAccess = DummyCommon
            set(access) {
                if (field != DummyCommon) {
                    Oneironaut.LOGGER.warn("CommonConfigAccess was replaced! Old {} New {}",
                        field.javaClass.name, access.javaClass.name)
                }
                field = access
            }

        @JvmStatic
        var client: ClientConfigAccess = DummyClient
            set(access) {
                if (field != DummyClient) {
                    Oneironaut.LOGGER.warn("ClientConfigAccess was replaced! Old {} New {}",
                        field.javaClass.name, access.javaClass.name)
                }
                field = access
            }

        @JvmStatic
        var server: ServerConfigAccess = DummyServer
            set(access) {
                if (field != DummyServer) {
                    Oneironaut.LOGGER.warn("ServerConfigAccess was replaced! Old {} New {}",
                        field.javaClass.name, access.javaClass.name)
                }
                field = access
            }

}