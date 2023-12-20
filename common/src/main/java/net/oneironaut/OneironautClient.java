package net.oneironaut;

import at.petrak.hexcasting.common.items.magic.ItemPackagedHex;
import dev.architectury.platform.Platform;
import dev.architectury.registry.item.ItemPropertiesRegistry;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.FluidRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.oneironaut.block.ThoughtSlurry;
import net.oneironaut.registry.OneironautThingRegistry;

/**
 * Common client loading entrypoint.
 */
public class OneironautClient {
    public static void init() {

        if (Platform.isFabric()){
            ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) -> {
                registry.register(new Identifier("oneironaut:block/thought_slurry"));
                registry.register(new Identifier("oneironaut:block/thought_slurry_flowing"));
            });

            FluidRenderHandlerRegistry.INSTANCE.register(ThoughtSlurry.STILL_FLUID, ThoughtSlurry.FLOWING_FLUID, new SimpleFluidRenderHandler(
                    new Identifier("oneironaut:block/thought_slurry"),
                    new Identifier("oneironaut:block/thought_slurry_flowing"),
                    0x8621c2
            ));

            BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), ThoughtSlurry.STILL_FLUID, ThoughtSlurry.FLOWING_FLUID);
            BlockRenderLayerMap.INSTANCE.putBlock(OneironautThingRegistry.WISP_LANTERN.get(), RenderLayer.getCutout());
            BlockRenderLayerMap.INSTANCE.putBlock(OneironautThingRegistry.WISP_LANTERN_TINTED.get(), RenderLayer.getCutout());
        } else {
            Oneironaut.LOGGER.info("oh no, forge, aaaaaaaaaaaa");
        }

        ItemPropertiesRegistry.register(OneironautThingRegistry.REVERBERATION_ROD.get(), ItemPackagedHex.HAS_PATTERNS_PRED, (stack, level, holder, holderID) -> {
            return OneironautThingRegistry.REVERBERATION_ROD.get().hasHex(stack) ? 0.99f : -0.01f;
        });
    }
}
