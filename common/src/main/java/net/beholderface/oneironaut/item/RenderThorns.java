package net.beholderface.oneironaut.item;

import net.beholderface.oneironaut.casting.OvercastDamageEnchant;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class RenderThorns extends Item {
    public RenderThorns(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        OvercastDamageEnchant.applyMindDamage(null, user, 2, false);
        user.damage(user.getDamageSources().sweetBerryBush(), user.isPlayer() ? 0.001f : 0f);
        user.getItemCooldownManager().set(this, 10);
        if (user instanceof ServerPlayerEntity serverPlayer){
            PlayerAdvancementTracker tracker = serverPlayer.getAdvancementTracker();
            Advancement ouchie = world.getServer().getAdvancementLoader().get(Identifier.of("oneironaut","prick_self"));
            if (!tracker.getProgress(ouchie).isDone()) {
                tracker.grantCriterion(ouchie, "grant");
            }
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
