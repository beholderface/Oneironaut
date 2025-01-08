package net.beholderface.oneironaut.mixin;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.beholderface.oneironaut.casting.RodState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.beholderface.oneironaut.casting.RodState;
import net.beholderface.oneironaut.item.ReverberationRod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

import static net.beholderface.oneironaut.MiscAPIKt.isUsingRod;


@SuppressWarnings("ConstantConditions")
@Mixin(value = CastingVM.class, priority = 1002/*gotta make sure to overwrite the hexal mixin here*/)
public abstract class QuietRodMixin {
    @Unique
    private final CastingVM oneironaut$harness = (CastingVM) (Object) this;

    /*@WrapOperation(method = "updateWithPattern", at = @At(value="INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", remap = false), remap = false)
    private boolean lessSparklyRod(List<OperatorSideEffect> sideEffects, Object o, Operation<Boolean> original){
        CastingEnvironment ctx = oneironaut$harness.getEnv();
        if (o instanceof OperatorSideEffect.Particles particles && isUsingRod(ctx)){
            RodState state = ReverberationRod.getState(ctx.getCaster());
            if (!(((ctx.getCaster().getWorld().getTime() - state.getTimestamp()) % 30.0) == 0)){
                return false;
            }
        }
        return original.call(sideEffects, o);
    }

    @ModifyVariable(method = "executeIotas",
    at = @At(value = "STORE"))
    private SoundEvent quieterRod(SoundEvent event) {
        CastingEnvironment ctx = oneironaut$harness.getEnv();
        if (isUsingRod(ctx)) {
            RodState state = ReverberationRod.getState(ctx.getCaster());
            ServerPlayerEntity caster = ctx.getCaster();
            //ServerWorld world = ctx.getWorld();
            if (caster != null) {
                //ItemStack activeStack = caster.getActiveItem();
                //play cast sound every 1.5 seconds
                if ((((caster.getWorld().getTime() - state.getTimestamp()) % 30.0) == 0) || event.equals(HexEvalSounds.MISHAP.sound())) {
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
    }*/
}
