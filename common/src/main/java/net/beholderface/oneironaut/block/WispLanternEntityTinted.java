package net.beholderface.oneironaut.block;

import at.petrak.hexcasting.api.misc.FrozenColorizer;
import at.petrak.hexcasting.common.particles.ConjureParticleOptions;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.beholderface.oneironaut.registry.OneironautBlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.beholderface.oneironaut.registry.OneironautBlockRegistry;
import net.beholderface.oneironaut.registry.OneironautItemRegistry;
import org.jetbrains.annotations.Nullable;

public class WispLanternEntityTinted extends BlockEntity {

    public WispLanternEntityTinted(BlockPos pos, BlockState state){
        super(OneironautBlockRegistry.WISP_LANTERN_ENTITY_TINTED.get(), pos, state);
    }

    private FrozenColorizer color = FrozenColorizer.DEFAULT.get();

    public void setColor(ItemStack item, PlayerEntity player){
        if (item.getItem() != Items.BARRIER){
            color = new FrozenColorizer(new ItemStack(item.getRegistryEntry()), player.getUuid());
        } else {
            color = IXplatAbstractions.INSTANCE.getColorizer(player);
        }
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

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    public void tick(World world, BlockPos pos, BlockState state){
        Random rand = world.random;
        if (world.isClient){
            Vec3d jarCenter = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5);
            //render a wisp-like thing
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
