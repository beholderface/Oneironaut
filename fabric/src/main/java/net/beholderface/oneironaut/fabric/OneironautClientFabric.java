package net.beholderface.oneironaut.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.beholderface.oneironaut.OneironautClient;
import net.beholderface.oneironaut.fabric.FabricPacketHandler.*;

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
