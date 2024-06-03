package net.beholderface.oneironaut.item;

import at.petrak.hexcasting.api.addldata.ADMediaHolder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.HashSet;
import java.util.Set;


//stolen because I copied this whole thing from gloop
public class ItemStolenMediaProvider extends Item {
    protected int mediaAmt;
    private boolean grabFromInventory;
    private int priority;

    public static final Set<ItemStolenMediaProvider> allStolenMediaItems = new HashSet<>();

    public ItemStolenMediaProvider(Item.Settings settings, int mediaAmt, boolean grabFromInventory, int priority){
        super(settings);
        this.mediaAmt = mediaAmt;
        this.grabFromInventory = grabFromInventory;
        this.priority = priority;
        allStolenMediaItems.add(this);
    }

    public ItemStolenMediaProvider(Item.Settings settings, int mediaAmt){
        this(settings, mediaAmt, true, ADMediaHolder.CHARGED_AMETHYST_PRIORITY);
    }

    public ItemStolenMediaProvider(Item.Settings settings, int mediaAmt, int priority){
        this(settings, mediaAmt, true, priority);
    }

    public boolean shouldGrabFromInventory(ItemStack stack){
        return grabFromInventory;
    }

    public int getMediaAmount(){
        return mediaAmt;
    }

    public int getPriority(){
        return priority;
    }

    public int getMedia(ItemStack stack){
        return mediaAmt * stack.getCount();
    }

    public int getMaxMedia(ItemStack stack){
        return mediaAmt * stack.getMaxCount();
    }

    public void setMedia(ItemStack stack, int media){
        // no
    }

    public boolean canProvideMedia(ItemStack stack){
        return true;
    }

    public boolean canRecharge(ItemStack stack){
        return false;
    }

    public boolean shouldUseOwnWithdrawLogic(ItemStack stack){
        return false;
    }

    public int withdrawMedia(ItemStack stack, int cost, boolean simulate) {
        int mediaHere = getMedia(stack);
        if (cost < 0) {
            cost = mediaHere;
        }
        int realCost = Math.min(cost, mediaHere);
        if (!simulate) {
            stack.decrement((int) Math.ceil(realCost / (double)mediaAmt));
        }
        return realCost;
    }

    public int insertMedia(ItemStack stack, int amount, boolean simulate) {
        return 0; // no don't do that
    }

    public InstancedProvider getProvider(ItemStack stack){
        return new InstancedProvider(stack, this);
    }

    public class InstancedProvider implements ADMediaHolder{
        protected ItemStack innerStack;
        protected ItemStolenMediaProvider providerItem;

        public InstancedProvider(ItemStack stack, ItemStolenMediaProvider providerItem){
            this.innerStack = stack;
            this.providerItem = providerItem;
        }

        @Override
        public int getMedia() {
            return providerItem.getMedia(innerStack);
        }

        @Override
        public int getMaxMedia() {
            return getMedia();
        }

        @Override
        public void setMedia(int media) {
            providerItem.setMedia(innerStack, media);
        }

        @Override
        public boolean canRecharge() {
            return providerItem.canRecharge(innerStack);
        }

        @Override
        public boolean canProvide() {
            return providerItem.canProvideMedia(innerStack);
        }

        @Override
        public int getConsumptionPriority() {
            return providerItem.getPriority();
        }

        @Override
        public boolean canConstructBattery() {
            return true;
        }

        @Override
        public int withdrawMedia(int cost, boolean simulate) {
            return providerItem.withdrawMedia(innerStack, cost, simulate);
        }
    }
}

