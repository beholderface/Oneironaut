package net.oneironaut.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.oneironaut.Oneironaut;

import javax.annotation.Nullable;

//import static net.oneironaut.MiscAPIKt.stringToWorld;
//import static net.oneironaut.MiscAPIKt.clientPlayertoServerPlayer;

public class NoosphereGateway extends BlockWithEntity{
    public NoosphereGateway(Settings settings){
        super(settings);
    }

    @Override
    @Nullable
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state){
        //Oneironaut.LOGGER.info("Creating blockentity.");
        return new NoosphereGateEntity(pos, state);
    }
    @Override
    public BlockRenderType getRenderType(BlockState state){
        return BlockRenderType.MODEL;
    }
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        //if (type != OneironautThingRegistry.NOOSPHERE_GATE_ENTITY.get()) return null;
        return (_world, _pos, _state, _be) -> ((NoosphereGateEntity)_be).tick(_world, _pos, _state);
    }

    /*public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context){
        return VoxelShapes.cuboid(0.4f, 0.4f, 0.4f, 0.6f, 0.6f, 0.6f);
    }*/

    /*@Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity){
        if (entity.isPlayer() && world.isClient){
            ServerPlayerEntity player = clientPlayertoServerPlayer(entity);
            ServerWorld noosphere = null;
            ServerWorld origin = null;
            if (player != null){
                origin = player.getWorld();
                ServerWorld current = origin;
                //Object[] regKeyArray = player.server.getWorldRegistryKeys().toArray();
                if (current.getRegistryKey().getValue().toString() != "oneironaut:noosphere"){
                    for (ServerWorld serverWorld : player.getServer().getWorlds()) {
                        current = serverWorld;
                        if (current.getRegistryKey().getValue().toString() == "oneironaut:noosphere") {
                            noosphere = current;
                        }
                    }
                } else {
                    noosphere = current;
                }
            }

            double compressionFactor;
            if (noosphere != null && origin != null){
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
            *//*for (Object key : regKeyArray){
                if (((RegistryKey<World>) key).getValue().toString() == "oneironaut:noosphere"){
                    noosphere = ((RegistryKey<World>) key)

                }
            }*//*
        }
    }*/
    /*@Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            player.sendMessage(Text.literal("AAAAAAAAAAAAAAA!"), false);
        }

        return ActionResult.SUCCESS;
    }*/

}

