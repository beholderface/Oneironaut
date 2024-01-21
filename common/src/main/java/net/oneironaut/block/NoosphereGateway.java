package net.oneironaut.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.oneironaut.Oneironaut;

import javax.annotation.Nullable;
import java.util.Map;

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

    @Override
    public PistonBehavior getPistonBehavior(BlockState pState) {
        return PistonBehavior.BLOCK;
    }

}

