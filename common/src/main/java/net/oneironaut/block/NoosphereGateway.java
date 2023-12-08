package net.oneironaut.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
//import net.oneironaut.MiscAPIKt.*;

import java.util.Iterator;

import static net.oneironaut.MiscAPIKt.stringToWorld;

public class NoosphereGateway extends Block {
    public NoosphereGateway(Settings settings){
        super(settings);
    }

    /*public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context){
        return VoxelShapes.cuboid(0.4f, 0.4f, 0.4f, 0.6f, 0.6f, 0.6f);
    }*/

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity){
        if (entity.isPlayer() && !world.isClient){
            ServerPlayerEntity player = (ServerPlayerEntity) entity;
            ServerWorld noosphere = null;
            ServerWorld origin = player.getWorld();
            ServerWorld current = origin;
            //Object[] regKeyArray = player.server.getWorldRegistryKeys().toArray();
            if (current.getRegistryKey().getValue().toString() != "oneironaut:noosphere"){
                for (ServerWorld serverWorld : player.server.getWorlds()) {
                    current = serverWorld;
                    if (current.getRegistryKey().getValue().toString() == "oneironaut:noosphere") {
                        noosphere = current;
                    }
                }
            } else {
                noosphere = current;
            }
            double compressionFactor;
            if (noosphere != null){
                if (noosphere == origin){
                    ServerWorld homeDim = stringToWorld(player.getSpawnPointDimension().getValue().toString(), player);
                    compressionFactor = 1 / homeDim.getDimension().coordinateScale();
                    BlockPos spawnpoint = player.getSpawnPointPosition();
                    player.teleport(homeDim, spawnpoint.getX() * compressionFactor, spawnpoint.getY(), spawnpoint.getZ() * compressionFactor, player.headYaw, player.prevPitch);
                } else {
                    compressionFactor = origin.getDimension().coordinateScale();
                    player.teleport(noosphere, pos.getX() * compressionFactor, 320 * compressionFactor, pos.getZ() * compressionFactor, player.headYaw, player.prevPitch);
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 1200));
                }
            }
            /*for (Object key : regKeyArray){
                if (((RegistryKey<World>) key).getValue().toString() == "oneironaut:noosphere"){
                    noosphere = ((RegistryKey<World>) key)

                }
            }*/
        }
    }
    /*@Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            player.sendMessage(Text.literal("AAAAAAAAAAAAAAA!"), false);
        }

        return ActionResult.SUCCESS;
    }*/

}
