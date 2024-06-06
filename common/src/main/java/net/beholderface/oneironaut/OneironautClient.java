package net.beholderface.oneironaut;

import at.petrak.hexcasting.common.items.ItemStaff;
import at.petrak.hexcasting.common.items.magic.ItemPackagedHex;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.platform.Platform;
import dev.architectury.registry.item.ItemPropertiesRegistry;
import net.beholderface.oneironaut.registry.OneironautBlockRegistry;
import net.beholderface.oneironaut.registry.OneironautItemRegistry;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.block.Block;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.beholderface.oneironaut.block.ThoughtSlurry;
import net.beholderface.oneironaut.item.ReverberationRod;
import net.beholderface.oneironaut.registry.OneironautBlockRegistry;
import net.beholderface.oneironaut.registry.OneironautItemRegistry;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Common client loading entrypoint.
 */
public class OneironautClient {

    private static int applyBlockRenderLayers(Block[] blocks, RenderLayer layer){
        int applied = 0;
        for (Block block : blocks){
            BlockRenderLayerMap.INSTANCE.putBlock(block, layer);
            applied++;
        }
        return applied;
    }

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

            Block[] cutoutBlocks = {OneironautBlockRegistry.WISP_LANTERN.get(), OneironautBlockRegistry.WISP_LANTERN_TINTED.get(),
                    OneironautBlockRegistry.WISP_BATTERY.get(), OneironautBlockRegistry.WISP_BATTERY_DECORATIVE.get(),
                    OneironautBlockRegistry.CIRCLE.get()};
            Block[] translucentBlocks = {OneironautBlockRegistry.RAYCAST_BLOCKER_GLASS.get(), OneironautBlockRegistry.MEDIA_GEL.get(),
                    OneironautBlockRegistry.CELL.get()};

            BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), ThoughtSlurry.STILL_FLUID, ThoughtSlurry.FLOWING_FLUID);

            Oneironaut.LOGGER.info("Applied cutout layer to " + applyBlockRenderLayers(cutoutBlocks, RenderLayer.getCutout()) + " blocks");
            Oneironaut.LOGGER.info("Applied translucent layer to " + applyBlockRenderLayers(translucentBlocks, RenderLayer.getTranslucent()) + " blocks");

            /*BlockRenderLayerMap.INSTANCE.putBlock(OneironautBlockRegistry.WISP_LANTERN.get(), RenderLayer.getCutout());
            BlockRenderLayerMap.INSTANCE.putBlock(OneironautBlockRegistry.WISP_LANTERN_TINTED.get(), RenderLayer.getCutout());
            BlockRenderLayerMap.INSTANCE.putBlock(OneironautBlockRegistry.WISP_BATTERY.get(), RenderLayer.getCutout());
            BlockRenderLayerMap.INSTANCE.putBlock(OneironautBlockRegistry.WISP_BATTERY_DECORATIVE.get(), RenderLayer.getCutout());
            BlockRenderLayerMap.INSTANCE.putBlock(OneironautBlockRegistry.CIRCLE.get(), RenderLayer.getCutout());*/
            /*BlockRenderLayerMap.INSTANCE.putBlock(OneironautBlockRegistry.RAYCAST_BLOCKER_GLASS.get(), RenderLayer.getTranslucent());
            BlockRenderLayerMap.INSTANCE.putBlock(OneironautBlockRegistry.MEDIA_GEL.get(), RenderLayer.getTranslucent());
            BlockRenderLayerMap.INSTANCE.putBlock(OneironautBlockRegistry.CELL.get(), RenderLayer.getTranslucent());*/
        } else {
            Oneironaut.LOGGER.info("oh no, forge, aaaaaaaaaaaa");
        }

        ItemPackagedHex[] castingItems = {OneironautItemRegistry.REVERBERATION_ROD.get(), OneironautItemRegistry.INSULATED_TRINKET.get()};
        for (ItemPackagedHex item : castingItems){
            ItemPropertiesRegistry.register(item, ItemPackagedHex.HAS_PATTERNS_PRED, (stack, world, holder, holderID) -> {
                return item.hasHex(stack) ? 0.99f : -0.01f;
            });
        }

        ItemPropertiesRegistry.register(OneironautItemRegistry.REVERBERATION_ROD.get(), ReverberationRod.CASTING_PREDICATE, (stack, world, holder, holderID) -> {
            //return 0.99f;
            if (holder != null){
                //return 0.99f;
                return holder.getActiveItem().equals(stack) ? 0.99f : -0.01f;
            } else {
                return -0.01f;
            }
            //return OneironautItemRegistry.REVERBERATION_ROD.get().hasHex(stack) ? 0.99f : -0.01f;
        });

        //ah yes, because I definitely want to turn my expensive staff into a much less expensive variant
        Item[] nameSensitiveStaves = {OneironautItemRegistry.ECHO_STAFF.get(), OneironautItemRegistry.BEACON_STAFF.get(), OneironautItemRegistry.SPOON_STAFF.get()};
        for (Item staff: nameSensitiveStaves) {
            ItemPropertiesRegistry.register(staff, ItemStaff.FUNNY_LEVEL_PREDICATE, (stack, level, holder, holderID) -> {
                if (!stack.hasCustomName()) {
                    return 0;
                }
                var name = stack.getName().getString().toLowerCase(Locale.ROOT);
                if (name.contains("old")) {
                    return 1f;
                } else if (name.contains("wand of the forest")) {
                    return 2f;
                } else {
                    return 0f;
                }
            });
        }
    }
}
