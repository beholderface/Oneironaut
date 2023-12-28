package net.oneironaut.item;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.casting.CastingHarness;
import at.petrak.hexcasting.api.spell.casting.ResolvedPatternType;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.utils.HexUtils;
import at.petrak.hexcasting.common.items.magic.ItemPackagedHex;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

import java.util.List;

public class ReverberationRod extends ItemPackagedHex  {
    public ReverberationRod(Settings settings){
        super(settings);
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
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BLOCK;
    }


    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand usedHand) {
        var stack = player.getStackInHand(usedHand);
        if (!hasHex(stack)) {
            return TypedActionResult.fail(stack);
        }
        Stat<?> stat = Stats.USED.getOrCreateStat(this);
        player.incrementStat(stat);
        if (!world.isClient){
            var sPlayer = world.getPlayerByUuid(player.getUuid()).getServer().getPlayerManager().getPlayer(player.getUuid());
            assert sPlayer != null;
            assert stack.getNbt() != null;
            stack.getNbt().putLongArray("initialPos", HexUtils.serializeToNBT(sPlayer.getEyePos()).getLongArray());
            stack.getNbt().putLongArray("initialLook", HexUtils.serializeToNBT(sPlayer.getRotationVector()).getLongArray());
            stack.getNbt().putLong("initialTime", world.getTime());
            stack.getNbt().putInt("delay", 0);
            stack.getNbt().putInt("resetDelay", 20);
        }
        player.setCurrentHand(usedHand);
        //cast immediately on use rather than waiting for the next tick
        stack.usageTick(world, player, stack.getMaxUseTime());
        return TypedActionResult.consume(stack);
    }
    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (user.isPlayer() && !world.isClient){
            ServerPlayerEntity sPlayer = world.getPlayerByUuid(user.getUuid()).getServer().getPlayerManager().getPlayer(user.getUuid());
            Hand usedHand;
            assert sPlayer != null;
            if(sPlayer.getStackInHand(Hand.MAIN_HAND) == stack){
                usedHand = Hand.MAIN_HAND;
            } else {
                usedHand = Hand.OFF_HAND;
            }
            castHex(stack, (ServerWorld) world, sPlayer, usedHand);
            /*List<Iota> instrs = getHex(stack, (ServerWorld) world);
            assert stack.getNbt() != null;
            int delay = stack.getNbt().getInt("delay");
            if (delay <= 0){
                if (delay < 0){
                    stack.getNbt().putInt("delay", 0);
                }
                var ctx = new CastingContext(sPlayer, usedHand, CastingContext.CastSource.PACKAGED_HEX);
                var harness = new CastingHarness(ctx);
                var info = harness.executeIotas(instrs, sPlayer.getWorld());
                if (info.getResolutionType().equals(ResolvedPatternType.ERRORED)){
                    sPlayer.stopUsingItem();
                    sPlayer.getItemCooldownManager().set(this, 20);
                }
            } else {
                stack.getNbt().putInt("delay", delay - 1);
            }*/
            //Oneironaut.LOGGER.info(info.getResolutionType());
        }
    }

    private boolean castHex(ItemStack stack, ServerWorld world, ServerPlayerEntity sPlayer, Hand usedHand){
        List<Iota> instrs = getHex(stack, world);
        assert stack.getNbt() != null;
        int delay = stack.getNbt().getInt("delay");
        if (delay <= 0){
            if (delay < 0){
                stack.getNbt().putInt("delay", 0);
            }
            var ctx = new CastingContext(sPlayer, usedHand, CastingContext.CastSource.PACKAGED_HEX);
            var harness = new CastingHarness(ctx);
            var info = harness.executeIotas(instrs, sPlayer.getWorld());
            if (info.getResolutionType().equals(ResolvedPatternType.ERRORED)){
                sPlayer.stopUsingItem();
                sPlayer.getItemCooldownManager().set(this, 20);
                return false;
            }
            return true;
        } else {
            stack.getNbt().putInt("delay", delay - 1);
        }
        return true;
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user.isPlayer() && !world.isClient){
            ServerPlayerEntity sPlayer = world.getPlayerByUuid(user.getUuid()).getServer().getPlayerManager().getPlayer(user.getUuid());
            assert sPlayer != null;
            assert stack.getNbt() != null;
            sPlayer.getItemCooldownManager().set(this, stack.getNbt().getInt("resetDelay"));
            //Oneironaut.LOGGER.info("Stopped casting from rod.");
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 20 * 60 * 60;
    }
}