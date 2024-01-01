package net.oneironaut.registry;

import at.petrak.hexcasting.common.items.ItemStaff;
import at.petrak.hexcasting.common.lib.HexBlocks;
import at.petrak.hexcasting.common.lib.HexItems;
import dev.architectury.core.item.ArchitecturyBucketItem;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BlockItem;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Rarity;
import net.oneironaut.Oneironaut;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;
import net.oneironaut.block.*;
import net.oneironaut.casting.DetectionResistEffect;
import net.oneironaut.casting.MissingEffect;
import net.oneironaut.item.BottomlessMediaItem;
import net.oneironaut.item.EchoStaff;
import net.oneironaut.item.ReverberationRod;
import net.oneironaut.item.PseudoamethystShard;

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

    public static final RegistrySupplier<BlockItem> PSUEDOAMETHYST_BLOCK_ITEM = ITEMS.register("pseudoamethyst_block", () -> new BlockItem(OneironautBlockRegistry.PSUEDOAMETHYST_BLOCK.get(), HexItems.props()));
    public static final RegistrySupplier<BlockItem> NOOSPHERE_BASALT_ITEM = ITEMS.register("noosphere_basalt", () -> new BlockItem(OneironautBlockRegistry.NOOSPHERE_BASALT.get(), HexItems.props()));
    public static final RegistrySupplier<BlockItem> WISP_LANTERN_ITEM = ITEMS.register("wisp_lantern", () -> new BlockItem(OneironautBlockRegistry.WISP_LANTERN.get(), HexItems.props()));
    public static final RegistrySupplier<BlockItem> WISP_LANTERN_TINTED_ITEM = ITEMS.register("wisp_lantern_tinted", () -> new BlockItem(OneironautBlockRegistry.WISP_LANTERN_TINTED.get(), HexItems.props()));
    public static final RegistrySupplier<Item> PSUEDOAMETHYST_SHARD = ITEMS.register("pseudoamethyst_shard", () -> new PseudoamethystShard(HexItems.props()));
    public static final RegistrySupplier<ArchitecturyBucketItem> THOUGHT_SLURRY_BUCKET = ITEMS.register("thought_slurry_bucket", () -> new ArchitecturyBucketItem(OneironautMiscRegistry.THOUGHT_SLURRY, HexItems.unstackable()));
    public static final RegistrySupplier<ReverberationRod> REVERBERATION_ROD = ITEMS.register("reverberation_rod", () -> new ReverberationRod(HexItems.unstackable()));
    public static final RegistrySupplier<BottomlessMediaItem> BOTTOMLESS_MEDIA_ITEM = ITEMS.register("endless_phial", () -> new BottomlessMediaItem(HexItems.unstackable()));
    public static final RegistrySupplier<BlockItem> SUPER_BUDDING_ITEM = ITEMS.register("super_budding", () -> new BlockItem(OneironautBlockRegistry.SUPER_BUDDING.get(), HexItems.props()));
    public static final RegistrySupplier<ItemStaff> ECHO_STAFF = ITEMS.register("echo_staff", () -> new EchoStaff(HexItems.unstackable()));
    public static final RegistrySupplier<ItemStaff> SPOON_STAFF = ITEMS.register("spoon_staff", () -> new ItemStaff(HexItems.unstackable()));
    public static final RegistrySupplier<BlockItem> SENTINEL_TRAP_ITEM = ITEMS.register("sentinel_trap", () -> new BlockItem(OneironautBlockRegistry.SENTINEL_TRAP.get(), HexItems.props()));
    public static final RegistrySupplier<BlockItem> SENTINEL_SENSOR_ITEM = ITEMS.register("sentinel_sensor", () -> new BlockItem(OneironautBlockRegistry.SENTINEL_SENSOR.get(), HexItems.props()));
    public static final RegistrySupplier<BlockItem> RAYCAST_BLOCKER_ITEM = ITEMS.register("raycast_blocker", () -> new BlockItem(OneironautBlockRegistry.RAYCAST_BLOCKER.get(), HexItems.props()));
    public static final RegistrySupplier<BlockItem> RAYCAST_BLOCKER_GLASS_ITEM = ITEMS.register("raycast_blocker_glass", () -> new BlockItem(OneironautBlockRegistry.RAYCAST_BLOCKER_GLASS.get(), HexItems.props()));
    public static final RegistrySupplier<BlockItem> CIRCLE_ITEM = ITEMS.register("circle", () -> new BlockItem(OneironautBlockRegistry.CIRCLE.get(), new Item.Settings().fireproof().rarity(Rarity.EPIC)));

    // A new creative tab. Notice how it is one of the few things that are not deferred
    //public static final ItemGroup DUMMY_GROUP = CreativeTabRegistry.create(id("dummy_group"), () -> new ItemStack(OneironautItemRegistry.DUMMY_ITEM.get()));



}
