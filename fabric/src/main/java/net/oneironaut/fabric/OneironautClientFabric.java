package net.oneironaut.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.oneironaut.OneironautClient;

/**
 * Fabric client loading entrypoint.
 */
public class OneironautClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        OneironautClient.init();
    }
}
