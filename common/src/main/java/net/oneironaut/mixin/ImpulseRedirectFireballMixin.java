package net.oneironaut.mixin;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.oneironaut.MiscAPIKt;
import net.oneironaut.Oneironaut;
import net.oneironaut.OneironautConfig;
import net.oneironaut.casting.FireballUpdatePacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//import static net.oneironaut.Oneironaut.playersToUpdate;

@SuppressWarnings("ConstantConditions")
@Mixin(targets = "at.petrak.hexcasting.common.casting.operators.spells.OpAddMotion$Spell")
public abstract class ImpulseRedirectFireballMixin {

    @Final
    @Shadow
    private Vec3d motion;
    @Final
    @Shadow
    private Entity target;

    @Unique
    private static final Identifier immuneTag = new Identifier(Oneironaut.MOD_ID, "impulse_redirect_blacklist");
    @Inject(method = "cast", at = @At(value = "RETURN", remap = false), remap = false)
    public void redirectFireball(CastingContext ctx, CallbackInfo ci/*, @Local(ordinal = 0) Entity target*/){
        if (target instanceof ExplosiveProjectileEntity explosive && OneironautConfig.getServer().getImpulseRedirectsFireball()){
            /*TrackedData<Float> POWER_X = DataTracker.registerData(ExplosiveProjectileEntity.class, TrackedDataHandlerRegistry.FLOAT);
            TrackedData<Float> POWER_Y = DataTracker.registerData(ExplosiveProjectileEntity.class, TrackedDataHandlerRegistry.FLOAT);
            TrackedData<Float> POWER_Z = DataTracker.registerData(ExplosiveProjectileEntity.class, TrackedDataHandlerRegistry.FLOAT);*/
            boolean immune = false;
            if (explosive instanceof WitherSkullEntity skull){
                //blue skulls are immune to the redirection
                if (skull.isCharged()){
                    immune = true;
                }
            }
            if (target.getType().isIn(MiscAPIKt.getEntityTagKey(immuneTag))){
                immune = true;
            }
            double deltaDelta = immune ? 0 : 1;
            double deltaX = motion.getX() * deltaDelta;
            double deltaY = motion.getY() * deltaDelta;
            double deltaZ = motion.getZ() * deltaDelta;
            /*DataTracker tracker = explosive.getDataTracker();
            tracker.startTracking(POWER_X, (float)explosive.powerX);
            tracker.startTracking(POWER_Y, (float)explosive.powerY);
            tracker.startTracking(POWER_Z, (float)explosive.powerZ);*/
            Vec3d oldPower = new Vec3d(explosive.powerX, explosive.powerY, explosive.powerZ);
            explosive.powerX = explosive.powerX + deltaX;
            explosive.powerY = explosive.powerY + deltaY;
            explosive.powerZ = explosive.powerZ + deltaZ;
            Vec3d newPower = new Vec3d(explosive.powerX, explosive.powerY, explosive.powerZ);
            if (!immune){
                explosive.setOwner(ctx.getCaster());
            }
            /*tracker.set(POWER_X, (float) (explosive.powerX + deltaX));
            tracker.set(POWER_Y, (float) (explosive.powerX + deltaY));
            tracker.set(POWER_Z, (float) (explosive.powerX + deltaZ));*/
            if (!newPower.equals(oldPower)){
                IXplatAbstractions.INSTANCE.sendPacketNear(explosive.getPos(), 128, ctx.getWorld(), new FireballUpdatePacket(newPower, explosive));
            }
        }
    }
}
