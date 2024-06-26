package net.beholderface.oneironaut.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.feature.FeatureConfig;

public record BlockBlobConfig(Identifier mainBlockID, int rarity, int size, int squish, int falloff, int immersion) implements FeatureConfig {
    public static Codec<BlockBlobConfig> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Identifier.CODEC.fieldOf("mainblockid").forGetter(BlockBlobConfig::mainBlockID),
                    Codecs.POSITIVE_INT.fieldOf("rarity").forGetter(BlockBlobConfig::rarity),
                    Codecs.POSITIVE_INT.fieldOf("size").forGetter(BlockBlobConfig::size),
                    Codecs.POSITIVE_INT.fieldOf("squish").forGetter(BlockBlobConfig::squish),
                    Codecs.POSITIVE_INT.fieldOf("falloff").forGetter(BlockBlobConfig::falloff),
                    Codecs.POSITIVE_INT.fieldOf("immersion").forGetter(BlockBlobConfig::immersion)
            ).apply(instance, BlockBlobConfig::new));
}
