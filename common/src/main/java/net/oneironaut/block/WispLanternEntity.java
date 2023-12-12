package net.oneironaut.block;

import at.petrak.hexcasting.api.misc.FrozenColorizer;
import at.petrak.hexcasting.common.particles.ConjureParticleOptions;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import at.petrak.hexcasting.common.lib.HexItems;
import net.oneironaut.registry.OneironautThingRegistry;

import java.util.UUID;

public class WispLanternEntity extends BlockEntity {

    public WispLanternEntity(BlockPos pos, BlockState state){
        super(OneironautThingRegistry.WISP_LANTERN_ENTITY, pos, state);
    }

    private FrozenColorizer color = FrozenColorizer.DEFAULT.get();

    public void setColor(ItemStack item, PlayerEntity player){
        color = new FrozenColorizer(new ItemStack(item.getRegistryEntry()), player.getUuid());
    }

    @Override
    public void writeNbt(NbtCompound nbt){
        nbt.put("color", color.serializeToNBT());
    }

    @Override
    public void readNbt(NbtCompound nbt){
        super.readNbt(nbt);
        color = FrozenColorizer.fromNBT(nbt.getCompound("color"));
    }

    public void tick(World world, BlockPos pos, BlockState state){
        Random rand = net.minecraft.util.math.random.Random.create();
        if (world.isClient){
            Vec3d jarCenter = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5);
            //render a wisp-like thing
            for (int i = 0; i < 4; i++){
                world.addParticle(
                        new ConjureParticleOptions(color.getColor(rand.nextInt(), jarCenter), true),
                        jarCenter.x + ((rand.nextGaussian() - 0.5) / 50),
                        jarCenter.y,
                        jarCenter.z + ((rand.nextGaussian() - 0.5) / 50),
                        (rand.nextDouble() - 0.5) / 100,
                        0.02 * (rand.nextDouble() - 0.5) / 100,
                        (rand.nextDouble() - 0.5) / 100
                );
            }
        }
    }
}
