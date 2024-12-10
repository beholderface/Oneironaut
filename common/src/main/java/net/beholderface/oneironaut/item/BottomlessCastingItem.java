package net.beholderface.oneironaut.item;

import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.common.items.magic.ItemPackagedHex;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtList;

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
    public int getMedia(ItemStack stack) {
        return MediaConstants.DUST_UNIT / 10;
    }

    @Override
    public int getMaxMedia(ItemStack stack) {
        return Integer.MAX_VALUE;
    }

    @Override
    public void setMedia(ItemStack stack, int media) {
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
    public void writeHex(ItemStack stack, List<Iota> program, int media) {
        NbtList patsTag = new NbtList();
        for (Iota pat : program) {
            patsTag.add(HexIotaTypes.serialize(pat));
        }

        NBTHelper.putList(stack, TAG_PROGRAM, patsTag);
    }

    public int cooldown(){
        return 5;
    }
}
