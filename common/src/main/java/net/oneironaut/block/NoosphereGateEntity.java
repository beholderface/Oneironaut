package net.oneironaut.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.oneironaut.registry.OneironautThingRegistry;

public class NoosphereGateEntity extends BlockEntity {
    public NoosphereGateEntity(BlockPos pos, BlockState state) {
        super(OneironautThingRegistry.NOOSPHERE_GATE_ENTITY, pos, state);
    }
    public void tick(World world, BlockPos pos, BlockState state){
        //if ((world.getServer().getTicks() % 5) == 0){
            Vec3d doublePos = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            PlayerEntity possiblePassenger = world.getClosestPlayer(doublePos.x, doublePos.y, doublePos.z, 2.0, false);
            Vec3d passengerMidpoint = new Vec3d(possiblePassenger.getX(), possiblePassenger.getY() + (possiblePassenger.getHeight() / 2), possiblePassenger.getZ());
            if (passengerMidpoint.isInRange(doublePos, 1)){
                possiblePassenger.sendMessage(Text.of("teleporting"));
            }
        //}
        world.setBlockState(pos.add(0, 1, 0), Blocks.ACACIA_FENCE.getDefaultState());
    }
}
