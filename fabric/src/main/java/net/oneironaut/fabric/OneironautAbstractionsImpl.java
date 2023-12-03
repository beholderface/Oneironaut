package net.oneironaut.fabric;

import net.fabricmc.loader.api.FabricLoader;
import net.oneironaut.OneironautAbstractions;

import java.nio.file.Path;

public class OneironautAbstractionsImpl {
    /**
     * This is the actual implementation of {@link OneironautAbstractions#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }
}
