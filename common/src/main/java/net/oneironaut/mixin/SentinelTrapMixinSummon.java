package net.oneironaut.mixin;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.oneironaut.Oneironaut;
import net.oneironaut.block.SentinelTrapImpetusEntity;
import net.oneironaut.registry.OneironautThingRegistry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@SuppressWarnings("ConstantConditions")
@Mixin(targets = "at.petrak.hexcasting.common.casting.operators.spells.sentinel.OpCreateSentinel$Spell")
public abstract class SentinelTrapMixinSummon {
    @Final
    @Shadow private Vec3d target;

    @Unique
    private final Map<RegistryKey<World>, Map<BlockPos, Vec3d>> trapMap = SentinelTrapImpetusEntity.trapLocationMap;

    @Inject(method = "cast", at = @At("HEAD"), remap = false)
    public void triggerTrap(CastingContext ctx, CallbackInfo ci){
        //Oneironaut.LOGGER.info("you've activated my trap card!");
        World world = ctx.getWorld();
        RegistryKey<World> worldKey = world.getRegistryKey();
        if (trapMap.containsKey(worldKey)){
            Map<BlockPos, Vec3d> trapPosMap = trapMap.get(worldKey);
            Iterator<Map.Entry<BlockPos, Vec3d>> entryIterator = trapPosMap.entrySet().iterator();
            Map.Entry<BlockPos, Vec3d> currentEntry;
            List<BlockPos> expiredKeys = new ArrayList<>();
            while(entryIterator.hasNext()){
                currentEntry = entryIterator.next();
                if (target.isInRange(currentEntry.getValue(), 8.0)){
                    BlockPos pos = currentEntry.getKey();
                    if (world.getBlockState(pos).getBlock().getDefaultState().equals(OneironautThingRegistry.SENTINEL_TRAP.get().getDefaultState())){
                        SentinelTrapImpetusEntity be = (SentinelTrapImpetusEntity) world.getBlockEntity(pos);
                        Iterator<ServerPlayerEntity> playerEntityIterator = world.getServer().getPlayerManager().getPlayerList().iterator();
                        ServerPlayerEntity currentPlayer = null;
                        ServerPlayerEntity foundPlayer = null;
                        while (playerEntityIterator.hasNext() && (be.getStoredPlayer() != null)){
                            currentPlayer = playerEntityIterator.next();
                            if(currentPlayer.getUuid().equals(be.getStoredPlayer().getUuid()) && currentPlayer.getWorld().getRegistryKey().equals(worldKey)){
                                foundPlayer = currentPlayer;
                                break;
                            }
                        }
                        if (foundPlayer != null ){
                            be.setTargetPlayer(ctx.getCaster().getUuid());
                            be.activateSpellCircle(foundPlayer);
                            Oneironaut.LOGGER.info(ctx.getCaster().getName().getString()+" has activated "+foundPlayer.getName().getString()+"'s trap card!");
                        }
                    } else {
                        expiredKeys.add(currentEntry.getKey());
                    }
                }
            }
            for (BlockPos key : expiredKeys){
                trapPosMap.remove(key);
            }
        }
    }
}