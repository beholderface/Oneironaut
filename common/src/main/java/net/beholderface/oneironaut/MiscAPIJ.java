package net.beholderface.oneironaut;

import kotlin.Pair;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;

//this class exists for API things that I understand better without kotlin
public class MiscAPIJ {
    public static class departure{
        private static final Map<LivingEntity, Map<ServerWorld, Pair<Vec3d, Long>>> departureData = new HashMap<>();
        public static boolean putNewUser(LivingEntity user) {
            if (!departureData.containsKey(user)) {
                Map<ServerWorld, Pair<Vec3d, Long>> newMap = new HashMap<>();
                departureData.put(user, newMap);
                return true;
            } else {
                return false;
            }
        }
        public static boolean addDepartureEntry(LivingEntity user){
            if (departureData.containsKey(user)){
                Map<ServerWorld, Pair<Vec3d, Long>> data = departureData.get(user);
                data.put((ServerWorld) user.getWorld(), new Pair<>(user.getPos(), user.getWorld().getTime()));
                return true;
            } else {
                return false;
            }
        }
        public static boolean departedThisTick(LivingEntity user, ServerWorld world){
            if (departureData.containsKey(user)){
                Map<ServerWorld, Pair<Vec3d, Long>> data = departureData.get(user);
                if (data.containsKey(world)){
                    Pair<Vec3d, Long> dimData = data.get(world);
                    return dimData.getSecond() == world.getTime();
                }
            }
            return false;
        }
    }
}
