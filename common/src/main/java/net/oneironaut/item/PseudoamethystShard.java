package net.oneironaut.item;

import at.petrak.hexcasting.api.item.MediaHolderItem;
import at.petrak.hexcasting.api.misc.MediaConstants;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class PseudoamethystShard extends Item implements MediaHolderItem {

    public PseudoamethystShard(Settings settings){
        super(settings);
    }
    private final int PSEUDOSHARD_UNIT = (int) (MediaConstants.SHARD_UNIT * 1.5);

    @Override
    public int getMedia(ItemStack stack) {
        return PSEUDOSHARD_UNIT * stack.getCount();
    }

    @Override
    public int getMaxMedia(ItemStack stack) {
        return PSEUDOSHARD_UNIT * stack.getMaxCount();
    }

    @Override
    public void setMedia(ItemStack stack, int media) {
        stack.setCount(media / PSEUDOSHARD_UNIT);
    }

    @Override
    public boolean canProvideMedia(ItemStack stack) {
        return true;
    }

    @Override
    public boolean canRecharge(ItemStack stack) {
        return false;
    }



}
