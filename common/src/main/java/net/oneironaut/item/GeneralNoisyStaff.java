package net.oneironaut.item;

import at.petrak.hexcasting.common.items.ItemStaff;
import at.petrak.hexcasting.common.network.MsgOpenSpellGuiAck;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class GeneralNoisyStaff extends ItemStaff {
    public final SoundEvent openSound;
    public final SoundEvent resetSound;
    private static final Float[] defaultSoundModifiers = {0.5f, 1f, 0.5f, 1f};
    private final Float[] soundModifiers;
    public GeneralNoisyStaff(Settings pProperties, SoundEvent openSound, SoundEvent resetSound, @Nullable Float[] soundModifiers) {
        super(pProperties);
        this.openSound = openSound;
        this.resetSound = resetSound;
        this.soundModifiers = soundModifiers == null ? defaultSoundModifiers : soundModifiers;
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        super.use(world, player, hand);
        if (world.isClient){
            if (player.isSneaking()){
                player.playSound(this.resetSound, this.soundModifiers[0], this.soundModifiers[1]);
            } else {
                player.playSound(this.openSound, this.soundModifiers[2], this.soundModifiers[3]);
            }
        }
        /*if (player.isSneaking()) {
            player.playSound(this.resetSound, this.soundModifiers[0], this.soundModifiers[1]);
            if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer) {
                IXplatAbstractions.INSTANCE.clearCastingData(serverPlayer);
            }
        } else {
            player.playSound(this.openSound, this.soundModifiers[2], this.soundModifiers[3]);
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
//        player.gameEvent(GameEvent.ITEM_INTERACT_START);*/

        return TypedActionResult.success(player.getStackInHand(hand));
    }
}
