package net.beholderface.oneironaut.item;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.casting.CastingHarness;
import at.petrak.hexcasting.api.spell.casting.ResolvedPatternType;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.common.items.magic.ItemPackagedHex;
import net.beholderface.oneironaut.Oneironaut;
import net.beholderface.oneironaut.casting.RodState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import net.beholderface.oneironaut.Oneironaut;
import net.beholderface.oneironaut.casting.RodState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ReverberationRod extends ItemPackagedHex  {

    public static final Identifier CASTING_PREDICATE = new Identifier(Oneironaut.MOD_ID, "is_casting");
    //public static final Map<UUID, Integer> DELAY_MAP = new HashMap<>();
    private static final Map<UUID, RodState> ROD_MAP = new HashMap<>();
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
        return UseAction.NONE;
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
            ROD_MAP.put(player.getUuid(), new RodState(player, true));
            /*stack.getNbt().putLongArray("initialPos", HexUtils.serializeToNBT(sPlayer.getEyePos()).getLongArray());
            stack.getNbt().putLongArray("initialLook", HexUtils.serializeToNBT(sPlayer.getRotationVector()).getLongArray());
            stack.getNbt().putLong("initialTime", world.getTime());
            stack.getNbt().putInt("delay", 0);
            stack.getNbt().putInt("resetDelay", 20);*/
            //DELAY_MAP.put(player.getUuid(), 0);
        }
        player.setCurrentHand(usedHand);
        //cast immediately on use rather than waiting for the next tick
        stack.usageTick(world, player, stack.getMaxUseTime());
        return TypedActionResult.consume(stack);
    }
    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (user.isPlayer() && !world.isClient){
            ServerPlayerEntity sPlayer = (ServerPlayerEntity) user; //world.getPlayerByUuid(user.getUuid()).getServer().getPlayerManager().getPlayer(user.getUuid());
            Hand usedHand;
            if(sPlayer.getStackInHand(Hand.MAIN_HAND) == stack){
                usedHand = Hand.MAIN_HAND;
            } else {
                usedHand = Hand.OFF_HAND;
            }
            if(!castHex(stack, (ServerWorld) world, sPlayer, usedHand)){
                sPlayer.stopUsingItem();
            }
        }
    }

    private boolean castHex(ItemStack stack, ServerWorld world, ServerPlayerEntity sPlayer, Hand usedHand){
        List<Iota> instrs = getHex(stack, world);
        //assert stack.getNbt() != null;
        //int delay = stack.getNbt().getInt("delay");
        RodState state = ROD_MAP.get(sPlayer.getUuid());
        int delay = state.getDelay();
        if (delay <= 0){
            if (delay < 0){
                state.setDelay(0);
            }
            var ctx = new CastingContext(sPlayer, usedHand, CastingContext.CastSource.PACKAGED_HEX);
            var harness = new CastingHarness(ctx);
            var info = harness.executeIotas(instrs, sPlayer.getWorld());
            if (info.getResolutionType().equals(ResolvedPatternType.ERRORED)){
                /*sPlayer.stopUsingItem();
                sPlayer.getItemCooldownManager().set(this, 20);*/
                state.setResetCooldown(20);
                return false;
            }
            return state.getCurrentlyCasting();
        } else {
            state.adjustDelay(-1);
            //DELAY_MAP.put(sPlayer.getUuid(), delay - 1);
            //stack.getNbt().putInt("delay", delay - 1);
        }
        return true;
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user.isPlayer() && !world.isClient){
            ServerPlayerEntity sPlayer = (ServerPlayerEntity) user;//world.getPlayerByUuid(user.getUuid()).getServer().getPlayerManager().getPlayer(user.getUuid());
            //assert sPlayer != null;
            //assert stack.getNbt() != null;
            RodState state = ROD_MAP.get(user.getUuid());
            sPlayer.getItemCooldownManager().set(this, state.getResetCooldown());
            state.setCurrentlyCasting(false);
            //Oneironaut.LOGGER.info("Stopped casting from rod.");
        }
    }

    public static RodState getState(Entity user){
        return ROD_MAP.getOrDefault(user.getUuid(), null);
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 20 * 60 * 60;
    }
}
