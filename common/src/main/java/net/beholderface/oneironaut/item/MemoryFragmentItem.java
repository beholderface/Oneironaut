package net.beholderface.oneironaut.item;

import at.petrak.hexcasting.common.lib.HexSounds;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.beholderface.oneironaut.Oneironaut;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemoryFragmentItem extends Item {

    public final List<Identifier> names;

    public MemoryFragmentItem(Settings settings, List<Identifier> advancementNames) {
        super(settings);
        this.names = advancementNames;
    }

    public static final List<Identifier> NAMES_TOWER = List.of(new Identifier[]{
            new Identifier(Oneironaut.MOD_ID, "lore/treatise1"),
            new Identifier(Oneironaut.MOD_ID, "lore/treatise2"),
            new Identifier(Oneironaut.MOD_ID, "lore/treatise3"),
            new Identifier(Oneironaut.MOD_ID, "lore/treatise4"),
            new Identifier(Oneironaut.MOD_ID, "lore/science1"),
            new Identifier(Oneironaut.MOD_ID, "lore/science2"),
            new Identifier(Oneironaut.MOD_ID, "lore/science3")
    });

    public static final String CRITEREON_KEY = "grant";

    //mostly stolen from base hex lore fragment code
    @Override
    public TypedActionResult<ItemStack> use(World level, PlayerEntity player, Hand usedHand) {
        player.playSound(HexSounds.READ_LORE_FRAGMENT, 1f, 1f);
        var handStack = player.getStackInHand(usedHand);
        if (!(player instanceof ServerPlayerEntity splayer)) {
            handStack.decrement(1);
            return TypedActionResult.success(handStack);
        }
        PlayerAdvancementTracker tracker = splayer.getAdvancementTracker();
        Advancement rootAdvancement = splayer.world.getServer().getAdvancementLoader().get(new Identifier(Oneironaut.MOD_ID, "lore/root"));
        if (!tracker.getProgress(rootAdvancement).isDone()){
            tracker.grantCriterion(rootAdvancement, CRITEREON_KEY);
        }
        Advancement unfoundLore = null;
        var shuffled = new ArrayList<>(this.names);
        Collections.shuffle(shuffled);
        for (var advID : shuffled) {
            var adv = splayer.world.getServer().getAdvancementLoader().get(advID);
            if (adv == null) {
                continue; // uh oh
            }

            if (!tracker.getProgress(adv).isDone()) {
                unfoundLore = adv;
                break;
            }
        }

        if (unfoundLore == null) {
            splayer.sendMessage(Text.translatable("item.oneironaut.memory_fragment.all"), true);
            splayer.addExperience(20);
            level.playSound(null, player.getPos().x, player.getPos().y, player.getPos().z,
                    HexSounds.READ_LORE_FRAGMENT, SoundCategory.PLAYERS, 1f, 1f);
        } else {
            tracker.grantCriterion(unfoundLore, CRITEREON_KEY);
        }

        Criteria.CONSUME_ITEM.trigger(splayer, handStack);
        splayer.incrementStat(Stats.USED.getOrCreateStat(this));
        handStack.decrement(1);

        return TypedActionResult.success(handStack);
    }

    @Override
    public boolean isDamageable(){
        return false;
    }
}
