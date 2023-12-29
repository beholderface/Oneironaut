package net.oneironaut.forge;

import dev.architectury.platform.forge.EventBuses;
import net.oneironaut.Oneironaut;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.oneironaut.block.ThoughtSlurry;

/**
 * This is your loading entrypoint on forge, in case you need to initialize
 * something platform-specific.
 */
@Mod(Oneironaut.MOD_ID)
public class OneironautForge {
    public OneironautForge() {
        // Submit our event bus to let architectury register our content on the right time
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(OneironautClientForge::init);
        ForgeBlockInit.BLOCKS.register(bus);
        ForgeItemInit.ITEMS.register(bus);
        ForgeFluidInit.FLUID_TYPES.register(bus);
        ForgeFluidInit.FLUIDS.register(bus);
        EventBuses.registerModEventBus(Oneironaut.MOD_ID, bus);
        Oneironaut.init();
    }
}
