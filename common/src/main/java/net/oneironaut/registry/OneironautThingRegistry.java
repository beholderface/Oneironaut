package net.oneironaut.registry;

import at.petrak.hexcasting.common.lib.HexItems;
import dev.architectury.core.item.ArchitecturyBucketItem;
import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BlockItem;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.oneironaut.Oneironaut;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;
import net.oneironaut.block.*;
import net.oneironaut.item.BottomlessMediaItem;
import net.oneironaut.item.ReverberationRod;
import net.oneironaut.item.PseudoamethystShard;

public class OneironautThingRegistry /*implements ModInitializer */{
    // Register items through this
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Oneironaut.MOD_ID, Registry.ITEM_KEY);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Oneironaut.MOD_ID, Registry.BLOCK_KEY);
    //public static final DeferredRegister<BlockEntity> BLOCK_ENTITIES = DeferredRegister.create(Oneironaut.MOD_ID, Registry.BLOCK_ENTITY_TYPE.getKey());
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Oneironaut.MOD_ID, Registry.FLUID_KEY);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Oneironaut.MOD_ID, Registry.BLOCK_ENTITY_TYPE_KEY);
    //public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Oneironaut.MOD_ID, Registry.FEATURE_KEY);
    //public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIG_FEATURES = DeferredRegister.create(Oneironaut.MOD_ID, Registry.CONFIGURED_FEATURE_KEY);



    public static void init() {
        if (Platform.isFabric()){
            FLUIDS.register();
            BLOCKS.register();
            BLOCK_ENTITIES.register();
            ITEMS.register();
        } else {
            BLOCKS.register();
            BLOCK_ENTITIES.register();
            FLUIDS.register();
            ITEMS.register();
        }
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
    /*public static final Block NOOSPHERE_GATE = Registry.register(
            Registry.BLOCK,
            new Identifier(Oneironaut.MOD_ID, "noosphere_gate"),
            new NoosphereGateway(AbstractBlock.Settings.copy(Blocks.END_GATEWAY).luminance(state -> 15).sounds(BlockSoundGroup.AMETHYST_BLOCK))
    );*/
    public static final RegistrySupplier<NoosphereGateway> NOOSPHERE_GATE = BLOCKS.register("noosphere_gate", () -> new NoosphereGateway(AbstractBlock.Settings.of(Material.PORTAL).luminance(state -> 15).noCollision()));

    /*public static final BlockEntityType<NoosphereGateEntity> NOOSPHERE_GATE_ENTITY = Registry.register(
            Registry.BLOCK_ENTITY_TYPE,
            new Identifier(Oneironaut.MOD_ID, "noosphere_gate_entity"),
            BlockEntityType.Builder.create(NoosphereGateEntity::new, NOOSPHERE_GATE).build(null));*/

    public static final RegistrySupplier<BlockEntityType<NoosphereGateEntity>> NOOSPHERE_GATE_ENTITY = BLOCK_ENTITIES.register("noosphere_gate_entity", () -> BlockEntityType.Builder.create(NoosphereGateEntity::new, OneironautThingRegistry.NOOSPHERE_GATE.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<WispLanternEntity>> WISP_LANTERN_ENTITY = BLOCK_ENTITIES.register("wisp_lantern_entity", () -> BlockEntityType.Builder.create(WispLanternEntity::new, OneironautThingRegistry.WISP_LANTERN.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<WispLanternEntityTinted>> WISP_LANTERN_ENTITY_TINTED = BLOCK_ENTITIES.register("wisp_lantern_entity_tinted", () -> BlockEntityType.Builder.create(WispLanternEntityTinted::new, OneironautThingRegistry.WISP_LANTERN_TINTED.get()).build(null));

    /*public static final Block WISP_LANTERN = Registry.register(
            Registry.BLOCK,
            new Identifier(Oneironaut.MOD_ID, "wisp_lantern"),
            new WispLantern(AbstractBlock.Settings.copy(Blocks.GLASS).luminance(state -> 15))
    );*/
    public static final RegistrySupplier<WispLantern> WISP_LANTERN = BLOCKS.register("wisp_lantern", () -> new WispLantern(AbstractBlock.Settings.of(Material.GLASS).luminance(state -> 15)));
    public static final RegistrySupplier<WispLanternTinted> WISP_LANTERN_TINTED = BLOCKS.register("wisp_lantern_tinted", () -> new WispLanternTinted(AbstractBlock.Settings.of(Material.GLASS)));


    /*public static final Block WISP_LANTERN_TINTED = Registry.register(
            Registry.BLOCK,
            new Identifier(Oneironaut.MOD_ID, "wisp_lantern_tinted"),
            new WispLanternTinted(AbstractBlock.Settings.copy(Blocks.GLASS).luminance(state -> 0))
    );*/
    /*public static final BlockEntityType<WispLanternEntity> WISP_LANTERN_ENTITY = Registry.register(
            Registry.BLOCK_ENTITY_TYPE,
            new Identifier(Oneironaut.MOD_ID, "wisp_lantern_entity"),
            BlockEntityType.Builder.create(WispLanternEntity::new, WISP_LANTERN).build(null));

    public static final BlockEntityType<WispLanternEntityTinted> WISP_LANTERN_ENTITY_TINTED = Registry.register(
            Registry.BLOCK_ENTITY_TYPE,
            new Identifier(Oneironaut.MOD_ID, "wisp_lantern_entity_tinted"),
            BlockEntityType.Builder.create(WispLanternEntityTinted::new, WISP_LANTERN_TINTED).build(null));*/
    public static final RegistrySupplier<Item> WISP_LANTERN_ITEM = ITEMS.register("wisp_lantern", () -> new BlockItem(WISP_LANTERN.get(), HexItems.props()));
    public static final RegistrySupplier<Item> WISP_LANTERN_TINTED_ITEM = ITEMS.register("wisp_lantern_tinted", () -> new BlockItem(WISP_LANTERN_TINTED.get(), HexItems.props()));

    public static final RegistrySupplier<Item> PSUEDOAMETHYST_SHARD = ITEMS.register("pseudoamethyst_shard", () -> new PseudoamethystShard(HexItems.props()));

    //why architectury documentation no worky

    public static final RegistrySupplier<ThoughtSlurry> THOUGHT_SLURRY = FLUIDS.register("thought_slurry", () -> ThoughtSlurry.STILL_FLUID /*new ThoughtSlurry.Still(OneironautThingRegistry.THOUGHT_SLURRY_ATTRIBUTES)*/);
    public static final RegistrySupplier<ThoughtSlurry> THOUGHT_SLURRY_FLOWING = FLUIDS.register("thought_slurry_flowing", () -> ThoughtSlurry.FLOWING_FLUID /*new ThoughtSlurry.Flowing(OneironautThingRegistry.THOUGHT_SLURRY_ATTRIBUTES)*/);
    //why level no exist
    public static final RegistrySupplier<ThoughtSlurryBlock> THOUGHT_SLURRY_BLOCK = BLOCKS.register("thought_slurry", () -> ThoughtSlurryBlock.INSTANCE /*new ThoughtSlurryBlock(ThoughtSlurry.STILL_FLUID, AbstractBlock.Settings.copy(Blocks.LAVA))*/);
    public static final RegistrySupplier<ArchitecturyBucketItem> THOUGHT_SLURRY_BUCKET = ITEMS.register("thought_slurry_bucket", () -> new ArchitecturyBucketItem(THOUGHT_SLURRY, HexItems.unstackable()));
    public static final RegistrySupplier<ReverberationRod> REVERBERATION_ROD = ITEMS.register("reverberation_rod", () -> new ReverberationRod(HexItems.unstackable()));
    public static final RegistrySupplier<BottomlessMediaItem> BOTTOMLESS_MEDIA_ITEM = ITEMS.register("endless_phial", () -> new BottomlessMediaItem(HexItems.unstackable()));

    public static final RegistrySupplier<SuperBuddingBlock> SUPER_BUDDING = BLOCKS.register("super_budding", () -> new SuperBuddingBlock(AbstractBlock.Settings.of(Material.AMETHYST)
    ));
    public static final RegistrySupplier<Item> SUPER_BUDDING_ITEM = ITEMS.register("super_budding", () -> new BlockItem(SUPER_BUDDING.get(), HexItems.props()));

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
