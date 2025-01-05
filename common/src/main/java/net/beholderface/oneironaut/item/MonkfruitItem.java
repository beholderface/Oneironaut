package net.beholderface.oneironaut.item;

import at.petrak.hexcasting.api.item.MediaHolderItem;
import at.petrak.hexcasting.api.misc.MediaConstants;
import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class MonkfruitItem extends AliasedBlockItem {
    public MonkfruitItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        double overallReleased = (world.random.nextDouble() * 4) + 1;
        List<ItemStack> mediaHolders = new ArrayList<>();
        if (user instanceof PlayerEntity player){
            for (ItemStack checkedStack : player.getInventory().main){
                if (checkedStack.getItem() instanceof MediaHolderItem battery){
                    if (battery.canRecharge(checkedStack)){
                        mediaHolders.add(checkedStack);
                    }
                }
            }
            if (player.getStackInHand(Hand.OFF_HAND).getItem() instanceof MediaHolderItem battery){
                if (battery.canRecharge(player.getStackInHand(Hand.OFF_HAND))){
                    mediaHolders.add(player.getStackInHand(Hand.OFF_HAND));
                }
            }
            for (ItemStack battery : mediaHolders){
                MediaHolderItem type = (MediaHolderItem) battery.getItem();
                double inserted = world.random.nextDouble() * overallReleased;
                overallReleased -= inserted;
                type.insertMedia(battery, (int) (inserted * MediaConstants.DUST_UNIT), false);
            }
        }
        return user.eatFood(world, stack);
    }
}
