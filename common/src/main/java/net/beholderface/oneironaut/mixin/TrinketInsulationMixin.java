package net.beholderface.oneironaut.mixin;

import at.petrak.hexcasting.api.spell.casting.CastingHarness;
import at.petrak.hexcasting.api.spell.casting.ResolvedPatternType;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.event.GameEvent;
import net.beholderface.oneironaut.Oneironaut;
import net.beholderface.oneironaut.item.InsulatedTrinketItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CastingHarness.class)
public abstract class TrinketInsulationMixin {

    //@Shadow protected Action getOperatorForFrame(ContinuationFrame frame, ServerWorld world);

    @WrapOperation(method = "executeIotas(Ljava/util/List;Lnet/minecraft/server/world/ServerWorld;)Lat/petrak/hexcasting/api/spell/casting/ControllerInfo;",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;emitGameEvent(Lnet/minecraft/entity/Entity;Lnet/minecraft/world/event/GameEvent;Lnet/minecraft/util/math/Vec3d;)V"))
    public void dontMakeVibration(ServerWorld instance, Entity entity, GameEvent gameEvent, Vec3d vec3d, Operation<Void> original
                                  , @Local ResolvedPatternType lastResolution
                                  ){
        boolean makesNoise = true;
        if (entity == InsulatedTrinketItem.getCurrentCaster()){
            //I'm not sure if this check actually does anything, but ¯\_(ツ)_/¯
            //the thing I was going for works anyway
            if (lastResolution.getSuccess()){
                //Oneironaut.LOGGER.info(lastResolution.getSuccess() + " " + lastResolution);
                makesNoise = false;
            }
            //disappointed
            /*ContinuationFrame frame = ((SpellContinuation.NotDone)continuation).getFrame();
            Action action = OneironautPatternRegistry.getOperatorForFrame2(frame, instance);
            //Oneironaut.LOGGER.info(action.getDisplayName());
            int media = 0;
            List<Iota> stack = harness.getStack();
            if (action instanceof ConstMediaAction constMediaAction){
                media = constMediaAction.getMediaCost();
            } else if (action instanceof SpellAction spellAction){
                try {
                    //hopefully nobody makes their spell actually do something in the execute method
                    media = spellAction.execute(harness.getStack(), ctx).getSecond();
                } catch (Exception e){
                    //mishaps are noisy
                    makesNoise = true;
                }
            }
            //making sure any stack changes that may be included in the execute method are not preserved
            harness.setStack(stack);
            if (media >= MediaConstants.DUST_UNIT / 8){
                makesNoise = true;
            }*/
        }
        if (makesNoise){
            original.call(instance, entity, gameEvent, vec3d);
        }
    }
}
