package net.oneironaut.registry;

import at.petrak.hexcasting.common.lib.HexBlocks;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.registry.Registry;
import net.oneironaut.Oneironaut;
import net.oneironaut.block.*;

public class OneironautBlockRegistry {
    //public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Oneironaut.MOD_ID, Registry.ITEM_KEY);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Oneironaut.MOD_ID, Registry.BLOCK_KEY);
    //public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Oneironaut.MOD_ID, Registry.FLUID_KEY);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Oneironaut.MOD_ID, Registry.BLOCK_ENTITY_TYPE_KEY);
    /*public static final DeferredRegister<StatusEffect> EFFECTS = DeferredRegister.create(Oneironaut.MOD_ID, Registry.MOB_EFFECT_KEY);
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(Oneironaut.MOD_ID, Registry.ENCHANTMENT_KEY);*/

    //I will not scream at my computer over this

    public static void init() {
        BLOCKS.register();
        BLOCK_ENTITIES.register();
    }
    public static final RegistrySupplier<Block> PSUEDOAMETHYST_BLOCK = BLOCKS.register("pseudoamethyst_block", () -> new Block(AbstractBlock.Settings.of(Material.AMETHYST)
            .hardness(1.5f)
            .sounds(BlockSoundGroup.AMETHYST_BLOCK)
            .resistance(5)
            .luminance(state -> 7)
            ));
    public static final RegistrySupplier<Block> NOOSPHERE_BASALT = BLOCKS.register("noosphere_basalt", () -> new Block(AbstractBlock.Settings.of(Material.STONE)
            .hardness(1f)
            .sounds(BlockSoundGroup.BASALT)
            .resistance(4)
    ));
    public static final RegistrySupplier<NoosphereGateway> NOOSPHERE_GATE = BLOCKS.register("noosphere_gate", () -> new NoosphereGateway(AbstractBlock.Settings.of(Material.PORTAL).luminance(state -> 15).noCollision().hardness(-1)));
    public static final RegistrySupplier<BlockEntityType<NoosphereGateEntity>> NOOSPHERE_GATE_ENTITY = BLOCK_ENTITIES.register("noosphere_gate_entity", () -> BlockEntityType.Builder.create(NoosphereGateEntity::new, NOOSPHERE_GATE.get()).build(null));
    public static final RegistrySupplier<WispLantern> WISP_LANTERN = BLOCKS.register("wisp_lantern", () -> new WispLantern(AbstractBlock.Settings.of(Material.GLASS).luminance(state -> 15).sounds(BlockSoundGroup.GLASS)));
    public static final RegistrySupplier<WispLanternTinted> WISP_LANTERN_TINTED = BLOCKS.register("wisp_lantern_tinted", () -> new WispLanternTinted(AbstractBlock.Settings.of(Material.GLASS).sounds(BlockSoundGroup.GLASS)));
    public static final RegistrySupplier<BlockEntityType<WispLanternEntity>> WISP_LANTERN_ENTITY = BLOCK_ENTITIES.register("wisp_lantern_entity", () -> BlockEntityType.Builder.create(WispLanternEntity::new, WISP_LANTERN.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<WispLanternEntityTinted>> WISP_LANTERN_ENTITY_TINTED = BLOCK_ENTITIES.register("wisp_lantern_entity_tinted", () -> BlockEntityType.Builder.create(WispLanternEntityTinted::new, WISP_LANTERN_TINTED.get()).build(null));
    public static final RegistrySupplier<ThoughtSlurryBlock> THOUGHT_SLURRY_BLOCK = BLOCKS.register("thought_slurry", () -> ThoughtSlurryBlock.INSTANCE /*new ThoughtSlurryBlock(ThoughtSlurry.STILL_FLUID, AbstractBlock.Settings.copy(Blocks.LAVA))*/);
    public static final RegistrySupplier<SuperBuddingBlock> SUPER_BUDDING = BLOCKS.register("super_budding", () -> new SuperBuddingBlock(AbstractBlock.Settings.of(Material.AMETHYST)));
    public static final RegistrySupplier<SentinelTrapImpetus> SENTINEL_TRAP = BLOCKS.register("sentinel_trap", () -> new SentinelTrapImpetus(AbstractBlock.Settings.of(HexBlocks.SLATE_BLOCK.getDefaultState().getMaterial()).hardness(2f)));
    public static final RegistrySupplier<BlockEntityType<SentinelTrapImpetusEntity>> SENTINEL_TRAP_ENTITY = BLOCK_ENTITIES.register("sentinel_trap_entity", () -> BlockEntityType.Builder.create(SentinelTrapImpetusEntity::new, SENTINEL_TRAP.get()).build(null));
    public static final RegistrySupplier<SentinelSensor> SENTINEL_SENSOR = BLOCKS.register("sentinel_sensor", () -> new SentinelSensor(AbstractBlock.Settings.of(HexBlocks.SLATE_BLOCK.getDefaultState().getMaterial())));
    public static final RegistrySupplier<BlockEntityType<SentinelSensorEntity>> SENTINEL_SENSOR_ENTITY = BLOCK_ENTITIES.register("sentinel_sensor_entity", () -> BlockEntityType.Builder.create(SentinelSensorEntity::new, SENTINEL_SENSOR.get()).build(null));
    public static final RegistrySupplier<Block> RAYCAST_BLOCKER = BLOCKS.register("raycast_blocker", () -> new Block(AbstractBlock.Settings.copy(Blocks.DEEPSLATE)));
    public static final RegistrySupplier<Block> RAYCAST_BLOCKER_GLASS = BLOCKS.register("raycast_blocker_glass", () -> new RaycastBlockerGlass(AbstractBlock.Settings.copy(Blocks.TINTED_GLASS)));
    public static final RegistrySupplier<Block> HEX_RESISTANT_BLOCK = BLOCKS.register("hex_resistant_block", () -> new Block(AbstractBlock.Settings.copy(Blocks.OBSIDIAN).hardness(1.5f)));
    public static final RegistrySupplier<Block> CIRCLE = BLOCKS.register("circle", () -> new CircleBlock(AbstractBlock.Settings.copy(Blocks.BLACK_CONCRETE)
            .nonOpaque().breakInstantly()));
    public static final RegistrySupplier<Block> MEDIA_ICE = BLOCKS.register("media_ice", ()-> new Block(AbstractBlock.Settings.copy(Blocks.PACKED_ICE)
            .slipperiness(1.1f).mapColor(MapColor.PALE_PURPLE)
    ));
    public static final RegistrySupplier<MediaGelBlock> MEDIA_GEL = BLOCKS.register("media_gel", ()-> new MediaGelBlock(AbstractBlock.Settings.copy(Blocks.SLIME_BLOCK)
            .velocityMultiplier(0.05f).jumpVelocityMultiplier(0.25f).mapColor(MapColor.PALE_PURPLE).sounds(BlockSoundGroup.SLIME).nonOpaque().hardness(Blocks.SOUL_SAND.getHardness())
    ));
    //will eventually do something related to cellular automata, and be related to the media gel
    public static final RegistrySupplier<CellBlock> CELL = BLOCKS.register("cell", ()-> new CellBlock(AbstractBlock.Settings.copy(Blocks.SLIME_BLOCK)
            .velocityMultiplier(0.6f).jumpVelocityMultiplier(0.75f).mapColor(MapColor.PALE_PURPLE).sounds(BlockSoundGroup.SLIME).nonOpaque().hardness(Blocks.SOUL_SAND.getHardness())
    ));
    public static final RegistrySupplier<BlockEntityType<CellEntity>> CELL_ENTITY = BLOCK_ENTITIES.register("cell_entity", () -> BlockEntityType.Builder.create(CellEntity::new, CELL.get()).build(null));


    //used for the eternal chorus mixin
    public static final BooleanProperty ETERNAL = BooleanProperty.of("eternal");
}
