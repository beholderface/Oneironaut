package net.oneironaut.mixin;

import at.petrak.hexcasting.api.spell.casting.CastingHarness;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.tick.Tick;
import net.oneironaut.block.NoosphereGateEntity;
import net.oneironaut.registry.OneironautThingRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ram.talia.hexal.api.HexalAPI;
import ram.talia.hexal.api.config.HexalConfig;
import ram.talia.hexal.common.entities.BaseCastingWisp;
import ram.talia.hexal.common.entities.BaseWisp;
import ram.talia.hexal.common.entities.TickingWisp;

import java.util.Iterator;
import java.util.Map;


@SuppressWarnings("ConstantConditions")
@Mixin(value = BaseCastingWisp.class)
public abstract class NoosphereWispMixin
{
    /*@Unique
    private final BaseCastingWisp wisp = (BaseCastingWisp) (Object) this;*/
    @Unique
    private final int baseUpkeep = HexalConfig.getServer().getTickingWispUpkeepPerTick();
    @Unique
    private final Map<RegistryKey<World>, Map<BlockPos, Vec3d>> gateMap = NoosphereGateEntity.gateLocationMap;
    @Redirect(method = "deductMedia",
            at = @At(value = "INVOKE",
            target="Lram/talia/hexal/common/entities/BaseCastingWisp;getNormalCostPerTick()I",
                    remap = false),
            remap = false)
    public int freeIfNoosphere1(BaseCastingWisp wisp){
        boolean foundGate = false;
        World world = wisp.getEntityWorld();
        RegistryKey<World> worldKey = world.getRegistryKey();
        String worldName = worldKey.getValue().toString();
        if(gateMap.containsKey(worldKey) && !(worldName.equals("oneironaut:noosphere"))){
            Map<BlockPos, Vec3d> gatePosMap = gateMap.get(worldKey);
            Iterator<Map.Entry<BlockPos, Vec3d>> entryIterator = gatePosMap.entrySet().iterator();
            Map.Entry<BlockPos, Vec3d> currentEntry;
            while(entryIterator.hasNext()){
                currentEntry = entryIterator.next();
                if (wisp.getPos().isInRange(currentEntry.getValue(), 8.0)){
                    if(world.getBlockState(currentEntry.getKey()).getBlock().getDefaultState().equals(OneironautThingRegistry.NOOSPHERE_GATE.get().getDefaultState())){
                        foundGate = true;
                        break;
                    } else {
                        gatePosMap.remove(currentEntry.getKey());
                    }
                }
            }
        }
        if (worldName.equals("oneironaut:noosphere") || foundGate){
            return wisp.wispNumContainedPlayers() < 1 ? 0 : baseUpkeep;
        } else {
            return baseUpkeep;
        }
    }
    @Redirect(method = "deductMedia",
            at = @At(value = "INVOKE",
                    target="Lram/talia/hexal/common/entities/BaseCastingWisp;getUntriggeredCostPerTick()I",
                    remap = false),
            remap = false)
    public int freeIfNoosphere2(BaseCastingWisp wisp){
        boolean foundGate = false;
        double discount = HexalConfig.getServer().getUntriggeredWispUpkeepDiscount();
        int discountedUpkeep = (int) (baseUpkeep * discount);
        World world = wisp.getEntityWorld();
        RegistryKey<World> worldKey = world.getRegistryKey();
        String worldName = worldKey.getValue().toString();
        if(gateMap.containsKey(worldKey) && !(worldName.equals("oneironaut:noosphere"))){
            Map<BlockPos, Vec3d> gatePosMap = gateMap.get(worldKey);
            Iterator<Map.Entry<BlockPos, Vec3d>> entryIterator = gatePosMap.entrySet().iterator();
            Map.Entry<BlockPos, Vec3d> currentEntry;
            while(entryIterator.hasNext()){
                currentEntry = entryIterator.next();
                if (wisp.getPos().isInRange(currentEntry.getValue(), 8.0)){
                    if(world.getBlockState(currentEntry.getKey()).getBlock().getDefaultState().equals(OneironautThingRegistry.NOOSPHERE_GATE.get().getDefaultState())){
                        foundGate = true;
                        break;
                    } else {
                        gatePosMap.remove(currentEntry.getKey());
                    }
                }
            }
        }
        if (worldName.equals("oneironaut:noosphere") || foundGate){
            return wisp.wispNumContainedPlayers() < 1 ? 0 : discountedUpkeep;
        } else {
            return discountedUpkeep;
        }
    }
}
