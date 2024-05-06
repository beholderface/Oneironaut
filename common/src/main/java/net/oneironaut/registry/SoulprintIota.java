package net.oneironaut.registry;

import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.iota.IotaType;
import at.petrak.hexcasting.api.utils.HexUtils;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;


public class SoulprintIota extends Iota{
    public SoulprintIota(@NotNull Pair<UUID, String> payload){
        super(OneironautIotaTypeRegistry.UUID, payload);
    }

    @Override
    public boolean isTruthy() {
        return true;
    }

    protected boolean toleratesOther(Iota that) {
        if (that.getType().equals(this.type)){
            SoulprintIota other = (SoulprintIota) that;
            Pair<UUID, String> thisPayload = (Pair<UUID, String>) this.payload;
            Pair<UUID, String> thatPayload = (Pair<UUID, String>) other.payload;
            return thisPayload.getLeft().equals(thatPayload.getLeft());
        }
        return false;
    }

    public @NotNull NbtElement serialize() {
        var data = new NbtCompound();
        var payload = (Pair<UUID, String>) this.payload;
        data.putUuid("iota_uuid", payload.getLeft());
        data.putString("entity_name", payload.getRight());
        return data;
    }
    public @NotNull UUID getEntity(){
        return (UUID) this.payload;
    }
    public static IotaType<SoulprintIota> TYPE = new IotaType<>() {
        @Override
        public SoulprintIota deserialize(NbtElement tag, ServerWorld world) throws IllegalArgumentException {
            var ctag = HexUtils.downcast(tag, NbtCompound.TYPE);
            return new SoulprintIota(new Pair<UUID, String>(ctag.getUuid("iota_uuid"), ctag.getString("entity_name")));
        }

        @Override
        public Text display(NbtElement tag) {
            var ctag = HexUtils.downcast(tag, NbtCompound.TYPE);
            var name = ctag.getString("entity_name");
            return Text.translatable("hexcasting.iota.oneironaut:uuid.label", name);
        }
        @Override
        public int color() {
            return 0xff_7a63bc;
        }
    };
}
