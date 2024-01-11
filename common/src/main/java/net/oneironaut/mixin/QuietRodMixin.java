package net.oneironaut.mixin;

import at.petrak.hexcasting.api.spell.ParticleSpray;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.casting.CastingHarness;
import at.petrak.hexcasting.api.spell.casting.ControllerInfo;
import at.petrak.hexcasting.api.spell.casting.sideeffects.OperatorSideEffect;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.oneironaut.casting.RodState;
import net.oneironaut.item.BottomlessMediaItem;
import net.oneironaut.item.ReverberationRod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ram.talia.hexal.api.spell.casting.IMixinCastingContext;

import java.util.List;

import static net.oneironaut.MiscAPIKt.isUsingRod;


@SuppressWarnings("ConstantConditions")
@Mixin(value = CastingHarness.class, priority = 1002/*gotta make sure to overwrite the hexal mixin here*/)
public abstract class QuietRodMixin {
    @Unique
    private final CastingHarness oneironaut$harness = (CastingHarness) (Object) this;
    /*@Unique
    private final CastingContext oneironaut$ctx = oneironaut$harness.getCtx();*/
    @Redirect(method = "updateWithPattern",
            at = @At(
                    value="INVOKE",
                    target = "Ljava/util/List;add(Ljava/lang/Object;)Z"
            ),
            remap = false)
    private boolean updateWithPatternRodOrWisp (List<OperatorSideEffect> sideEffects, Object o) {
        CastingContext ctx = oneironaut$harness.getCtx();
        if (o instanceof OperatorSideEffect.Particles particles) {
            IMixinCastingContext ctxi = (IMixinCastingContext)(Object) ctx;
            //ctx.getCaster().sendMessage(Text.of("mixin is doing a thing"));
            if (!(isUsingRod(ctx) || ctxi.hasWisp()))
                return sideEffects.add(particles);
            else if (isUsingRod(ctx)){
                //do particles every 30 ticks
                RodState state = ReverberationRod.getState(ctx.getCaster());
                if ((((ctx.getCaster().getWorld().getTime() - state.getTimestamp()) % 30.0) == 0)){
                    return sideEffects.add(particles);
                } else {
                    return false;
                }
            }else {
                return false;
            }
        }
        return sideEffects.add((OperatorSideEffect) o);
    }

    @ModifyVariable(method = "executeIotas",
    at = @At(value = "STORE"))
    private SoundEvent thing(SoundEvent event){
        CastingContext ctx = oneironaut$harness.getCtx();
        if (isUsingRod(ctx)){
            RodState state = ReverberationRod.getState(ctx.getCaster());
            ServerPlayerEntity caster = ctx.getCaster();
            //ServerWorld world = ctx.getWorld();
            if (caster != null){
                //ItemStack activeStack = caster.getActiveItem();
                //play cast sound every 1.5 seconds
                if ((((caster.getWorld().getTime() - state.getTimestamp()) % 30.0) == 0) || event.equals(HexEvalSounds.MISHAP.sound())){
                    return event;
                } else {
                    return SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME;
                }
            } else {
                return SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME;
            }
        } else {
            return event;
        }
    }

    /*@Inject(method = "executeIotas", at = @At("HEAD"))
    private void thing(List<? extends Iota> iotas, ServerWorld world, CallbackInfoReturnable<ControllerInfo> cir){
        //make sure the bottomless phial timestamp is always correct (this has nothing to do with quiet rods, I just needed somewhere to put this)
        CastingContext ctx = oneironaut$harness.getCtx();
        BottomlessMediaItem.time = ctx.getWorld().getTime();
    }*/

    /*@SuppressWarnings("DefaultAnnotationParam")
    @Redirect(method = "executeIotas",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/world/ServerWorld;playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"
                    , remap = false
            ),
            remap = true)
    private void playSoundRodOrWisp (ServerWorld world, PlayerEntity player, double x, double y, double z, SoundEvent soundEvent, SoundCategory soundSource, float v, float p) {
        CastingContext ctx = harness.getCtx();
        IMixinCastingContext wispContext = (IMixinCastingContext) (Object) ctx;

        BaseCastingWisp wisp = wispContext.getWisp();

        if (wisp != null) {
            wisp.scheduleCastSound();
        } else if (isUsingRod(ctx)) {
            ServerPlayerEntity caster = ctx.getCaster();
            if (caster != null){
                ItemStack activeStack = caster.getActiveItem();
                //play cast sound every 1.5 seconds
                if ((((caster.getWorld().getTime() - activeStack.getNbt().getDouble("initialTime")) % 30.0) == 0)){
                    world.playSound(player, x, y, z, soundEvent, soundSource, v, p);
                }
            }
        } else {
            world.playSound(player, x, y, z, soundEvent, soundSource, v, p);
        }
    }*/
}
