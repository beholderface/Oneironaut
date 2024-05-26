package net.oneironaut.registry;

import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.common.items.ItemStaff;
import dev.architectury.core.item.ArchitecturyBucketItem;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.item.*;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Rarity;
import net.oneironaut.Oneironaut;
import net.minecraft.util.registry.Registry;
import net.oneironaut.item.*;

import static net.oneironaut.Oneironaut.id;

public class OneironautItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Oneironaut.MOD_ID, Registry.ITEM_KEY);
    /*public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Oneironaut.MOD_ID, Registry.BLOCK_KEY);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Oneironaut.MOD_ID, Registry.FLUID_KEY);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Oneironaut.MOD_ID, Registry.BLOCK_ENTITY_TYPE_KEY);
    public static final DeferredRegister<StatusEffect> EFFECTS = DeferredRegister.create(Oneironaut.MOD_ID, Registry.MOB_EFFECT_KEY);
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(Oneironaut.MOD_ID, Registry.ENCHANTMENT_KEY);*/

    //I will not scream at my computer over this

    public static void init() {
        ITEMS.register();
    }

    public static final ItemGroup ONEIRONAUT_GROUP = CreativeTabRegistry.create(id("oneironaut"), () -> new ItemStack(OneironautItemRegistry.PSUEDOAMETHYST_SHARD.get()));
    private static final Item.Settings ONEIRONAUT_STACKABLE64 = new Item.Settings().group(ONEIRONAUT_GROUP).maxCount(64);
    private static final Item.Settings ONEIRONAUT_STACKABLE16 = new Item.Settings().group(ONEIRONAUT_GROUP).maxCount(16);
    private static final Item.Settings ONEIRONAUT_UNSTACKABLE = new Item.Settings().group(ONEIRONAUT_GROUP).maxCount(1);

    public static final RegistrySupplier<BlockItem> PSUEDOAMETHYST_BLOCK_ITEM = ITEMS.register("pseudoamethyst_block", () -> new BlockItem(OneironautBlockRegistry.PSUEDOAMETHYST_BLOCK.get(), ONEIRONAUT_STACKABLE64));
    public static final RegistrySupplier<BlockItem> NOOSPHERE_BASALT_ITEM = ITEMS.register("noosphere_basalt", () -> new BlockItem(OneironautBlockRegistry.NOOSPHERE_BASALT.get(), ONEIRONAUT_STACKABLE64));
    public static final RegistrySupplier<BlockItem> WISP_LANTERN_ITEM = ITEMS.register("wisp_lantern", () -> new BlockItem(OneironautBlockRegistry.WISP_LANTERN.get(), ONEIRONAUT_STACKABLE64));
    public static final RegistrySupplier<BlockItem> WISP_LANTERN_TINTED_ITEM = ITEMS.register("wisp_lantern_tinted", () -> new BlockItem(OneironautBlockRegistry.WISP_LANTERN_TINTED.get(), ONEIRONAUT_STACKABLE64));
    public static final RegistrySupplier<ItemStolenMediaProvider> PSUEDOAMETHYST_SHARD = ITEMS.register("pseudoamethyst_shard", () -> new
            ItemStolenMediaProvider(ONEIRONAUT_STACKABLE64, (int) (MediaConstants.SHARD_UNIT * 1.5), 1500));
    public static final RegistrySupplier<ArchitecturyBucketItem> THOUGHT_SLURRY_BUCKET = ITEMS.register("thought_slurry_bucket", () -> new ArchitecturyBucketItem(OneironautMiscRegistry.THOUGHT_SLURRY, ONEIRONAUT_UNSTACKABLE));
    public static final RegistrySupplier<ReverberationRod> REVERBERATION_ROD = ITEMS.register("reverberation_rod", () -> new ReverberationRod(ONEIRONAUT_UNSTACKABLE));
    public static final RegistrySupplier<InsulatedTrinketItem> INSULATED_TRINKET = ITEMS.register("insulated_trinket", () -> new InsulatedTrinketItem(ONEIRONAUT_UNSTACKABLE));
    public static final RegistrySupplier<BottomlessMediaItem> BOTTOMLESS_MEDIA_ITEM = ITEMS.register("endless_phial", () -> new BottomlessMediaItem(ONEIRONAUT_UNSTACKABLE));
    public static final RegistrySupplier<BlockItem> SUPER_BUDDING_ITEM = ITEMS.register("super_budding", () -> new BlockItem(OneironautBlockRegistry.SUPER_BUDDING.get(), ONEIRONAUT_STACKABLE64));
    public static final RegistrySupplier<ItemStaff> ECHO_STAFF = ITEMS.register("echo_staff", () -> new GeneralNoisyStaff(ONEIRONAUT_UNSTACKABLE, SoundEvents.BLOCK_SCULK_SENSOR_CLICKING, SoundEvents.BLOCK_SCULK_SHRIEKER_SHRIEK, null));
    public static final RegistrySupplier<ItemStaff> BEACON_STAFF = ITEMS.register("beacon_staff", () -> new GeneralNoisyStaff(ONEIRONAUT_UNSTACKABLE, SoundEvents.BLOCK_BEACON_ACTIVATE, SoundEvents.BLOCK_BEACON_DEACTIVATE, null));
    public static final RegistrySupplier<ShovelItem> SPOON_STAFF = ITEMS.register("spoon_staff", () -> new ShovelItem(ToolMaterials.IRON, 1.5F, -3.0F, ONEIRONAUT_UNSTACKABLE.maxDamage(1024)));
    public static final RegistrySupplier<BlockItem> SENTINEL_TRAP_ITEM = ITEMS.register("sentinel_trap", () -> new BlockItem(OneironautBlockRegistry.SENTINEL_TRAP.get(), ONEIRONAUT_STACKABLE64));
    public static final RegistrySupplier<BlockItem> SENTINEL_SENSOR_ITEM = ITEMS.register("sentinel_sensor", () -> new BlockItem(OneironautBlockRegistry.SENTINEL_SENSOR.get(), ONEIRONAUT_STACKABLE64));
    public static final RegistrySupplier<BlockItem> RAYCAST_BLOCKER_ITEM = ITEMS.register("raycast_blocker", () -> new BlockItem(OneironautBlockRegistry.RAYCAST_BLOCKER.get(), ONEIRONAUT_STACKABLE64));
    public static final RegistrySupplier<BlockItem> RAYCAST_BLOCKER_GLASS_ITEM = ITEMS.register("raycast_blocker_glass", () -> new BlockItem(OneironautBlockRegistry.RAYCAST_BLOCKER_GLASS.get(), ONEIRONAUT_STACKABLE64));
    public static final RegistrySupplier<BlockItem> HEX_RESISTANT_BLOCK_ITEM = ITEMS.register("hex_resistant_block", () -> new BlockItem(OneironautBlockRegistry.HEX_RESISTANT_BLOCK.get(), ONEIRONAUT_STACKABLE64));
    public static final RegistrySupplier<BlockItem> CIRCLE_ITEM = ITEMS.register("circle", () -> new BlockItem(OneironautBlockRegistry.CIRCLE.get(), new Item.Settings().fireproof().rarity(Rarity.EPIC)));
    public static final RegistrySupplier<GeneralPigmentItem> PIGMENT_NOOSPHERE = ITEMS.register("pigment_noosphere", () -> new GeneralPigmentItem(ONEIRONAUT_STACKABLE64, GeneralPigmentItem.colors_noosphere));
    public static final RegistrySupplier<GeneralPigmentItem> PIGMENT_FLAME = ITEMS.register("pigment_flame", () -> new GeneralPigmentItem(ONEIRONAUT_STACKABLE64, GeneralPigmentItem.colors_flame));
    public static final RegistrySupplier<GeneralPigmentItem> PIGMENT_ECHO = ITEMS.register("pigment_echo", () -> new GeneralPigmentItem(ONEIRONAUT_STACKABLE64, GeneralPigmentItem.colors_echo));
    public static final RegistrySupplier<MemoryFragmentItem> MEMORY_FRAGMENT = ITEMS.register("memory_fragment", () -> new MemoryFragmentItem(ONEIRONAUT_UNSTACKABLE.rarity(Rarity.RARE)));
    public static final RegistrySupplier<BlockItem> MEDIA_ICE_ITEM = ITEMS.register("media_ice", () -> new BlockItem(OneironautBlockRegistry.MEDIA_ICE.get(), ONEIRONAUT_STACKABLE64));
    public static final RegistrySupplier<BlockItem> MEDIA_GEL_ITEM = ITEMS.register("media_gel", () -> new BlockItem(OneironautBlockRegistry.MEDIA_GEL.get(), ONEIRONAUT_STACKABLE64));
    public static final RegistrySupplier<BlockItem> CELL_ITEM = ITEMS.register("cell", () -> new BlockItem(OneironautBlockRegistry.CELL.get(), ONEIRONAUT_STACKABLE64));

}
