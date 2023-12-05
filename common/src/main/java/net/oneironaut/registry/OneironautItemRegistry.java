package net.oneironaut.registry;

import dev.architectury.core.block.ArchitecturyLiquidBlock;
import dev.architectury.core.fluid.ArchitecturyFlowingFluid;
import dev.architectury.core.fluid.ArchitecturyFluidAttributes;
import dev.architectury.core.fluid.SimpleArchitecturyFluidAttributes;
import dev.architectury.core.item.ArchitecturyBucketItem;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.oneironaut.Oneironaut;
import net.minecraft.item.Item;
import at.petrak.hexcasting.xplat.IClientXplatAbstractions;
import at.petrak.hexcasting.common.lib.HexItems;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;

import java.util.function.Supplier;

import static net.oneironaut.Oneironaut.id;

public class OneironautItemRegistry {
    // Register items through this
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Oneironaut.MOD_ID, Registry.ITEM_KEY);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Oneironaut.MOD_ID, Registry.BLOCK_KEY);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Oneironaut.MOD_ID, Registry.FLUID_KEY);
    //public static final DeferredRegister<FluidBlock> FLUID_BLOCKS = DeferredRegister.create(Oneironaut.MOD_ID, Registry.FLUID_KEY);
    public static void init() {
        FLUIDS.register();
        BLOCKS.register();
        ITEMS.register();
    }
    //why architectury documentation no worky
    /*public static final ArchitecturyFluidAttributes THOUGHT_SLURRY_ATTRIBUTES = new SimpleArchitecturyFluidAttributes(OneironautItemRegistry.THOUGHT_SLURRY, OneironautItemRegistry.THOUGHT_SLURRY_FLOWING)
            .blockSupplier(() -> OneironautItemRegistry.THOUGHT_SLURRY_BLOCK)
            .bucketItemSupplier(() -> OneironautItemRegistry.THOUGHT_SLURRY_BUCKET);
    public static final RegistrySupplier<Fluid> THOUGHT_SLURRY = FLUIDS.register("thought_slurry", () -> new ArchitecturyFlowingFluid.Source(THOUGHT_SLURRY_ATTRIBUTES));
    public static final RegistrySupplier<Fluid> THOUGHT_SLURRY_FLOWING = FLUIDS.register("thought_slurry_flowing", () -> new ArchitecturyFlowingFluid.Flowing(THOUGHT_SLURRY_ATTRIBUTES));
    public static final RegistrySupplier<FluidBlock> THOUGHT_SLURRY_BLOCK = BLOCKS.register("thought_slurry", () -> new ArchitecturyLiquidBlock((Supplier<? extends FlowableFluid>) THOUGHT_SLURRY, AbstractBlock.Settings.copy(Blocks.LAVA)));
    public static final RegistrySupplier<Item> THOUGHT_SLURRY_BUCKET = ITEMS.register("example_fluid_bucket", () -> new ArchitecturyBucketItem(THOUGHT_SLURRY, HexItems.unstackable()));*/




    // A new creative tab. Notice how it is one of the few things that are not deferred
    //public static final ItemGroup DUMMY_GROUP = CreativeTabRegistry.create(id("dummy_group"), () -> new ItemStack(OneironautItemRegistry.DUMMY_ITEM.get()));

    // During the loading phase, refrain from accessing suppliers' items (e.g. EXAMPLE_ITEM.get()), they will not be available
    //public static final RegistrySupplier<Item> DUMMY_ITEM = ITEMS.register("dummy_item", () -> new Item(new Item.Settings().group(DUMMY_GROUP)));


}
