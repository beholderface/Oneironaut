package net.beholderface.oneironaut.block;

import at.petrak.hexcasting.api.misc.FrozenColorizer;
import net.beholderface.oneironaut.Oneironaut;
import net.beholderface.oneironaut.registry.OneironautBlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.beholderface.oneironaut.MiscAPIKt;
import net.beholderface.oneironaut.Oneironaut;
import net.beholderface.oneironaut.registry.OneironautBlockRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CellEntity extends BlockEntity {

    /*
    * the plan is to eventually make it act as a 3D cellular automaton, with rule B6/S567 as mentioned here https://conwaylife.com/wiki/Three-dimensional_cellular_automaton
    * will produce plasmodial psyche, and something else that I haven't decided
    */
    public final Map<BlockPos, BlockState> neighborMap;
    public BlockPos initialPos;
    private Boolean verified;
    public CellEntity(BlockPos pos, BlockState state){
        super(OneironautBlockRegistry.CELL_ENTITY.get(), pos, state);
        this.initialPos = this.pos;
        World world = this.world;
        this.neighborMap = new HashMap<>();
        if (world != null){
            for(BlockPos neighborPos : getNeighbors(this.pos)){
                neighborMap.put(neighborPos, world.getBlockState(neighborPos));
            }
        }
        this.verified = false;
    }

    public void tick(World world, BlockPos pos, BlockState state){
        if (this.initialPos == null){
            this.initialPos = pos;
        }
        removeIfMoved();
    }

    public boolean updateNeighborMap(){
        if (removeIfMoved() || this.world == null){
            return false;
        }
        for (BlockPos neighborPos : this.neighborMap.keySet()){
            neighborMap.put(neighborPos, this.world.getBlockState(neighborPos));
        }
        return true;
    }

    private boolean removeIfMoved(){
        /*
        * I am intentionally allowing you to move it to the same coordinates in other dimensions, because that is complex
        * and/or expensive enough to offset the value of the exploit, IMO.
        */
        if (!this.initialPos.equals(this.pos) && world != null){
            Oneironaut.LOGGER.info("position mismatch: initial pos " + this.initialPos.toShortString() + " is not " + this.pos.toShortString());
            world.removeBlockEntity(this.pos);
            world.removeBlock(this.pos, false);
            return true;
        }
        return false;
    }

    private static List<BlockPos> getNeighbors(BlockPos pos){
        return MiscAPIKt.getPositionsInCuboid(pos.add(-1,-1,-1), pos.add(1, 1, 1), pos);
    }

    //there's something wrong here that causes /setblock and similar to error when you tell it to produce one with empty NBT, but I have no idea why.
    //the error message is not informative in the slightest
    @Override
    public void writeNbt(NbtCompound nbt){
        if (this.initialPos == null){
            this.initialPos = this.pos;
        }
        if (this.verified == null){
            this.verified = false;
        }
        nbt.putIntArray("initialPos", new int[]{this.initialPos.getX(),this.initialPos.getY(),this.initialPos.getZ()});
        nbt.putBoolean("verified", this.verified);
    }
    @Override
    public void readNbt(NbtCompound nbt){
        super.readNbt(nbt);
        int[] posArray = nbt.getIntArray("initialPos");
        this.initialPos = posArray == null ? this.pos : new BlockPos(posArray[0],posArray[1],posArray[2]);
        this.verified = nbt.getBoolean("verified");
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

    public boolean getVerified(){
        return this.verified;
    }
    public void setVerified(boolean verification){
        this.verified = verification;
    }
}
