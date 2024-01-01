package net.oneironaut.casting;

import at.petrak.hexcasting.api.misc.HexDamageSources;
import at.petrak.hexcasting.api.spell.mishaps.Mishap;
import at.petrak.hexcasting.ktxt.AccessorWrappers;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.minecraft.enchantment.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class OvercastDamageEnchant extends Enchantment {
    private static final Map<LivingEntity, Long> cooldownMap = new HashMap<>();
    public OvercastDamageEnchant() {
        super(Rarity.RARE, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public boolean isTreasure() {
        return true;
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return false;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof AxeItem || super.isAcceptableItem(stack);
    }

    @Override
    public void onTargetDamaged(LivingEntity user, Entity target, int level) {
        World world = user.world;
        long currentTime = world.getTime();
        long lastTime = cooldownMap.getOrDefault(user, 0L);
        if (target instanceof LivingEntity livingTarget && (lastTime + 20) < currentTime && !world.isClient){
            //user.sendMessage(Text.of(String.valueOf(lastTime)));
            //ripped from the trulyHurt method to see if I could get more consistent results
            boolean brainswept = false;
            if (livingTarget instanceof MobEntity mob){
                brainswept = IXplatAbstractions.INSTANCE.isBrainswept(mob);
            }
            if (!livingTarget.isInvulnerableTo(HexDamageSources.OVERCAST) && !livingTarget.isDead() && !brainswept){
                livingTarget.setHealth(livingTarget.getHealth() - level);
                AccessorWrappers.markHurt(livingTarget);
                if (livingTarget.isAlive() && livingTarget.getHealth() <= 1f && livingTarget instanceof MobEntity mob){
                    //if it has more than 100 max health, it's probably a boss, and I'm not letting people get flayed dragons
                    if (!(mob.getMaxHealth() > 100.0f)){
                        IXplatAbstractions.INSTANCE.brainsweep(mob);
                        if (user instanceof ServerPlayerEntity player){
                            IXplatAbstractions.INSTANCE.sendPacketNear(target.getPos(), 128.0, (ServerWorld) mob.world, new ParticleBurstPacket(
                                    target.getPos(), new Vec3d(0.0, 0.1, 0.0), 0.1, 0.025,
                                    IXplatAbstractions.INSTANCE.getColorizer(player), 64, false));
                            //Vec3d soundPos = mob.getPos();
                            world.playSoundFromEntity(null, mob, SoundEvents.ENTITY_ELDER_GUARDIAN_CURSE, SoundCategory.PLAYERS, 1.0f, 1.0f);
                        }
                    }
                }
            }
            //Mishap.Companion.trulyHurt(livingTarget, HexDamageSources.OVERCAST, level);
            cooldownMap.put(user, currentTime);
        }
    }

    public boolean canAccept(Enchantment other){
        return !(other instanceof DamageEnchantment) && this != other;
    }

    /*public float getAttackDamage(int level, EntityGroup group) {
        return 0.5F + (float)Math.max(0, level - 1) * 0.5F;
    }*/

    @Override
    public int getMaxLevel() {
        return 5;
    }
}
