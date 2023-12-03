package net.oneironaut.forge;

import net.oneironaut.OneironautClient;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * Forge client loading entrypoint.
 */
public class OneironautClientForge {
    public static void init(FMLClientSetupEvent event) {
        OneironautClient.init();
    }
}
