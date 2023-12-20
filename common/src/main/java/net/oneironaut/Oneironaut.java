package net.oneironaut;

import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.TickEvent;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.oneironaut.casting.IdeaInscriptionManager;
import net.oneironaut.registry.OneironautFeatureRegistry;
import net.oneironaut.registry.OneironautIotaTypeRegistry;
import net.oneironaut.registry.OneironautThingRegistry;
import net.oneironaut.registry.OneironautPatternRegistry;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static net.minecraft.server.command.CommandManager.literal;

/**
 * This is effectively the loading entrypoint for most of your code, at least
 * if you are using Architectury as intended.
 */
public class Oneironaut {
    public static final String MOD_ID = "oneironaut";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);


    public static void init() {
        LOGGER.info("why do they call it oven when you of in the cold food of out hot eat the food");

        OneironautThingRegistry.init();
        OneironautIotaTypeRegistry.init();
        OneironautPatternRegistry.init();
        OneironautFeatureRegistry.init();

        //Registry.register(Registry.CHUNK_GENERATOR, new Identifier(MOD_ID, "noosphere"))

        LOGGER.info(OneironautAbstractions.getConfigDirectory().toAbsolutePath().normalize().toString());
        LifecycleEvent.SERVER_STARTED.register((startedserver) ->{
            IdeaInscriptionManager ideaState = IdeaInscriptionManager.getServerState(startedserver);
            IdeaInscriptionManager.cleanMap(startedserver, ideaState);
            ideaState.markDirty();
        });
        CommandRegistrationEvent.EVENT.register(((dispatcher, registryAccess, environment) -> dispatcher.register(literal("clearinscribedideas")
                .requires(source -> source.hasPermissionLevel(3))
                .executes(context -> {
                    IdeaInscriptionManager.eraseIota("everything");
                    context.getSource().sendFeedback(Text.translatable("text.oneironaut.clearIdeasResponse"), true);
                    return 1;
                })
        )));
        //IdeaInscriptionManager ideaState = IdeaInscriptionManager.getServerState()
    }

    /**
     * Shortcut for identifiers specific to this mod.
     */
    public static Identifier id(String string) {
        return new Identifier(MOD_ID, string);
    }
}
