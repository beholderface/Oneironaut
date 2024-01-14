package net.oneironaut.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.oneironaut.block.NoosphereGateEntity;
import net.oneironaut.registry.OneironautBlockRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import ram.talia.hexal.api.config.HexalConfig;
import ram.talia.hexal.common.entities.BaseCastingWisp;

import java.util.Iterator;
import java.util.Map;


@SuppressWarnings("ConstantConditions")
@Mixin(value = BaseCastingWisp.class)
public abstract class NoosphereWispMixin
{
    @Unique
    private final int baseUpkeep = HexalConfig.getServer().getTickingWispUpkeepPerTick();
    @Unique
    private static final Map<RegistryKey<World>, Map<BlockPos, Vec3d>> gateMap = NoosphereGateEntity.gateLocationMap;

    @WrapOperation(method = "deductMedia",
            at = @At(value = "INVOKE",
                    target="Lram/talia/hexal/common/entities/BaseCastingWisp;getNormalCostPerTick()I",
                    remap = false),
            remap = false)
    public int freeIfNoosphereNormal(BaseCastingWisp wisp, Operation<Integer> original){
        if (free(wisp)){
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
        if (free(wisp)){
            return 0;
        }
        return original.call(wisp);
    }

    @Unique
    private static boolean free(BaseCastingWisp wisp){
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
}
