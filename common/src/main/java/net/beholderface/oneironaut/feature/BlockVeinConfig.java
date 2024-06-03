package net.beholderface.oneironaut.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.gen.feature.FeatureConfig;

public record BlockVeinConfig(Identifier mainBlockID, Identifier carvedBlockID) implements FeatureConfig {
    public static Codec<BlockVeinConfig> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Identifier.CODEC.fieldOf("mainBlockID").forGetter(BlockVeinConfig::mainBlockID),
                    Identifier.CODEC.fieldOf("carvedBlockID").forGetter(BlockVeinConfig::carvedBlockID))
                    .apply(instance, BlockVeinConfig::new));
}
