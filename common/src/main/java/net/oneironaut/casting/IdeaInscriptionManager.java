package net.oneironaut.casting;

import at.petrak.hexcasting.api.spell.iota.*;
import at.petrak.hexcasting.api.spell.mishaps.MishapOthersName;
import at.petrak.hexcasting.api.utils.HexUtils;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.types.Type;
import dev.architectury.registry.registries.Registries;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import net.oneironaut.Oneironaut;

import java.util.*;

public class IdeaInscriptionManager extends PersistentState {


    //setup for Idea Inscription
    private static Map<String, NbtCompound> iotaMap = new HashMap<>();
    private static final int minuteInTicks = 20 * 60;
    private static final int hourInTicks = minuteInTicks * 60;


    //save NBT of the map
    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        Iterator<Map.Entry<String, NbtCompound>> iotaIterator = iotaMap.entrySet().iterator();
        Map.Entry<String, NbtCompound> nextIota;
        while (iotaIterator.hasNext()){
            nextIota = iotaIterator.next();
            nbt.put(nextIota.getKey(), nextIota.getValue());
        }
        return nbt;
    }

    //reassemble the map from NBT
    public static IdeaInscriptionManager createFromNbt(NbtCompound nbt){
        IdeaInscriptionManager ideas = new IdeaInscriptionManager();
        Map<String, NbtCompound> reconstructedIotaMap = new HashMap<>();
        Iterator<String> ideaIterator = nbt.getKeys().iterator();
        String currentIdeaKey;
        while (ideaIterator.hasNext()){
            currentIdeaKey = ideaIterator.next();
            reconstructedIotaMap.put(currentIdeaKey, nbt.getCompound(currentIdeaKey));
        }
        iotaMap = reconstructedIotaMap;
        return ideas;
    }

    public static IdeaInscriptionManager getServerState(MinecraftServer server){
        PersistentStateManager stateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();
        IdeaInscriptionManager ideas = stateManager.getOrCreate(IdeaInscriptionManager::createFromNbt, IdeaInscriptionManager::new, Oneironaut.MOD_ID);
        ideas.markDirty();
        return ideas;
    }

    public static void cleanMap(MinecraftServer server, IdeaInscriptionManager ideaState){
        ArrayList<String> KeysToRemove= new ArrayList<String>();
        //remove map entries that correspond to nonexistent entities
        Iterator<String> keys = iotaMap.keySet().iterator();
        long overworldTime = server.getOverworld().getTime();
        Oneironaut.LOGGER.info("Cleaning expired idea entries, current time is "+overworldTime);
        String currentKey;
        NbtCompound currentData;
        long timestamp;
        while (keys.hasNext()){
            //Oneironaut.LOGGER.info("About to iterate key");
            currentKey = keys.next();
            currentData = iotaMap.get(currentKey);
            timestamp = currentData.getLong("timestamp");
            //Oneironaut.LOGGER.info("Key " + currentKey + " iterated");
            if ((timestamp + hourInTicks) < overworldTime){
                Oneironaut.LOGGER.info("Found expired key " + currentKey + ", expired by " + (overworldTime - timestamp) + " ticks.");
                KeysToRemove.add(currentKey);
            }
        }
        if (!KeysToRemove.isEmpty()){
            Iterator<String> stringIter = KeysToRemove.iterator();
            String currentString;
            while (stringIter.hasNext()){
                currentString = stringIter.next();
                //Oneironaut.LOGGER.info("Removing key " + currentString);
                iotaMap.remove(currentString);
            }
            ideaState.markDirty();
            Oneironaut.LOGGER.info("Removed " + KeysToRemove.size() + " expired entries.");
        }
    }

    public static void writeIota(Object key, Iota iota, ServerPlayerEntity player, ServerWorld world){
        IotaType<?> type = iota.getType();
        NbtCompound iotaNbt = new NbtCompound();
        iotaNbt.putLong("timestamp", world.getTime());
        iotaNbt.put("iota", iota.serialize());
        iotaNbt.putString("type", type.typeName().getString());
        iotaMap.put(key.toString(), iotaNbt);
    }

    public static void eraseIota(Object key){
        if (key.equals("everything")){
            iotaMap.clear();
        } else {
            iotaMap.remove(key.toString());
        }
    }

    public static Iota readIota(Object key, ServerWorld world){
        String keyString = key.toString();
        NbtCompound iotaNbt = iotaMap.getOrDefault(keyString, null);
        Iota iota = new GarbageIota();

        if (iotaNbt != null){
            if ((iotaNbt.getLong("timestamp") + hourInTicks) >= world.getTime()){
                String typeString = iotaNbt.getString("type");
                Iterator<IotaType<?>> types = IXplatAbstractions.INSTANCE.getIotaTypeRegistry().iterator();
                IotaType<?> currentType;
                while (types.hasNext()){
                    currentType = types.next();
                    if (currentType.typeName().getString().equals(typeString)){
                        iota = currentType.deserialize(iotaNbt.get("iota"), world);
                        break;
                    }
                }
            } else {
                IdeaInscriptionManager ideaState = IdeaInscriptionManager.getServerState(world.getServer());
                iotaMap.remove(key.toString());
                ideaState.markDirty();
            }
        }
        return iota;
    }
}
