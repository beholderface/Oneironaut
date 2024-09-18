package net.beholderface.oneironaut.item;

import net.beholderface.oneironaut.casting.OvercastDamageEnchant;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MindScalpelItem extends Item {
    public MindScalpelItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean isDamageable() {
        return false;
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity target, Hand hand) {
        ItemCooldownManager cooldownManager = user.getItemCooldownManager();
        if (!cooldownManager.isCoolingDown(this)){
            OvercastDamageEnchant.applyMindDamage(user, target, 2, true);
            target.damage(DamageSource.player(user), 0);
            user.swingHand(hand);
            cooldownManager.set(this, 15);
            user.incrementStat(Stats.USED.getOrCreateStat(this));
            return ActionResult.CONSUME;
        } else {
            return ActionResult.PASS;
        }
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof PlayerEntity player){
            this.useOnEntity(stack, player, target, Hand.MAIN_HAND);
        }
        return false;
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return !miner.isCreative();
    }
}
