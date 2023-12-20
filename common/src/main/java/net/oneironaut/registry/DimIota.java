package net.oneironaut.registry;

import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.iota.IotaType;
import at.petrak.hexcasting.api.utils.HexUtils;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.*;

//import java.awt.*;

public class DimIota extends Iota{
    public DimIota(@NotNull String dim){
        super(OneironautIotaTypeRegistry.DIM, dim);
    }

    public String getIotaType(){
        return (String) this.payload;
    }

    /*public NbtElement getKey(){
        var ctag = HexUtils.downcast(this.payload, NbtCompound.TYPE);
        return (RegistryKey<World>) ctag.get("dim_key");
    }*/

    @Override
    public boolean isTruthy() {
        return true;
    }

    protected boolean toleratesOther(Iota that) {
        if (that.getType().equals(this.type)){
            DimIota other = (DimIota) that;
            return this.payload.equals(other.payload);
        }
        return false;
    }

    public @NotNull NbtElement serialize() {
        var data = new NbtCompound();
        var payload = this.payload;
        data.putString("dim_key", (String) payload);
        return data;
    }
    public static IotaType<DimIota> TYPE = new IotaType<>() {
        @Override
        public DimIota deserialize(NbtElement tag, ServerWorld world) throws IllegalArgumentException {
            var ctag = HexUtils.downcast(tag, NbtCompound.TYPE);
            return new DimIota(ctag.getString("dim_key"));
        }

        @Override
        public Text display(NbtElement tag) {
            //return Text.translatable("text.oneironaut.dimiota.name");
            var ctag = HexUtils.downcast(tag, NbtCompound.TYPE);
            var id = ctag.getString("dim_key");
            //var id = world.getValue().toString();
            String formatCode = switch (id) {
                case "minecraft:overworld" -> "§2";
                case "minecraft:the_nether" -> "§4";
                case "minecraft:the_end" -> "§e";
                case "oneironaut:noosphere" -> "§5§l";
                default -> "§9";
            };
            return Text.of(formatCode+id);
            //return Text.of("bees");
            //return Text.of(ctag.get("dim_ID").toString());
        }
        @Override
        public int color() {
            return 0xff_5555FF;
        }
    };
}
