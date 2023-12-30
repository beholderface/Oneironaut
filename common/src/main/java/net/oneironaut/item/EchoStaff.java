package net.oneironaut.item;

import at.petrak.hexcasting.common.items.ItemStaff;
import at.petrak.hexcasting.common.lib.HexSounds;
import at.petrak.hexcasting.common.network.MsgOpenSpellGuiAck;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class EchoStaff extends ItemStaff {
    public EchoStaff(Settings pProperties) {
        super(pProperties);
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (player.isSneaking()) {
            player.playSound(SoundEvents.BLOCK_SCULK_SHRIEKER_SHRIEK, 1f, 1f);
            if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer) {
                IXplatAbstractions.INSTANCE.clearCastingData(serverPlayer);
            }
        } else {
            player.playSound(SoundEvents.BLOCK_SCULK_SENSOR_CLICKING, 1f, 1f);
        }

        if (!world.isClient() && player instanceof ServerPlayerEntity serverPlayer) {
            var harness = IXplatAbstractions.INSTANCE.getHarness(serverPlayer, hand);
            var patterns = IXplatAbstractions.INSTANCE.getPatterns(serverPlayer);
            var descs = harness.generateDescs();

            IXplatAbstractions.INSTANCE.sendPacketToPlayer(serverPlayer,
                    new MsgOpenSpellGuiAck(hand, patterns, descs.getFirst(), descs.getSecond(), descs.getThird(),
                            harness.getParenCount()));
        }

        player.incrementStat(Stats.USED.getOrCreateStat(this));
//        player.gameEvent(GameEvent.ITEM_INTERACT_START);

        return TypedActionResult.success(player.getStackInHand(hand));
    }
}
