package net.beholderface.oneironaut.casting;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DepartureEntry {
    private static final Map<CastingEnvironment, Map<ServerWorld, DepartureEntry>> departureMap = new HashMap<>();

    public final Vec3d originPos;
    public final ServerWorld originDim;
    public final long timestamp;


    public DepartureEntry(CastingEnvironment ctx, ServerWorld world){
        Entity caster = ctx.getCastingEntity();
        assert caster != null;
        this.originPos = caster.getPos();
        this.originDim = world;
        this.timestamp = world.getServer().getOverworld().getTime();
        Map<ServerWorld, DepartureEntry> list = departureMap.get(ctx);
        if (list == null){
            Map<ServerWorld, DepartureEntry> newList = new HashMap<>();
            newList.put(world, this);
            departureMap.put(ctx, newList);
        } else {
            list.put(world, this);
        }
    }

    @Nullable
    public static DepartureEntry getEntry(@NotNull CastingEnvironment ctx, ServerWorld queried, boolean allowExpired){
        var relevantMap = departureMap.get(ctx);
        if (relevantMap != null){
            DepartureEntry entry = relevantMap.get(queried);
            if (entry != null && (!entry.isExpired() || allowExpired)){
                return entry;
            }
        }
        return null;
    }
    @Nullable
    public static DepartureEntry getEntry(@NotNull CastingEnvironment ctx, ServerWorld queried){
        return getEntry(ctx, queried, false);
    }

    public static void clearMap(){
        //not sure if the loop is actually needed, considering garbage collection, but just in case
        for (CastingEnvironment ctx : departureMap.keySet()){
            departureMap.get(ctx).clear();
        }
        departureMap.clear();
    }

    public boolean isWithinCylinder(Vec3d pos){
        Vec3d yZeroPos = new Vec3d(pos.x, 0.0, pos.z);
        Vec3d yZeroPos2 = new Vec3d(originPos.x, 0.0, originPos.z);
        return yZeroPos.distanceTo(yZeroPos2) <= 8;
    }
    public boolean isExpired(){
        return originDim.getServer().getOverworld().getTime() > timestamp;
    }
}
