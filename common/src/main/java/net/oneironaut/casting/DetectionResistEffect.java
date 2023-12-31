package net.oneironaut.casting;

import at.petrak.hexcasting.api.item.MediaHolderItem;
import at.petrak.hexcasting.api.misc.HexDamageSources;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.casting.CastingHarness;
import at.petrak.hexcasting.common.items.magic.ItemMediaHolder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.oneironaut.item.BottomlessMediaItem;

import java.sql.Array;

public class DetectionResistEffect extends StatusEffect {
    public DetectionResistEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0xcfa0f3);
    }
    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier){
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier){
        long time = entity.getWorld().getTime();
        if (!(entity.world.isClient)){
            ItemStack mainStack = entity.getMainHandStack();
            ItemStack offStack = entity.getOffHandStack();
            if ((time % 5) == 0){
                ((ServerWorld)entity.world).playSoundFromEntity(
                        null, entity, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.PLAYERS, 3f, 1f);
            }
            if (entity.isPlayer()){
                if ((time % 20) == 0){
                    ServerPlayerEntity player = (ServerPlayerEntity) entity;
                    CastingContext ctx = new CastingContext(player, Hand.MAIN_HAND, CastingContext.CastSource.STAFF);
                    CastingHarness harness = new CastingHarness(ctx);
                    int deficit = harness.withdrawMedia(MediaConstants.DUST_UNIT / 10, false);
                    if (deficit > 0 && (time % 40) == 0){
                        entity.damage(HexDamageSources.OVERCAST, 1f);
                    }
                }
            } else if (mainStack.getItem() instanceof BottomlessMediaItem || offStack.getItem() instanceof BottomlessMediaItem){
                //do nothing, they are immune
            } else if ((time % 40) == 0){
                entity.damage(HexDamageSources.OVERCAST, 1f);
            }
        }
    }
}