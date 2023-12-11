package net.oneironaut.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.oneironaut.Oneironaut;
import net.oneironaut.registry.OneironautThingRegistry;

public record NoosphereSeaIslandConfig(int number, Identifier blockID) implements FeatureConfig {
    public static Codec<NoosphereSeaIslandConfig> CODEC = RecordCodecBuilder.create(
            instance ->
                    instance.group(
                            Codecs.POSITIVE_INT.fieldOf("number").forGetter(NoosphereSeaIslandConfig::number),
                            Identifier.CODEC.fieldOf("blockID").forGetter(NoosphereSeaIslandConfig::blockID))
                    .apply(instance, NoosphereSeaIslandConfig::new));
    /*public static ConfiguredFeature<NoosphereSeaIslandConfig, NoosphereSeaIsland> NOOSPHERE_SEA_ISLAND_SMALL = new ConfiguredFeature<NoosphereSeaIslandConfig, NoosphereSeaIsland>(
            OneironautThingRegistry.NOOSPHERE_SEA_ISLAND,
            new NoosphereSeaIslandConfig(7, new Identifier(Oneironaut.MOD_ID, "noosphere_basalt"))
    );
    public static ConfiguredFeature<NoosphereSeaIslandConfig, NoosphereSeaIsland> NOOSPHERE_SEA_ISLAND_MEDIUM = new ConfiguredFeature<NoosphereSeaIslandConfig, NoosphereSeaIsland>(
            OneironautThingRegistry.NOOSPHERE_SEA_ISLAND,
            new NoosphereSeaIslandConfig(11, new Identifier(Oneironaut.MOD_ID, "noosphere_basalt"))
    );
    public static ConfiguredFeature<NoosphereSeaIslandConfig, NoosphereSeaIsland> NOOSPHERE_SEA_ISLAND_LARGE = new ConfiguredFeature<NoosphereSeaIslandConfig, NoosphereSeaIsland>(
            OneironautThingRegistry.NOOSPHERE_SEA_ISLAND,
            new NoosphereSeaIslandConfig(19, new Identifier(Oneironaut.MOD_ID, "noosphere_basalt"))
    );*/
}
