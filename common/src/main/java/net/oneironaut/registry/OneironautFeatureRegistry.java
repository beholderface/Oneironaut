package net.oneironaut.registry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.oneironaut.Oneironaut;
import net.oneironaut.feature.*;

import java.util.List;
import java.util.function.Predicate;

public class OneironautFeatureRegistry {
    public static void init(){
        Predicate<BiomeSelectionContext> nonVanillaDimensions = BiomeSelectors.foundInOverworld().and(BiomeSelectors.foundInTheEnd().and(BiomeSelectors.foundInTheNether())).negate();
        //islands
        Registry.register(Registry.FEATURE, NOOSPHERE_SEA_ISLAND_ID, NOOSPHERE_SEA_ISLAND);
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, NOOSPHERE_SEA_ISLAND_SMALL_ID, NOOSPHERE_SEA_ISLAND_SMALL);
        Registry.register(BuiltinRegistries.PLACED_FEATURE, NOOSPHERE_SEA_ISLAND_SMALL_ID, NOOSPHERE_SEA_ISLAND_SMALL_PLACED);
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, NOOSPHERE_SEA_ISLAND_MEDIUM_ID, NOOSPHERE_SEA_ISLAND_MEDIUM);
        Registry.register(BuiltinRegistries.PLACED_FEATURE, NOOSPHERE_SEA_ISLAND_MEDIUM_ID, NOOSPHERE_SEA_ISLAND_MEDIUM_PLACED);
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, NOOSPHERE_SEA_ISLAND_LARGE_ID, NOOSPHERE_SEA_ISLAND_LARGE);
        Registry.register(BuiltinRegistries.PLACED_FEATURE, NOOSPHERE_SEA_ISLAND_LARGE_ID, NOOSPHERE_SEA_ISLAND_LARGE_PLACED);
        BiomeModifications.addFeature(
                nonVanillaDimensions,
                GenerationStep.Feature.RAW_GENERATION,
                RegistryKey.of(Registry.PLACED_FEATURE_KEY, NOOSPHERE_SEA_ISLAND_SMALL_ID)
        );
        BiomeModifications.addFeature(
                nonVanillaDimensions,
                GenerationStep.Feature.RAW_GENERATION,
                RegistryKey.of(Registry.PLACED_FEATURE_KEY, NOOSPHERE_SEA_ISLAND_MEDIUM_ID)
        );
        BiomeModifications.addFeature(
                nonVanillaDimensions,
                GenerationStep.Feature.RAW_GENERATION,
                RegistryKey.of(Registry.PLACED_FEATURE_KEY, NOOSPHERE_SEA_ISLAND_LARGE_ID)
        );
        //volcano
        Registry.register(Registry.FEATURE, NOOSPHERE_SEA_VOLCANO_ID, NOOSPHERE_SEA_VOLCANO);
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, NOOSPHERE_SEA_VOLCANO_ID, NOOSPHERE_SEA_VOLCANO_CONFIGURED);
        Registry.register(BuiltinRegistries.PLACED_FEATURE, NOOSPHERE_SEA_VOLCANO_ID, NOOSPHERE_SEA_VOLCANO_PLACED);
        BiomeModifications.addFeature(
                nonVanillaDimensions,
                GenerationStep.Feature.RAW_GENERATION,
                RegistryKey.of(Registry.PLACED_FEATURE_KEY, NOOSPHERE_SEA_VOLCANO_ID)
        );
        //pseudoamethyst veins
        Registry.register(Registry.FEATURE, BLOCK_VEIN_ID, BLOCK_VEIN);
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, PSEUDOAMETHYST_VEIN_ID, PSEUDOAMETHYST_VEIN);
        Registry.register(BuiltinRegistries.PLACED_FEATURE, PSEUDOAMETHYST_VEIN_ID, PSEUDOAMETHYST_VEIN_PLACED);
        BiomeModifications.addFeature(
                nonVanillaDimensions,
                GenerationStep.Feature.UNDERGROUND_ORES,
                RegistryKey.of(Registry.PLACED_FEATURE_KEY, PSEUDOAMETHYST_VEIN_ID)
        );
    }
    //islands
    public static final Identifier NOOSPHERE_SEA_ISLAND_ID = new Identifier(Oneironaut.MOD_ID, "noosphere_sea_island");
    public static Feature<NoosphereSeaIslandConfig> NOOSPHERE_SEA_ISLAND = new NoosphereSeaIsland(NoosphereSeaIslandConfig.CODEC);
    //small
    public static final Identifier NOOSPHERE_SEA_ISLAND_SMALL_ID = new Identifier(Oneironaut.MOD_ID, "noosphere_sea_island_small");
    public static ConfiguredFeature<NoosphereSeaIslandConfig, NoosphereSeaIsland> NOOSPHERE_SEA_ISLAND_SMALL = new ConfiguredFeature<NoosphereSeaIslandConfig, NoosphereSeaIsland>(
            (NoosphereSeaIsland) NOOSPHERE_SEA_ISLAND,
            new NoosphereSeaIslandConfig(7, new Identifier(Oneironaut.MOD_ID, "noosphere_basalt")));
    public static PlacedFeature NOOSPHERE_SEA_ISLAND_SMALL_PLACED = new PlacedFeature(
            RegistryEntry.of(NOOSPHERE_SEA_ISLAND_SMALL), List.of());
    //medium
    public static final Identifier NOOSPHERE_SEA_ISLAND_MEDIUM_ID = new Identifier(Oneironaut.MOD_ID, "noosphere_sea_island_medium");
    public static ConfiguredFeature<NoosphereSeaIslandConfig, NoosphereSeaIsland> NOOSPHERE_SEA_ISLAND_MEDIUM = new ConfiguredFeature<NoosphereSeaIslandConfig, NoosphereSeaIsland>(
            (NoosphereSeaIsland) NOOSPHERE_SEA_ISLAND,
            new NoosphereSeaIslandConfig(11, new Identifier(Oneironaut.MOD_ID, "noosphere_basalt")));
    public static PlacedFeature NOOSPHERE_SEA_ISLAND_MEDIUM_PLACED = new PlacedFeature(
            RegistryEntry.of(NOOSPHERE_SEA_ISLAND_MEDIUM), List.of());
    //large
    public static final Identifier NOOSPHERE_SEA_ISLAND_LARGE_ID = new Identifier(Oneironaut.MOD_ID, "noosphere_sea_island_large");
    public static ConfiguredFeature<NoosphereSeaIslandConfig, NoosphereSeaIsland> NOOSPHERE_SEA_ISLAND_LARGE = new ConfiguredFeature<NoosphereSeaIslandConfig, NoosphereSeaIsland>(
            (NoosphereSeaIsland) NOOSPHERE_SEA_ISLAND,
            new NoosphereSeaIslandConfig(19, new Identifier(Oneironaut.MOD_ID, "noosphere_basalt")));
    public static PlacedFeature NOOSPHERE_SEA_ISLAND_LARGE_PLACED = new PlacedFeature(
            RegistryEntry.of(NOOSPHERE_SEA_ISLAND_LARGE), List.of());

    //volcano
    public static final Identifier NOOSPHERE_SEA_VOLCANO_ID = new Identifier(Oneironaut.MOD_ID, "noosphere_sea_volcano");
    public static Feature<NoosphereSeaVolcanoConfig> NOOSPHERE_SEA_VOLCANO = new NoosphereSeaVolcano(NoosphereSeaVolcanoConfig.CODEC);
    public static ConfiguredFeature<NoosphereSeaVolcanoConfig, NoosphereSeaVolcano> NOOSPHERE_SEA_VOLCANO_CONFIGURED = new ConfiguredFeature<NoosphereSeaVolcanoConfig, NoosphereSeaVolcano>(
            (NoosphereSeaVolcano) NOOSPHERE_SEA_VOLCANO,
            new NoosphereSeaVolcanoConfig(new Identifier(Oneironaut.MOD_ID, "noosphere_basalt"), new Identifier(Oneironaut.MOD_ID, "pseudoamethyst_block"))
    );
    public static PlacedFeature NOOSPHERE_SEA_VOLCANO_PLACED = new PlacedFeature(
            RegistryEntry.of(NOOSPHERE_SEA_VOLCANO_CONFIGURED), List.of());

    //block veins
    public static final Identifier BLOCK_VEIN_ID = new Identifier(Oneironaut.MOD_ID, "block_vein");
    public static Feature<BlockVeinConfig> BLOCK_VEIN = new BlockVein(BlockVeinConfig.CODEC);
    public static final Identifier PSEUDOAMETHYST_VEIN_ID = new Identifier(Oneironaut.MOD_ID, "pseudoamethyst_vein");
    //pseudoamethyst
    public static ConfiguredFeature<BlockVeinConfig, BlockVein> PSEUDOAMETHYST_VEIN = new ConfiguredFeature<>(
            (BlockVein) BLOCK_VEIN,
            new BlockVeinConfig(new Identifier(Oneironaut.MOD_ID, "pseudoamethyst_block"), new Identifier(Oneironaut.MOD_ID, "noosphere_basalt"))
    );
    public static PlacedFeature PSEUDOAMETHYST_VEIN_PLACED = new PlacedFeature(
            RegistryEntry.of(PSEUDOAMETHYST_VEIN), List.of());
}
