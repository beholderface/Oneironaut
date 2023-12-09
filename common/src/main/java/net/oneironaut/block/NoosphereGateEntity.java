package net.oneironaut.block;

import at.petrak.hexcasting.api.misc.MediaConstants;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.oneironaut.registry.OneironautThingRegistry;
import static net.oneironaut.MiscAPIKt.stringToWorld;
import static net.oneironaut.MiscAPIKt.playerUUIDtoServerPlayer;
import at.petrak.hexcasting.common.particles.ConjureParticleOptions;
import ram.talia.hexal.common.entities.TickingWisp;
import ram.talia.hexal.common.lib.HexalEntities;

import java.util.Iterator;
import java.util.List;

public class NoosphereGateEntity extends BlockEntity {
    public NoosphereGateEntity(BlockPos pos, BlockState state) {
        super(OneironautThingRegistry.NOOSPHERE_GATE_ENTITY, pos, state);
        //Oneironaut.LOGGER.info("super Creating blockentity.");
    }
    public void tick(World world, BlockPos pos, BlockState state){
        //Oneironaut.LOGGER.info("Spam.");
        if (!world.isClient){
            Vec3d doublePos = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            PlayerEntity possiblePassenger = world.getClosestPlayer(doublePos.x, doublePos.y, doublePos.z, 2.0, false);
            if (possiblePassenger != null){
                Vec3d passengerMidpoint = new Vec3d(possiblePassenger.getX(), possiblePassenger.getY() + (possiblePassenger.getHeight() / 2), possiblePassenger.getZ());
                if (passengerMidpoint.isInRange(doublePos, 1)){
                    MinecraftServer server = possiblePassenger.getServer();
                    ServerPlayerEntity player = playerUUIDtoServerPlayer(possiblePassenger.getUuid(), server);
                    //possiblePassenger.sendMessage(Text.of("teleporting"));
                    ServerWorld noosphere = null;
                    ServerWorld origin = player.getWorld();
                    ServerWorld current = origin;
                    //Oneironaut.LOGGER.info("Current: " + current.getRegistryKey().getValue().toString());
                    if (!(current.getRegistryKey().getValue().toString().equals("oneironaut:noosphere"))){
                        //Oneironaut.LOGGER.info("Iterating.");
                        for (ServerWorld serverWorld : server.getWorlds()) {
                            current = serverWorld;
                            //Oneironaut.LOGGER.info("Current: " + current.getRegistryKey().getValue().toString());
                            if (current.getRegistryKey().getValue().toString().equals("oneironaut:noosphere")) {
                                noosphere = current;
                                //Oneironaut.LOGGER.info("Noosphere REALLY shouldn't be null.");
                                break;
                            }
                        }
                    } else {
                        noosphere = current;
                    }
                    /*if (noosphere != null){
                        Oneironaut.LOGGER.info("Noosphere: " + noosphere.getRegistryKey().getValue().toString());
                    } else {
                        Oneironaut.LOGGER.info("Noosphere is null.");
                    }*/
                    //Oneironaut.LOGGER.info("Noosphere: " + noosphere.toString());
                    double compressionFactor;
                    if (noosphere != null){
                        if (noosphere == origin){
                            ServerWorld homeDim = stringToWorld(player.getSpawnPointDimension().getValue().toString(), player);
                            compressionFactor = 1 / homeDim.getDimension().coordinateScale();
                            BlockPos spawnpoint = player.getSpawnPointPosition();
                            if (spawnpoint == null){
                                spawnpoint = homeDim.getSpawnPos();
                                //Oneironaut.LOGGER.info("Spawnpoint was null, using world spawn");
                            }
                            player.teleport(homeDim, spawnpoint.getX() * compressionFactor, spawnpoint.getY(), spawnpoint.getZ() * compressionFactor, player.getYaw(), player.getPitch());
                        } else {
                            //iterate to find the ground
                            double altitude = 321;
                            while (noosphere.getBlockState(new BlockPos(pos.getX(), altitude, pos.getZ())).isAir()){
                                altitude -= 1;
                                //don't go into the void
                                if (altitude < -64){
                                    altitude = 321;
                                    break;
                                }
                            }
                            compressionFactor = origin.getDimension().coordinateScale();
                            player.teleport(noosphere, pos.getX() * compressionFactor, altitude + 1, pos.getZ() * compressionFactor, player.getYaw(), player.getPitch());
                            if (altitude >= 321){
                                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 1200));
                            }
                        }
                    }
                }
            }
            //maintain wisps
            int radius = 8;
            List<Entity> nearby = world.getOtherEntities(null, new Box(pos.add(new Vec3i(-radius, -radius, -radius)), pos.add(new Vec3i(radius, radius, radius))));
            Iterator<Entity> iterNearby = nearby.iterator();
            Entity currentEntity = null;
            while (iterNearby.hasNext()){
                currentEntity = iterNearby.next();
                if (currentEntity.getType().equals(HexalEntities.TICKING_WISP)){
                    TickingWisp wisp = (TickingWisp) currentEntity;
                    if (wisp.getPos().isInRange(doublePos, radius) && (wisp.getCaster() != null) && (wisp.getMedia() < 1000 * MediaConstants.DUST_UNIT)){
                        int upkeep = wisp.getNormalCostPerTick();
                        if (!wisp.canScheduleCast()){
                            upkeep = wisp.getUntriggeredCostPerTick();
                        }
                        wisp.addMedia(upkeep);
                    }
                }
            }
        } else {
            //purple slipway thing
            Random rand = Random.create();
            Vec3d vec = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            int i = 0;
            while (i < 8){
                i++;
                /*int randombase = 18;
                int randomhelp = randombase * 2;
                int r = (106 + (rand.nextInt(randombase) - randomhelp)) * (256 * 256);
                int g = (31 + (rand.nextInt(randombase) - randomhelp)) * 256;
                int b = (210 + (rand.nextInt(randombase) - randomhelp));
                int color = (r + g + b);*/
                world.addParticle(
                        new ConjureParticleOptions(0x6a31d2, true),
                        (vec.x + 0.35 * rand.nextGaussian()),
                        (vec.y + 0.35 * rand.nextGaussian()),
                        (vec.z + 0.35 * rand.nextGaussian()),
                        0.0125 * (rand.nextDouble() - 0.5),
                        0.0125 * (rand.nextDouble() - 0.5),
                        0.0125 * (rand.nextDouble() - 0.5)
                );
            }
        }

        //world.setBlockState(pos.add(0, 1, 0), Blocks.ACACIA_FENCE.getDefaultState());
    }
}
