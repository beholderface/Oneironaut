package net.beholderface.oneironaut.registry;

import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.beholderface.oneironaut.Oneironaut;
import net.beholderface.oneironaut.feature.*;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.PlacedFeature;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class OneironautFeatureRegistry {

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Oneironaut.MOD_ID, RegistryKeys.FEATURE);
    public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIG_FEATURES = DeferredRegister.create(Oneironaut.MOD_ID, RegistryKeys.CONFIGURED_FEATURE);
    public static final DeferredRegister<PlacedFeature> PLACED_FEATURES = DeferredRegister.create(Oneironaut.MOD_ID, RegistryKeys.PLACED_FEATURE);
    public static void init(){
        FEATURES.register();
        CONFIG_FEATURES.register();
        PLACED_FEATURES.register();

        if (Platform.isFabric()){
            Predicate<BiomeSelectionContext> noosphereSeaHopefully = BiomeSelectors.vanilla().negate();
            RegistryKey<Biome> noosphere_sea = RegistryKey.of(RegistryKeys.BIOME, new Identifier("oneironaut:noosphere_sea"));
            noosphereSeaHopefully =  BiomeSelectors.includeByKey(noosphere_sea);
            BiomeModifications.addFeature(
                    noosphereSeaHopefully,
                    GenerationStep.Feature.RAW_GENERATION,
                    RegistryKey.of(NOOSPHERE_SEA_ISLAND_SMALL_PLACED.getRegistryKey(), NOOSPHERE_SEA_ISLAND_SMALL_ID)
            );
            BiomeModifications.addFeature(
                    noosphereSeaHopefully,
                    GenerationStep.Feature.RAW_GENERATION,
                    RegistryKey.of(NOOSPHERE_SEA_ISLAND_MEDIUM_PLACED.getRegistryKey(), NOOSPHERE_SEA_ISLAND_MEDIUM_ID)
            );
            BiomeModifications.addFeature(
                    noosphereSeaHopefully,
                    GenerationStep.Feature.RAW_GENERATION,
                    RegistryKey.of(NOOSPHERE_SEA_ISLAND_LARGE_PLACED.getRegistryKey(), NOOSPHERE_SEA_ISLAND_LARGE_ID)
            );
            BiomeModifications.addFeature(
                    noosphereSeaHopefully,
                    GenerationStep.Feature.RAW_GENERATION,
                    RegistryKey.of(NOOSPHERE_SEA_VOLCANO_PLACED.getRegistryKey(), NOOSPHERE_SEA_VOLCANO_ID)
            );
            BiomeModifications.addFeature(
                    noosphereSeaHopefully,
                    GenerationStep.Feature.UNDERGROUND_ORES,
                    RegistryKey.of(PSEUDOAMETHYST_VEIN_PLACED.getRegistryKey(), PSEUDOAMETHYST_VEIN_ID)
            );
            BiomeModifications.addFeature(
                    noosphereSeaHopefully,
                    GenerationStep.Feature.LOCAL_MODIFICATIONS,
                    RegistryKey.of(GEL_BLOB_PLACED.getRegistryKey(), GEL_BLOB_ID)
            );
            BiomeModifications.addFeature(
                    noosphereSeaHopefully,
                    GenerationStep.Feature.LOCAL_MODIFICATIONS,
                    RegistryKey.of(ICE_BLOB_PLACED.getRegistryKey(), ICE_BLOB_ID)
            );
            //wish this worked
            //BiomeModifications.addSpawn(noosphereSeaHopefully, SpawnGroup.MISC, HexalEntities.WANDERING_WISP, 10, 1, 8);
        } else {
            Oneironaut.LOGGER.info("phooey");
        }
    }
    //islands
    public static final RegistrySupplier<Feature<NoosphereSeaIslandConfig>> NOOSPHERE_SEA_ISLAND = FEATURES.register("noosphere_sea_island", () -> new NoosphereSeaIsland(NoosphereSeaIslandConfig.CODEC));
    //small
    public static final Identifier NOOSPHERE_SEA_ISLAND_SMALL_ID = new Identifier(Oneironaut.MOD_ID, "noosphere_sea_island_small");
    public static final RegistrySupplier<ConfiguredFeature<NoosphereSeaIslandConfig, NoosphereSeaIsland>> NOOSPHERE_SEA_ISLAND_SMALL = CONFIG_FEATURES.register("noosphere_sea_island_small", () -> new ConfiguredFeature<NoosphereSeaIslandConfig, NoosphereSeaIsland>(
            (NoosphereSeaIsland) NOOSPHERE_SEA_ISLAND.get(),
            new NoosphereSeaIslandConfig(7, new Identifier(Oneironaut.MOD_ID, "noosphere_basalt"))));
    public static final RegistrySupplier<PlacedFeature>  NOOSPHERE_SEA_ISLAND_SMALL_PLACED = PLACED_FEATURES.register("noosphere_sea_island_small", () ->new PlacedFeature(
            RegistryEntry.of(NOOSPHERE_SEA_ISLAND_SMALL.get()), List.of()));
    //medium
    public static final Identifier NOOSPHERE_SEA_ISLAND_MEDIUM_ID = new Identifier(Oneironaut.MOD_ID, "noosphere_sea_island_medium");
    public static final RegistrySupplier<ConfiguredFeature<NoosphereSeaIslandConfig, NoosphereSeaIsland>> NOOSPHERE_SEA_ISLAND_MEDIUM = CONFIG_FEATURES.register("noosphere_sea_island_medium", () -> new ConfiguredFeature<NoosphereSeaIslandConfig, NoosphereSeaIsland>(
            (NoosphereSeaIsland) NOOSPHERE_SEA_ISLAND.get(),
            new NoosphereSeaIslandConfig(11, new Identifier(Oneironaut.MOD_ID, "noosphere_basalt"))));
    public static final RegistrySupplier<PlacedFeature>  NOOSPHERE_SEA_ISLAND_MEDIUM_PLACED = PLACED_FEATURES.register("noosphere_sea_island_medium", () ->new PlacedFeature(
            RegistryEntry.of(NOOSPHERE_SEA_ISLAND_MEDIUM.get()), List.of()));
    //large
    public static final Identifier NOOSPHERE_SEA_ISLAND_LARGE_ID = new Identifier(Oneironaut.MOD_ID, "noosphere_sea_island_large");
    public static final RegistrySupplier<ConfiguredFeature<NoosphereSeaIslandConfig, NoosphereSeaIsland>> NOOSPHERE_SEA_ISLAND_LARGE = CONFIG_FEATURES.register("noosphere_sea_island_large", () -> new ConfiguredFeature<NoosphereSeaIslandConfig, NoosphereSeaIsland>(
            (NoosphereSeaIsland) NOOSPHERE_SEA_ISLAND.get(),
            new NoosphereSeaIslandConfig(19, new Identifier(Oneironaut.MOD_ID, "noosphere_basalt"))));
    public static final RegistrySupplier<PlacedFeature>  NOOSPHERE_SEA_ISLAND_LARGE_PLACED = PLACED_FEATURES.register("noosphere_sea_island_large", () ->new PlacedFeature(
            RegistryEntry.of(NOOSPHERE_SEA_ISLAND_LARGE.get()), List.of()));

    //volcano
    public static final Identifier NOOSPHERE_SEA_VOLCANO_ID = new Identifier(Oneironaut.MOD_ID, "noosphere_sea_volcano");
    //public static Feature<NoosphereSeaVolcanoConfig> NOOSPHERE_SEA_VOLCANO = new NoosphereSeaVolcano(NoosphereSeaVolcanoConfig.CODEC);
    public static final RegistrySupplier<Feature<NoosphereSeaVolcanoConfig>> NOOSPHERE_SEA_VOLCANO = FEATURES.register("noosphere_sea_volcano", () -> new NoosphereSeaVolcano(NoosphereSeaVolcanoConfig.CODEC));

    public static final RegistrySupplier<ConfiguredFeature<NoosphereSeaVolcanoConfig, NoosphereSeaVolcano>> NOOSPHERE_SEA_VOLCANO_CONFIGURED = CONFIG_FEATURES.register("noosphere_sea_volcano", () -> new ConfiguredFeature<NoosphereSeaVolcanoConfig, NoosphereSeaVolcano>(
            (NoosphereSeaVolcano) NOOSPHERE_SEA_VOLCANO.get(),
            new NoosphereSeaVolcanoConfig(new Identifier(Oneironaut.MOD_ID, "noosphere_basalt"), new Identifier(Oneironaut.MOD_ID, "pseudoamethyst_block"))));
    public static final RegistrySupplier<PlacedFeature>  NOOSPHERE_SEA_VOLCANO_PLACED = PLACED_FEATURES.register("noosphere_sea_volcano", () ->new PlacedFeature(
            RegistryEntry.of(NOOSPHERE_SEA_VOLCANO_CONFIGURED.get()), List.of()));
    /*public static ConfiguredFeature<NoosphereSeaVolcanoConfig, NoosphereSeaVolcano> NOOSPHERE_SEA_VOLCANO_CONFIGURED = new ConfiguredFeature<NoosphereSeaVolcanoConfig, NoosphereSeaVolcano>(
            (NoosphereSeaVolcano) NOOSPHERE_SEA_VOLCANO,
            new NoosphereSeaVolcanoConfig(new Identifier(Oneironaut.MOD_ID, "noosphere_basalt"), new Identifier(Oneironaut.MOD_ID, "pseudoamethyst_block"))
    );
    public static PlacedFeature NOOSPHERE_SEA_VOLCANO_PLACED = new PlacedFeature(
            RegistryEntry.of(NOOSPHERE_SEA_VOLCANO_CONFIGURED), List.of());*/

    //block veins
    public static final Identifier BLOCK_VEIN_ID = new Identifier(Oneironaut.MOD_ID, "block_vein");
    //public static Feature<BlockVeinConfig> BLOCK_VEIN = new BlockVein(BlockVeinConfig.CODEC);
    public static final RegistrySupplier<Feature<BlockVeinConfig>> BLOCK_VEIN = FEATURES.register("block_vein", () -> new BlockVein(BlockVeinConfig.CODEC));

    public static final RegistrySupplier<ConfiguredFeature<BlockVeinConfig, BlockVein>> PSEUDOAMETHYST_VEIN = CONFIG_FEATURES.register("pseudoamethyst_vein", () -> new ConfiguredFeature<BlockVeinConfig, BlockVein>(
            (BlockVein) BLOCK_VEIN.get(),
            new BlockVeinConfig(new Identifier(Oneironaut.MOD_ID, "pseudoamethyst_block"), new Identifier(Oneironaut.MOD_ID, "noosphere_basalt"))));
    public static final RegistrySupplier<PlacedFeature>  PSEUDOAMETHYST_VEIN_PLACED = PLACED_FEATURES.register("pseudoamethyst_vein", () ->new PlacedFeature(
            RegistryEntry.of(PSEUDOAMETHYST_VEIN.get()), List.of()));

    public static final Identifier PSEUDOAMETHYST_VEIN_ID = new Identifier(Oneironaut.MOD_ID, "pseudoamethyst_vein");
    //pseudoamethyst
    /*public static ConfiguredFeature<BlockVeinConfig, BlockVein> PSEUDOAMETHYST_VEIN = new ConfiguredFeature<>(
            (BlockVein) BLOCK_VEIN,
            new BlockVeinConfig(new Identifier(Oneironaut.MOD_ID, "pseudoamethyst_block"), new Identifier(Oneironaut.MOD_ID, "noosphere_basalt"))
    );*/
    /*public static PlacedFeature PSEUDOAMETHYST_VEIN_PLACED = new PlacedFeature(
            RegistryEntry.of(PSEUDOAMETHYST_VEIN), List.of());*/

    public static final RegistrySupplier<Feature<BlockBlobConfig>> BLOCK_BLOB = FEATURES.register("block_blob", () -> new BlockBlob(BlockBlobConfig.CODEC));

    public static final Identifier GEL_BLOB_ID = new Identifier(Oneironaut.MOD_ID, "gel_blob");
    public static final RegistrySupplier<ConfiguredFeature<BlockBlobConfig, BlockBlob>> GEL_BLOB = CONFIG_FEATURES.register("gel_blob", () -> new ConfiguredFeature<BlockBlobConfig, BlockBlob>(
            (BlockBlob) BLOCK_BLOB.get(),
            new BlockBlobConfig(new Identifier(Oneironaut.MOD_ID, "media_gel"), 250, 6, 2, 5, 1)));
    public static final RegistrySupplier<PlacedFeature>  GEL_BLOB_PLACED = PLACED_FEATURES.register("gel_blob", () ->new PlacedFeature(
            RegistryEntry.of(GEL_BLOB.get()), List.of()));

    public static final Identifier ICE_BLOB_ID = new Identifier(Oneironaut.MOD_ID, "ice_blob");
    public static final RegistrySupplier<ConfiguredFeature<BlockBlobConfig, BlockBlob>> ICE_BLOB = CONFIG_FEATURES.register("ice_blob", () -> new ConfiguredFeature<BlockBlobConfig, BlockBlob>(
            (BlockBlob) BLOCK_BLOB.get(),
            new BlockBlobConfig(new Identifier(Oneironaut.MOD_ID, "media_ice"), 150, 8, 1, 5, 8)));
    public static final RegistrySupplier<PlacedFeature>  ICE_BLOB_PLACED = PLACED_FEATURES.register("ice_blob", () ->new PlacedFeature(
            RegistryEntry.of(ICE_BLOB.get()), List.of()));

}
