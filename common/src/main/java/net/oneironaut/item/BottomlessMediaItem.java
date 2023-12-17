package net.oneironaut.item;

import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.common.items.magic.ItemMediaHolder;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BottomlessMediaItem extends ItemMediaHolder {

    public BottomlessMediaItem(Settings settings){
        super(settings);
    }
    @Override
    public boolean isItemBarVisible(ItemStack pStack) {
        return false;
    }

    @Override
    public void appendTooltip(ItemStack pStack, @Nullable World pLevel, List<Text> pTooltipComponents, TooltipContext pIsAdvanced) {
        //do nothing
    }

    private double arbitraryLog(double base, double num){
        return Math.log(num) / Math.log(base);
    }

    private Map<PlayerEntity, NbtCompound> playerPhialCounts = new HashMap<>();
    private long time;

    private int logMedia(ItemStack stack){
        NbtCompound nbt = stack.getOrCreateNbt();
        int media = 0;
        if (time != nbt.getLong("latestTime")){
            return media;
        } else {
            int foundItems = nbt.getInt("foundPhials");
            if (foundItems == 1){
                media = MediaConstants.DUST_UNIT / 10;
            } else {
                media = (int) (((arbitraryLog(6.0, foundItems) + 0.75) / foundItems) * (MediaConstants.DUST_UNIT / 10));
            }
        }
        //int media = foundItems > 0 ? (int) (((arbitraryLog(6.0, foundItems) + 0.75) / foundItems) * (MediaConstants.DUST_UNIT / 10)) : 0;
        //Oneironaut.LOGGER.info("Media in each of the "+ foundItems + " endless phials in inventory: "+media);
        return media;
    }
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient){
            time = world.getTime();
            if (entity.isPlayer()){
                PlayerEntity player = (PlayerEntity) entity;
                NbtCompound nbt = new NbtCompound();
                if (playerPhialCounts.containsKey(player)){
                    NbtCompound mapNBT = playerPhialCounts.get(player);
                    if (mapNBT.getLong("latestTime") == time){
                        int phials = mapNBT.getInt("foundPhials") + 1;
                        mapNBT.putInt("foundPhials", phials);
                        stack.getOrCreateNbt().putInt("foundPhials", phials);
                        playerPhialCounts.put(player, nbt);
                    } else {
                        nbt.putLong("latestTime", time);
                        nbt.putInt("foundPhials", 1);
                        stack.getOrCreateNbt().putInt("foundPhials", 1);
                        stack.getOrCreateNbt().putLong("latestTime", time);
                        playerPhialCounts.put(player, nbt);
                    }
                } else {
                    nbt.putLong("latestTime", time);
                    nbt.putInt("foundPhials", 1);
                    stack.getOrCreateNbt().putInt("foundPhials", 1);
                    stack.getOrCreateNbt().putLong("latestTime", time);
                    playerPhialCounts.put(player, nbt);
                }
                /*Iterator<ItemStack> invIterator = player.getInventory().main.iterator();
                int count = 0;
                boolean foundSelf = false;
                ItemStack current;
                while(invIterator.hasNext()){
                    current = invIterator.next();
                    if (current == stack){
                        foundSelf = true;
                    }
                    if (current.getItem().equals(stack.getItem())){
                        count++;
                    }
                }
                current = player.getStackInHand(Hand.OFF_HAND);
                if (current == stack){
                    foundSelf = true;
                }
                if (current.getItem().equals(stack.getItem())){
                    count++;
                }
                if (!foundSelf){
                    count = 0;
                }
                stack.getOrCreateNbt().putInt("countInInventory", count);*/
            }
        }
    }
    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
        stack.getOrCreateNbt().putInt("foundPhials", 1);
        stack.getOrCreateNbt().putLong("latestTime", world.getTime());
    }

    @Override
    public int getMedia(ItemStack stack) {
        //Oneironaut.LOGGER.info(stack.getHolder());
        return logMedia(stack);
    }

    @Override
    public int getMaxMedia(ItemStack stack) {
        return logMedia(stack);
    }

    @Override
    public void setMedia(ItemStack stack, int media) {
        //stack.setCount(media / PSEUDOSHARD_UNIT);
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
