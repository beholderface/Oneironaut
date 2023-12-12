package net.oneironaut.registry;

import at.petrak.hexcasting.common.lib.HexItems;
import dev.architectury.core.item.ArchitecturyBucketItem;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registries;
import dev.architectury.registry.registries.RegistrySupplier;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModification;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BlockItem;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.oneironaut.Oneironaut;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;
import net.oneironaut.block.*;
import net.oneironaut.feature.NoosphereSeaIsland;
import net.oneironaut.feature.NoosphereSeaIslandConfig;
import ram.talia.hexal.common.lib.HexalEntities;

import java.util.List;
import java.util.function.Predicate;

public class OneironautThingRegistry /*implements ModInitializer */{
    // Register items through this
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Oneironaut.MOD_ID, Registry.ITEM_KEY);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Oneironaut.MOD_ID, Registry.BLOCK_KEY);
    //public static final DeferredRegister<BlockEntity> BLOCK_ENTITIES = DeferredRegister.create(Oneironaut.MOD_ID, Registry.BLOCK_ENTITY_TYPE.getKey());
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Oneironaut.MOD_ID, Registry.FLUID_KEY);
    //public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Oneironaut.MOD_ID, Registry.FEATURE_KEY);
    //public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIG_FEATURES = DeferredRegister.create(Oneironaut.MOD_ID, Registry.CONFIGURED_FEATURE_KEY);



    public static void init() {
        FLUIDS.register();
        BLOCKS.register();
        ITEMS.register();
        //FEATURES.register();
        //CONFIG_FEATURES.register();
    }


    //public static final RegistrySupplier<Block> STUPID_BLOCK = BLOCKS.register("stupid_block", () -> new Block(AbstractBlock.Settings.of(Material.AMETHYST)));
    //public static final RegistrySupplier<Block> SMART_BLOCK = BLOCKS.register("smart_block", () -> new Block(AbstractBlock.Settings.of(Material.AMETHYST)));
    public static final RegistrySupplier<Block> PSUEDOAMETHYST_BLOCK = BLOCKS.register("pseudoamethyst_block", () -> new Block(AbstractBlock.Settings.of(Material.AMETHYST)
            .hardness(1.5f)
            .sounds(BlockSoundGroup.AMETHYST_BLOCK)
            .resistance(5)
            .luminance(state -> 7)
            ));
    public static final RegistrySupplier<Item> PSUEDOAMETHYST_BLOCK_ITEM = ITEMS.register("pseudoamethyst_block", () -> new BlockItem(PSUEDOAMETHYST_BLOCK.get(), HexItems.props()));

    public static final RegistrySupplier<Block> NOOSPHERE_BASALT = BLOCKS.register("noosphere_basalt", () -> new Block(AbstractBlock.Settings.of(Material.STONE)
            .hardness(1f)
            .sounds(BlockSoundGroup.BASALT)
            .resistance(4)
    ));
    public static final RegistrySupplier<Item> NOOSPHERE_BASALT_ITEM = ITEMS.register("noosphere_basalt", () -> new BlockItem(NOOSPHERE_BASALT.get(), HexItems.props()));

    //public static final Block NOOSPHERE_GATE = new Block(AbstractBlock.Settings.copy(Blocks.END_GATEWAY).luminance(state -> 15).sounds(BlockSoundGroup.AMETHYST_BLOCK));
    public static final Block NOOSPHERE_GATE = Registry.register(
            Registry.BLOCK,
            new Identifier(Oneironaut.MOD_ID, "noosphere_gate"),
            new NoosphereGateway(AbstractBlock.Settings.copy(Blocks.END_GATEWAY).luminance(state -> 15).sounds(BlockSoundGroup.AMETHYST_BLOCK))
    );
    public static final BlockEntityType<NoosphereGateEntity> NOOSPHERE_GATE_ENTITY = Registry.register(
            Registry.BLOCK_ENTITY_TYPE,
            new Identifier(Oneironaut.MOD_ID, "noosphere_gate_entity"),
            BlockEntityType.Builder.create(NoosphereGateEntity::new, NOOSPHERE_GATE).build(null));

    public static final Block WISP_LANTERN = Registry.register(
            Registry.BLOCK,
            new Identifier(Oneironaut.MOD_ID, "wisp_lantern"),
            new WispLantern(AbstractBlock.Settings.copy(Blocks.LANTERN).luminance(state -> 15).sounds(BlockSoundGroup.GLASS).nonOpaque())
    );
    public static final BlockEntityType<WispLanternEntity> WISP_LANTERN_ENTITY = Registry.register(
            Registry.BLOCK_ENTITY_TYPE,
            new Identifier(Oneironaut.MOD_ID, "wisp_lantern_entity"),
            BlockEntityType.Builder.create(WispLanternEntity::new, WISP_LANTERN).build(null));
    public static final RegistrySupplier<Item> WISP_LANTERN_ITEM = ITEMS.register("wisp_lantern", () -> new BlockItem(WISP_LANTERN, HexItems.props()));

    public static final RegistrySupplier<Item> PSUEDOAMETHYST_SHARD = ITEMS.register("pseudoamethyst_shard", () -> new Item(HexItems.props()));

    //why architectury documentation no worky

    public static final RegistrySupplier<FlowableFluid> THOUGHT_SLURRY = FLUIDS.register("thought_slurry", () -> ThoughtSlurry.STILL_FLUID /*new ThoughtSlurry.Still(OneironautThingRegistry.THOUGHT_SLURRY_ATTRIBUTES)*/);
    public static final RegistrySupplier<FlowableFluid> THOUGHT_SLURRY_FLOWING = FLUIDS.register("thought_slurry_flowing", () -> ThoughtSlurry.FLOWING_FLUID /*new ThoughtSlurry.Flowing(OneironautThingRegistry.THOUGHT_SLURRY_ATTRIBUTES)*/);
    //why level no exist
    public static final RegistrySupplier<FluidBlock> THOUGHT_SLURRY_BLOCK = BLOCKS.register("thought_slurry", () -> ThoughtSlurryBlock.INSTANCE /*new ThoughtSlurryBlock(ThoughtSlurry.STILL_FLUID, AbstractBlock.Settings.copy(Blocks.LAVA))*/);
    public static final RegistrySupplier<Item> THOUGHT_SLURRY_BUCKET = ITEMS.register("thought_slurry_bucket", () -> new ArchitecturyBucketItem(THOUGHT_SLURRY, HexItems.unstackable()));




    //@Override
    //public void onInitialize(){
        /*Registry.register(Registry.FEATURE, NOOSPHERE_SEA_ISLAND_ID, NOOSPHERE_SEA_ISLAND);
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, NOOSPHERE_SEA_ISLAND_ID, NOOSPHERE_SEA_ISLAND_SMALL);
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, NOOSPHERE_SEA_ISLAND_ID, NOOSPHERE_SEA_ISLAND_MEDIUM);
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, NOOSPHERE_SEA_ISLAND_ID, NOOSPHERE_SEA_ISLAND_LARGE);*/
    //}


    // A new creative tab. Notice how it is one of the few things that are not deferred
    //public static final ItemGroup DUMMY_GROUP = CreativeTabRegistry.create(id("dummy_group"), () -> new ItemStack(OneironautItemRegistry.DUMMY_ITEM.get()));



}
