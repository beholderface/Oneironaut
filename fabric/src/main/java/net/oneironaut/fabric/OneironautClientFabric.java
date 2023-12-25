package net.oneironaut.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.oneironaut.OneironautClient;
import net.oneironaut.fabric.FabricPacketHandler.*;

/**
 * Fabric client loading entrypoint.
 */
public class OneironautClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        OneironautClient.init();
        FabricPacketHandler.INSTANCE.initClientBound();
    }
}
