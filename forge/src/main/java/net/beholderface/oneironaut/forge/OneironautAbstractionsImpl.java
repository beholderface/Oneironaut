package net.beholderface.oneironaut.forge;

import net.beholderface.oneironaut.OneironautAbstractions;
import net.beholderface.oneironaut.OneironautAbstractions;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class OneironautAbstractionsImpl {
    /**
     * This is the actual implementation of {@link OneironautAbstractions#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }
}
