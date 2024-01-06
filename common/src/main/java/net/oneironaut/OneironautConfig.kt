package net.oneironaut

import net.minecraft.util.Identifier

object OneironautConfig {

        interface CommonConfigAccess { }

        interface ClientConfigAccess { }

        interface ServerConfigAccess {
            //allow Noetic Gateway to teleport other players
            val planeShiftOtherPlayers : Boolean
            //Idea Inscription expiration time, in ticks
            val ideaLifetime : Int
            //val reduceEverbookLogSpam : Boolean
            val swapRequiresNoosphere : Boolean
            val swapSwapsBEs : Boolean

            companion object {
                const val DEFAULT_ALLOW_PLANESHIFT_OTHERS = false
                //const val DEFAULT_REDUCE_EVERBOOK_SPAM = true
                const val DEFAULT_IDEA_LIFETIME = 20 * 60 * 60 //one hour
                const val DEFAULT_SWAP_NOOSPHERE = true
                const val DEFAULT_SWAP_BES = false
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
            /*override val reduceEverbookLogSpam: Boolean
                get() = throw IllegalStateException("Attempted to access property of Dummy Config Object")*/
            override val ideaLifetime: Int
                get() = throw IllegalStateException("Attempted to access property of Dummy Config Object")
            override val swapRequiresNoosphere: Boolean
                get() = throw IllegalStateException("Attempted to access property of Dummy Config Object")
            override val swapSwapsBEs: Boolean
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