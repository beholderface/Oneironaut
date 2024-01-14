package net.oneironaut.block;

import at.petrak.hexcasting.api.player.Sentinel;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.oneironaut.registry.OneironautBlockRegistry;

import java.util.List;

public class CellularControllerEntity extends BlockEntity {
    public CellularControllerEntity(BlockPos pos, BlockState state){
        super(OneironautBlockRegistry.CELLULAR_CONTROLLER_ENTITY.get(), pos, state);
    }

    public void tick(World world, BlockPos pos, BlockState state){
    }
}
