package net.oneironaut.forge;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.FogShape;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.data.client.BlockStateVariantMap;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.BlockRenderView;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.oneironaut.forge.ForgeFluidInit;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class FluidRegistryContainer {
    public final RegistryObject<FluidType> type;
    public final FluidType.Properties typeProperties;
    public final RegistryObject<FluidBlock> block;
    public final RegistryObject<BucketItem> bucket;
    private ForgeFlowingFluid.Properties properties;
    public final RegistryObject<ForgeFlowingFluid.Source> source;
    public final RegistryObject<ForgeFlowingFluid.Flowing> flowing;

    public FluidRegistryContainer(String name, FluidType.Properties typeProperties,
                                  Supplier<IClientFluidTypeExtensions> clientExtensions, @Nullable AdditionalProperties additionalProperties,
                                  AbstractBlock.Settings blockProperties, Item.Settings itemProperties) {
        this.typeProperties = typeProperties;
        this.type = ForgeFluidInit.FLUID_TYPES.register(name, () -> new FluidType(this.typeProperties) {
            @Override
            public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                consumer.accept(clientExtensions.get());
            }
        });

        this.source = ForgeFluidInit.FLUIDS.register(name + "_source", () -> new ForgeFlowingFluid.Source(this.properties));
        this.flowing = ForgeFluidInit.FLUIDS.register(name + "_flowing",
                () -> new ForgeFlowingFluid.Flowing(this.properties));

        this.properties = new ForgeFlowingFluid.Properties(this.type, this.source, this.flowing);
        if (additionalProperties != null) {
            this.properties.explosionResistance(additionalProperties.explosionResistance)
                    .levelDecreasePerBlock(additionalProperties.levelDecreasePerBlock)
                    .slopeFindDistance(additionalProperties.slopeFindDistance).tickRate(additionalProperties.tickRate);
        }

        this.block = ForgeBlockInit.BLOCKS.register(name, () -> new FluidBlock(this.source, blockProperties));
        this.properties.block(this.block);

        this.bucket = ForgeItemInit.ITEMS.register(name + "_bucket", () -> new BucketItem(this.source, itemProperties));
        this.properties.bucket(this.bucket);
    }

    public FluidRegistryContainer(String name, FluidType.Properties typeProperties,
                                  Supplier<IClientFluidTypeExtensions> clientExtensions, AbstractBlock.Settings blockProperties,
                                  Item.Settings itemProperties) {
        this(name, typeProperties, clientExtensions, null, blockProperties, itemProperties);
    }

    public ForgeFlowingFluid.Properties getProperties() {
        return this.properties;
    }

    public static IClientFluidTypeExtensions createExtension(ClientExtensions extensions) {
        return new IClientFluidTypeExtensions() {
            @Override
            public Identifier getFlowingTexture() {
                return extensions.flowing;
            }

            @Nullable
            @Override
            public Identifier getOverlayTexture() {
                return extensions.overlay;
            }

            @Override
            public Identifier getRenderOverlayTexture(MinecraftClient minecraft) {
                return extensions.renderOverlay;
            }

            @Override
            public Identifier getStillTexture() {
                return extensions.still;
            }

            @Override
            public int getTintColor(FluidState state, BlockRenderView getter, BlockPos pos) {
                return extensions.tintFunction == null ? 0xFFFFFFFF : extensions.tintFunction.apply(state, getter, pos);
            }

            @Override
            public @NotNull Vec3f modifyFogColor(Camera camera, float partialTick, ClientWorld level,
                                                 int renderDistance, float darkenWorldAmount, Vec3f fluidFogColor) {
                return extensions.fogColor == null
                        ? IClientFluidTypeExtensions.super.modifyFogColor(camera, partialTick, level, renderDistance,
                        darkenWorldAmount, fluidFogColor)
                        : extensions.fogColor;
            }

            @Override
            public void modifyFogRender(Camera camera, BackgroundRenderer.FogType mode, float renderDistance, float partialTick,
                                        float nearDistance, float farDistance, FogShape shape) {
                RenderSystem.setShaderFogStart(1f);
                RenderSystem.setShaderFogEnd(6f);
            }
        };
    }

    public static class AdditionalProperties {
        private int levelDecreasePerBlock = 1;
        private float explosionResistance = 1;
        private int slopeFindDistance = 4;
        private int tickRate = 5;

        public AdditionalProperties explosionResistance(float resistance) {
            this.explosionResistance = resistance;
            return this;
        }

        public AdditionalProperties levelDecreasePerBlock(int decrease) {
            this.levelDecreasePerBlock = decrease;
            return this;
        }

        public AdditionalProperties slopeFindDistance(int distance) {
            this.slopeFindDistance = distance;
            return this;
        }

        public AdditionalProperties tickRate(int rate) {
            this.tickRate = rate;
            return this;
        }
    }

    public static class ClientExtensions {
        private Identifier still;
        private Identifier flowing;
        private Identifier overlay;
        private Identifier renderOverlay;
        private Vec3f fogColor;
        private TriFunction<FluidState, BlockRenderView, BlockPos, Integer> tintFunction;

        private final String modid;

        public ClientExtensions(String modid, String fluidName) {
            this.modid = modid;
            still(fluidName);
            flowing(fluidName);
            overlay(fluidName);
        }

        public ClientExtensions flowing(String name) {
            return flowing(name, "blocks");
        }

        public ClientExtensions flowing(String name, String folder) {
            this.flowing = new Identifier(this.modid, folder + "/" + name + "_flowing");
            return this;
        }

        public ClientExtensions fogColor(float red, float green, float blue) {
            this.fogColor = new Vec3f(red, green, blue);
            return this;
        }

        public ClientExtensions overlay(String name) {
            return overlay(name, "blocks");
        }

        public ClientExtensions overlay(String name, String folder) {
            this.overlay = new Identifier(this.modid, folder + "/" + name + "_overlay");
            return renderOverlay(new Identifier(this.modid, "textures/" + folder + "/" + name + "_overlay.png"));
        }

        public ClientExtensions renderOverlay(Identifier path) {
            this.renderOverlay = path;
            return this;
        }

        public ClientExtensions still(String name) {
            return still(name, "blocks");
        }

        public ClientExtensions still(String name, String folder) {
            this.still = new Identifier(this.modid, folder + "/" + name + "_still");
            return this;
        }

        public ClientExtensions tint(int tint) {
            this.tintFunction = ($0, $1, $2) -> tint;
            return this;
        }

        public ClientExtensions tint(TriFunction<FluidState, BlockRenderView, BlockPos, Integer> tinter) {
            this.tintFunction = tinter;
            return this;
        }
    }
}
