package net.oneironaut.forge;

import at.petrak.hexcasting.common.lib.HexItems;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.oneironaut.Oneironaut;

public class ForgeFluidInit {
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, Oneironaut.MOD_ID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, Oneironaut.MOD_ID);
    public static final FluidRegistryContainer THOUGHT_SLURRY = new FluidRegistryContainer("thought_slurry",
            FluidType.Properties.create().canPushEntity(true).canDrown(true).canConvertToSource(true).supportsBoating(true).canSwim(true),
            () -> FluidRegistryContainer.createExtension(new FluidRegistryContainer.ClientExtensions(Oneironaut.MOD_ID, "thought_slurry").fogColor(49f,18.8f,66.7f)),
            AbstractBlock.Settings.copy(Blocks.WATER), new Item.Settings().group(ItemGroup.MISC).maxCount(1));
}
