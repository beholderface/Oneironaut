package net.oneironaut;

import net.oneironaut.registry.OneironautFeatureRegistry;
import net.oneironaut.registry.OneironautIotaTypeRegistry;
import net.oneironaut.registry.OneironautThingRegistry;
import net.oneironaut.registry.OneironautPatternRegistry;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    }

    /**
     * Shortcut for identifiers specific to this mod.
     */
    public static Identifier id(String string) {
        return new Identifier(MOD_ID, string);
    }
}
