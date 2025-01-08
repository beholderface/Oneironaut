package net.beholderface.oneironaut.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.beholderface.oneironaut.block.blockentity.NoosphereGateEntity;
import net.beholderface.oneironaut.registry.OneironautBlockRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import ram.talia.hexal.common.entities.BaseCastingWisp;

import java.util.Map;


@SuppressWarnings("ConstantConditions")
@Mixin(value = BaseCastingWisp.class)
public abstract class MiscWispMixin
{

    @Unique
    private final BaseCastingWisp oneironaut$wisp = (BaseCastingWisp) (Object) this;

    //the code for negating wisp upkeep
    @Unique
    private static final Map<RegistryKey<World>, Map<BlockPos, Vec3d>> gateMap = NoosphereGateEntity.gateLocationMap;

    @WrapOperation(method = "deductMedia",
            at = @At(value = "INVOKE",
                    target="Lram/talia/hexal/common/entities/BaseCastingWisp;getNormalCostPerTick()I",
                    remap = false),
            remap = false)
    public int freeIfNoosphereNormal(BaseCastingWisp wisp, Operation<Integer> original){
        if (oneironaut$free(wisp)){
            return 0;
        }
        return original.call(wisp);
    }

    @WrapOperation(method = "deductMedia",
            at = @At(value = "INVOKE",
                    target="Lram/talia/hexal/common/entities/BaseCastingWisp;getUntriggeredCostPerTick()I",
                    remap = false),
            remap = false)
    public int freeIfNoosphereSleepy(BaseCastingWisp wisp, Operation<Integer> original){
        if (oneironaut$free(wisp)){
            return 0;
        }
        return original.call(wisp);
    }

    @Unique
    private static boolean oneironaut$free(BaseCastingWisp wisp){
        boolean foundGate = false;
        //Contrary to what Big IDE wants you to think, casting wisp to Entity is not redundant.
        //This is because outside of dev environments, the desired methods do not seem to exist in BaseCastingWisp.
        //I have no idea why it thinks they do exist when in a dev environment.
        World world = ((Entity)wisp).getEntityWorld();
        RegistryKey<World> worldKey = world.getRegistryKey();
        String worldName = worldKey.getValue().toString();
        if(gateMap.containsKey(worldKey) && !(worldName.equals("oneironaut:noosphere"))){
            Map<BlockPos, Vec3d> gatePosMap = gateMap.get(worldKey);
            for (Map.Entry<BlockPos, Vec3d> map : gatePosMap.entrySet()){
                if (((Entity)wisp).getPos().isInRange(map.getValue(), 8.0)){
                    if(world.getBlockState(map.getKey()).getBlock().equals(OneironautBlockRegistry.NOOSPHERE_GATE.get().getDefaultState().getBlock())){
                        foundGate = true;
                    } else {
                        gatePosMap.remove(map.getKey());
                    }
                }
            }
        }
        if (worldName.equals("oneironaut:noosphere")){
            foundGate = true;
        }
        if (wisp.wispNumContainedPlayers() > 0){
            foundGate = false;
        }
        return foundGate;
    }

    //the code for modifying wisp volume here doesn't actually work because you can't set SoundInstance volume after construction
    /*@WrapOperation(method = "playCastSoundClient", at = @At(value = "INVOKE",
            target = "Lram/talia/hexal/common/lib/HexalSounds$SoundEntry;playAt(Lnet/minecraft/world/World;Lnet/minecraft/util/math/Vec3d;FFZ)V",
            remap = true))
    public void playVolumeModified(HexalSounds.SoundEntry instance, World world, Vec3d pos, float volume, float pitch, boolean fade, Operation<Void> original,
                                   //@Local WispCastingSoundInstance sound
                                   ){
        ComponentKey<DoubleComponent> volumeComponent = DoubleComponent.VOLUME;
        Optional<DoubleComponent> maybeVolume = volumeComponent.maybeGet(oneironaut$wisp);
        double multiplier = 1.0;
        if (maybeVolume.isPresent()){
            multiplier = maybeVolume.get().getValue();
            Oneironaut.LOGGER.info("component found, with value " + multiplier);
        } else {
            Oneironaut.LOGGER.info("no component found, using default multiplier of" + multiplier);
        }
        instance.playAt(world, pos, (0.3f * (float)multiplier), pitch, fade);
    }*/
}