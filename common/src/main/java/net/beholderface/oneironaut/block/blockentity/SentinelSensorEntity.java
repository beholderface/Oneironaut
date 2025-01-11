package net.beholderface.oneironaut.block.blockentity;

import at.petrak.hexcasting.api.player.Sentinel;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.beholderface.oneironaut.block.SentinelSensor;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.beholderface.oneironaut.registry.OneironautBlockRegistry;
import net.beholderface.oneironaut.registry.OneironautItemRegistry;

import java.util.List;

public class SentinelSensorEntity extends BlockEntity {
    //private static List<Direction> directions = new ArrayList<>();
    private static final Direction[] directions = {Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH};
    public SentinelSensorEntity(BlockPos pos, BlockState state){
        super(OneironautBlockRegistry.SENTINEL_SENSOR_ENTITY.get(), pos, state);
    }

    public void tick(World world, BlockPos pos, BlockState state){
        if ((world.getTime() % 4) == 0 && !(world.isClient)){
            MinecraftServer server = world.getServer();
            Vec3d posCenter = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            RegistryKey<World> worldKey = world.getRegistryKey();
            assert server != null;
            List<ServerPlayerEntity> players = server.getPlayerManager().getPlayerList();
            Sentinel currentSentinel = null;
            int output = 0;
            int prospectiveOutput = 0;
            boolean great = false;
            for (ServerPlayerEntity player : players){
                currentSentinel = IXplatAbstractions.INSTANCE.getSentinel(player);
                if (currentSentinel != null && currentSentinel.dimension().equals(worldKey) && currentSentinel.position().isInRange(posCenter, 16.0)){
                    prospectiveOutput = (int) Math.abs(currentSentinel.position().subtract(posCenter).length() - 15);
                    if (prospectiveOutput > output){
                        output = prospectiveOutput;
                        great = currentSentinel.extendsRange();
                    }
                }
            }
            BlockState newState = state;
            output = Math.min(output, 15);
            if (state.get(Properties.LEVEL_15) != output){
                newState = state.with(Properties.LEVEL_15, output);
            }
            if (state.get(SentinelSensor.GREAT) != great){
                newState = state.with(SentinelSensor.GREAT, great);
            }
            if (!(state.equals(newState))){
                world.setBlockState(pos, newState);
                world.updateNeighborsAlways(pos, OneironautBlockRegistry.SENTINEL_SENSOR.get());
                for (Direction dir : directions){
                    world.updateNeighborsAlways(pos.offset(dir), OneironautBlockRegistry.SENTINEL_SENSOR.get());
                }
                world.updateComparators(pos, newState.getBlock());
            }
        }
    }
}
