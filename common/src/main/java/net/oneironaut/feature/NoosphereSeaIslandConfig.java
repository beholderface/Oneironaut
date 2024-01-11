package net.oneironaut.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.gen.feature.FeatureConfig;

public record NoosphereSeaIslandConfig(int size, Identifier blockID) implements FeatureConfig {
    public static Codec<NoosphereSeaIslandConfig> CODEC = RecordCodecBuilder.create(
            instance ->
                    instance.group(
                            Codecs.POSITIVE_INT.fieldOf("size").forGetter(NoosphereSeaIslandConfig::size),
                            Identifier.CODEC.fieldOf("blockID").forGetter(NoosphereSeaIslandConfig::blockID))
                    .apply(instance, NoosphereSeaIslandConfig::new));
}
