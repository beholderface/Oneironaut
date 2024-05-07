package net.oneironaut.registry;

import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.iota.IotaType;
import at.petrak.hexcasting.api.utils.HexUtils;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

//import java.awt.*;

public class PotionIota extends Iota{
    public PotionIota(@NotNull StatusEffect effect){
        super(OneironautIotaTypeRegistry.POTION, effect);
    }

    @Override
    public boolean isTruthy() {
        return true;
    }

    protected boolean toleratesOther(Iota that) {
        if (that.getType().equals(this.type)){
            PotionIota other = (PotionIota) that;
            return this.payload.equals(other.payload);
        }
        return false;
    }

    public @NotNull NbtElement serialize() {
        var data = new NbtCompound();
        var payload = (StatusEffect) this.payload;
        data.putString("potion_key", payload.getTranslationKey());
        return data;
    }
    public @NotNull StatusEffect getEffect(/*PotionIota iota*/){
        return (StatusEffect) this.payload;
    }
    public static IotaType<PotionIota> TYPE = new IotaType<>() {
        @Override
        public PotionIota deserialize(NbtElement tag, ServerWorld world) throws IllegalArgumentException {
            var ctag = HexUtils.downcast(tag, NbtCompound.TYPE);
            Iterator<StatusEffect> statusEffectIterator = Registry.STATUS_EFFECT.iterator();
            String potionKey = ctag.getString("potion_key");
            StatusEffect currentEffect = OneironautMiscRegistry.MISSING.get();
            while (statusEffectIterator.hasNext()){
                currentEffect = statusEffectIterator.next();
                if (currentEffect.getTranslationKey().equals(potionKey)){
                    break;
                }
            }
            return new PotionIota(currentEffect);
        }

        @Override
        public Text display(NbtElement tag) {
            var ctag = HexUtils.downcast(tag, NbtCompound.TYPE);
            var text = ctag.getString("potion_key");
            Text translatedName = Text.translatable(text);
            Style originalStyle = translatedName.getStyle();
            Style formattedStyle = switch (text){
                case "effect.oneironaut.detection_resistance" -> originalStyle.withColor(0xff55ff);
                case "effect.oneironaut.missing" -> originalStyle.withColor(0xaa0000).withBold(true);
                default -> originalStyle.withColor(0x0000aa);
            };
            return translatedName.copy().setStyle(formattedStyle);
        }
        @Override
        public int color() {
            return 0xff_5555FF;
        }
    };
}
