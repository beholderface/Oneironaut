package net.beholderface.oneironaut.casting.lichdom;

import at.petrak.hexcasting.api.addldata.ADMediaHolder;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.utils.NBTHelper;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LichData implements ADMediaHolder {
    private long lastRevive = Long.MIN_VALUE;
    private List<Iota> deathHex = null;
    private List<Iota> passiveHex = null;
    private long internalMedia = 0;
    private long abilityCooldown = 0;
    public static final long maxInternalMedia = MediaConstants.QUENCHED_SHARD_UNIT * 64;
    public LichData(){}
    public LichData(List<Iota> deathHex, List<Iota> passiveHex, long lastRevive, long storedMedia, long abilityCooldown){
        this.deathHex = deathHex;
        this.passiveHex = passiveHex;
        this.lastRevive = lastRevive;
        this.internalMedia = storedMedia;
        this.abilityCooldown = abilityCooldown;
    }

    public static final String DEATH_HEX_KEY = "deathhex";
    public static final String PASSIVE_HEX_KEY = "passivehex";
    public static final String REVIVE_KEY = "revivetimestamp";
    public static final String MEDIA_KEY = "storedmedia";
    public static final String COOLDOWN_KEY = "abilitycooldown";
    public NbtCompound serialize(){
        NbtCompound output = new NbtCompound();
        output.putLong(MEDIA_KEY, this.internalMedia);
        output.putLong(REVIVE_KEY, this.lastRevive);
        NbtList deathHex = new NbtList();
        if (this.deathHex != null){
            for (Iota iota : this.deathHex){
                deathHex.add(IotaType.serialize(iota));
            }
        }
        NBTHelper.putList(output, DEATH_HEX_KEY, deathHex);
        NbtList passiveHex = new NbtList();
        if (this.passiveHex != null){
            for (Iota iota : this.passiveHex){
                passiveHex.add(IotaType.serialize(iota));
            }
        }
        NBTHelper.putList(output, PASSIVE_HEX_KEY, passiveHex);
        return output;
    }
    @Nullable
    public static LichData deserialize(NbtCompound data, ServerWorld world){
        try {
            long media = data.getLong(MEDIA_KEY);
            long timestamp = data.getLong(REVIVE_KEY);
            long abilityCooldown = data.getLong(COOLDOWN_KEY);
            NbtList deathHexNbt = NBTHelper.getList(data, DEATH_HEX_KEY, NbtElement.COMPOUND_TYPE);
            assert deathHexNbt != null;
            List<Iota> deathHex = new ArrayList<>();
            for (NbtElement element : deathHexNbt){
                NbtCompound tag = NBTHelper.getAsCompound(element);
                deathHex.add(IotaType.deserialize(tag, world));
            }
            NbtList passiveHexNbt = NBTHelper.getList(data, DEATH_HEX_KEY, NbtElement.COMPOUND_TYPE);
            assert passiveHexNbt != null;
            List<Iota> passiveHex = new ArrayList<>();
            for (NbtElement element : passiveHexNbt){
                NbtCompound tag = NBTHelper.getAsCompound(element);
                passiveHex.add(IotaType.deserialize(tag, world));
            }
            return new LichData(deathHex, passiveHex, timestamp, media, abilityCooldown);
        } catch (Exception ignored){
            //deserialization failed
            return null;
        }
    }

    public long getLastRevive() {
        return lastRevive;
    }

    public long getAbilityCooldown() {
        return abilityCooldown;
    }
    public boolean isOnCooldown(){
        return abilityCooldown > 0;
    }

    public void setAbilityCooldown(long abilityCooldown) {
        this.abilityCooldown = abilityCooldown;
    }
    public void adjustAbilityCooldown(long delta){
        this.abilityCooldown += delta;
    }

    public List<Iota> getPassiveHex() {
        return passiveHex;
    }
    public void setPassiveHex(List<Iota> hex){
        this.passiveHex = hex;
    }

    public List<Iota> getDeathHex() {
        return deathHex;
    }

    public void setDeathHex(List<Iota> deathHex) {
        this.deathHex = deathHex;
    }

    @Override
    public long getMedia() {
        return internalMedia;
    }

    @Override
    public long getMaxMedia() {
        return maxInternalMedia;
    }

    @Override
    public void setMedia(long media) {
        this.internalMedia = media;
    }

    @Override
    public boolean canRecharge() {
        return true;
    }

    @Override
    public boolean canProvide() {
        return false;
    }

    @Override
    public int getConsumptionPriority() {
        return 0;
    }

    @Override
    public boolean canConstructBattery() {
        return false;
    }
}
