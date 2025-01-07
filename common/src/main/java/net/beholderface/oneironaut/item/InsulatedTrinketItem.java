package net.beholderface.oneironaut.item;

import at.petrak.hexcasting.common.items.magic.ItemPackagedHex;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class InsulatedTrinketItem extends ItemPackagedHex {
    public InsulatedTrinketItem(Settings pProperties) {
        super(pProperties);
    }

    @Override
    public boolean breakAfterDepletion() {
        return false;
    }

    @Override
    public int cooldown() {
        return 5;
    }

    @Override
    public boolean canDrawMediaFromInventory(ItemStack stack) {
        return false;
    }

    private static Pair<PlayerEntity, Hand> currentCaster = null;
    public static PlayerEntity getCurrentCaster(){
        return currentCaster == null ? null : currentCaster.getLeft();
    }
    public static Hand getHand(){
        return currentCaster == null ? null :currentCaster.getRight();
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand usedHand){
        try {
            currentCaster = new Pair<>(player, usedHand);
            TypedActionResult<ItemStack> output = super.use(world, player, usedHand);
            currentCaster = null;
            return output;
        } catch (Exception idk/*just in case something goes wrong for whatever reason.
        I don't want to accidentally turn off *all* GameEvent things from casting */){
            currentCaster = null;
            return TypedActionResult.fail(player.getStackInHand(usedHand));
        }
    }
}
