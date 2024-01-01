package net.oneironaut.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.oneironaut.Oneironaut;
import net.oneironaut.OneironautConfig;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ram.talia.hexal.api.HexalAPI;
import ram.talia.hexal.api.everbook.Everbook;

import java.nio.file.Path;

@Mixin(Everbook.class)
public abstract class ShutUpEverbookSaveMixin {
    @Unique
    private final Everbook oneironaut$everbook = (Everbook) (Object) this;

    @Redirect(method = "saveToDisk", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;)V", remap = false), remap = false)
    public void shushAboutSaving(Logger instance, String s){
        NbtCompound tag = oneironaut$everbook.serialiseToNBT();
        final Path MINECRAFT_PATH = MinecraftClient.getInstance().runDirectory.toPath();
        Path everbookPath = MINECRAFT_PATH.resolve("everbook/everbook-" + oneironaut$everbook.getUuid() + ".dat");
        if (OneironautConfig.getServer().getReduceEverbookLogSpam()){
            HexalAPI.LOGGER.info("saving everbook of length " + tag.toString().length() + " at " + everbookPath);
            HexalAPI.LOGGER.debug("saving everbook " + tag + " at " + everbookPath);
            Oneironaut.LOGGER.info("I reduced the mostly-useless data spam, check debug log for full everbook data");
        } else {
            HexalAPI.LOGGER.info("saving everbook " + tag + " at " + everbookPath);
        }

        //sadly I couldn't figure out how to make it stop doing it for loading
    }
}
