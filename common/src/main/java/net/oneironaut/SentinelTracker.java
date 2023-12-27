//do I even need this?
/*
package net.oneironaut;

import at.petrak.hexcasting.api.player.Sentinel;
import at.petrak.hexcasting.api.spell.iota.*;
import at.petrak.hexcasting.common.items.ItemFocus;
import at.petrak.hexcasting.common.lib.HexItems;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.util.*;

public class SentinelTracker extends PersistentState {

    //setup for Sentinel Trap Impetus
    private static Map<UUID, Sentinel> sentinelMap = new HashMap<>();
    private static final int minuteInTicks = 20 * 60;
    private static final int hourInTicks = minuteInTicks * 60;


    //save NBT of the map
    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        Iterator<Map.Entry<UUID, Sentinel>> iotaIterator = sentinelMap.entrySet().iterator();
        Map.Entry<UUID, Sentinel> nextSentinel;
        while (iotaIterator.hasNext()){
            nextSentinel = iotaIterator.next();
            nbt.putString(nextSentinel.getKey().toString(), nextSentinel.getValue().toString());
        }
        return nbt;
    }

    //reassemble the map from NBT
    public static SentinelTracker createFromNbt(NbtCompound nbt){
        SentinelTracker ideas = new SentinelTracker();
        Map<UUID, String> reconstructedSentinelMap = new HashMap<>();
        Iterator<String> ideaIterator = nbt.getKeys().iterator();
        String currentIdeaKey;
        while (ideaIterator.hasNext()){
            currentIdeaKey = ideaIterator.next();
            reconstructedSentinelMap.put(UUID.fromString(currentIdeaKey), nbt.getString(currentIdeaKey));
        }
        sentinelMap = reconstructedSentinelMap;
        return ideas;
    }

    public static SentinelTracker getServerState(MinecraftServer server){
        PersistentStateManager stateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();
        SentinelTracker ideas = stateManager.getOrCreate(SentinelTracker::createFromNbt, SentinelTracker::new, Oneironaut.MOD_ID);
        ideas.markDirty();
        return ideas;
    }

    public static boolean addSentinel(ServerPlayerEntity player){
        UUID uuid = player.getUuid();
        Sentinel sentinel = IXplatAbstractions.INSTANCE.getSentinel(player);
        if (!(sentinelMap.containsKey(uuid))){
            sentinelMap.put(uuid, sentinel.toString());
            Oneironaut.LOGGER.info("Added " + sentinel + " to the sentinel map");
            return true;
        } else {
            return false;
        }
    }
}
*/
