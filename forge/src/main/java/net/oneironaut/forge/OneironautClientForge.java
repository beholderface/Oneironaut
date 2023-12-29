package net.oneironaut.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.oneironaut.Oneironaut;
import net.oneironaut.OneironautClient;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * Forge client loading entrypoint.
 */
//@Mod.EventBusSubscriber(modid = Oneironaut.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class OneironautClientForge {
    public static void init(FMLClientSetupEvent event) {
        //Oneironaut.LOGGER.info("arg blarg jarg");
        OneironautClient.init();
    }
    public OneironautClientForge(){
        //Oneironaut.LOGGER.info("rarg blarg jarg");
        OneironautClient.init();
    }
}
