package net.oneironaut.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.gen.feature.FeatureConfig;

public record NoosphereSeaVolcanoConfig(Identifier mainBlockID, Identifier secondaryBlockID) implements FeatureConfig {
    public static Codec<NoosphereSeaVolcanoConfig> CODEC = RecordCodecBuilder.create(
            instance ->
                    instance.group(
                            Identifier.CODEC.fieldOf("mainBlockID").forGetter(NoosphereSeaVolcanoConfig::mainBlockID),
                            Identifier.CODEC.fieldOf("secondaryBlockID").forGetter(NoosphereSeaVolcanoConfig::secondaryBlockID))
                    .apply(instance, NoosphereSeaVolcanoConfig::new));
}
