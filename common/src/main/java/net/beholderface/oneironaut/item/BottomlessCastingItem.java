package net.beholderface.oneironaut.item;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.common.items.magic.ItemPackagedHex;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtList;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

public class BottomlessCastingItem extends ItemPackagedHex {
    public BottomlessCastingItem(Settings pProperties) {
        super(pProperties);
    }

    @Override
    public boolean breakAfterDepletion() {
        return false;
    }

    @Override
    public boolean canDrawMediaFromInventory(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canRecharge(ItemStack stack) {
        return false;
    }

    @Override
    public long getMedia(ItemStack stack) {
        return MediaConstants.DUST_UNIT / 10;
    }

    @Override
    public long getMaxMedia(ItemStack stack) {
        return Integer.MAX_VALUE;
    }

    @Override
    public void setMedia(ItemStack stack, long media) {
        //no-op
    }

    @Override
    public boolean isItemBarVisible(ItemStack pStack) {
        return this.hasHex(pStack);
    }

    @Override
    public int getItemBarColor(ItemStack pStack) {
        if (MinecraftClient.getInstance() != null && MinecraftClient.getInstance().world != null){
            return Color.HSBtoRGB(((float) MinecraftClient.getInstance().world.getTime()) / 100.0f, 1f, 1f);
        }
        return 0;
    }

    @Override
    public int getItemBarStep(ItemStack pStack) {
        return 13;
    }

    @Override
    public void writeHex(ItemStack stack, List<Iota> program, @Nullable FrozenPigment pigment, long media) {
        NbtList patsTag = new NbtList();
        for (Iota pat : program) {
            patsTag.add(IotaType.serialize(pat));
        }

        NBTHelper.putList(stack, TAG_PROGRAM, patsTag);
        if (pigment != null)
            NBTHelper.putCompound(stack, TAG_PIGMENT, pigment.serializeToNBT());
    }

    public int cooldown(){
        return 5;
    }
}
