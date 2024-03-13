package net.oneironaut.casting.cell;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.iota.EntityIota;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.iota.NullIota;
import at.petrak.hexcasting.api.spell.iota.Vec3Iota;
import at.petrak.hexcasting.api.spell.mishaps.Mishap;
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota;
import at.petrak.hexcasting.api.spell.mishaps.MishapLocationTooFarAway;
import at.petrak.hexcasting.api.spell.mishaps.MishapNotEnoughArgs;
import com.mojang.datafixers.util.Pair;
import kotlin.Triple;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.oneironaut.MiscAPIKt;
import net.oneironaut.registry.OneironautBlockRegistry;
import net.oneironaut.registry.PotionIota;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OpCellCopyEffects implements ICellSpell{
    public static final String[][] copyPattern = {
            {
                    "   ",
                    "CCC",
                    "   "
            },
            {
                    "CCC",
                    "   ",
                    "CCC"
            },
            {
                    "0"
            }
    };

    private final String[][] rawPattern;
    private List<BlockPos> pattern;// = new ArrayList<>();
    //private final int cost = MediaConstants.CRYSTAL_UNIT * 5;
    private final Box bounds;
    private final String translationKey;

    public OpCellCopyEffects(String[][] rawPattern, String translationKey){
        this.rawPattern = rawPattern;
        this.translationKey  = translationKey;
        BlockPos lowerCorner = BlockPos.ORIGIN;
        int x = 0;
        int y = 0;
        int z = 0;
        for (BlockPos pos : CellSpellManager.stringsToPattern(this.getRawPattern())){
            if (pos.getX() > x){
                x = pos.getX();
            }
            if (pos.getY() > y){
                y = pos.getY();
            }
            if (pos.getZ() > z){
                z = pos.getZ();
            }
        }
        this.bounds = new Box(lowerCorner, new BlockPos(x + 1, y + 1, z + 1));
    }

    public String[][] getRawPattern() {
        return this.rawPattern;
    }

    public String getTranslationKey(){
        return this.translationKey;
    }

    public void initPattern(String[][] pattern){
        this.pattern = CellSpellManager.stringsToPattern(pattern);
    }

    public @NotNull List<BlockPos> getPattern() {
        return this.pattern;
    }

    public @NotNull Triple<Integer, @Nullable Mishap, List<Iota>> evaluateConditions(CastingContext ctx, List<Iota> capturedArgs, Box bounds) {
        //Oneironaut.LOGGER.info("eval method sucessfully called");
        int cost = (int) ((bounds.getXLength() * bounds.getYLength() * bounds.getZLength()) * 0.25);
        Optional<Iota> targetContainer = CellSpellManager.getOptionalIota(capturedArgs, 0, EntityIota.TYPE);
        Optional<Iota> effectContainer = CellSpellManager.getOptionalIota(capturedArgs, 1, PotionIota.TYPE);
        Optional<Iota> originContainer = CellSpellManager.getOptionalIota(capturedArgs, 2, EntityIota.TYPE);
        LivingEntity target = null;
        StatusEffect effect = null;
        LivingEntity origin = ctx.getCaster();
        if (targetContainer.isPresent()){
            EntityIota targetIota = ((EntityIota) targetContainer.get());
            if (targetIota.getEntity() instanceof LivingEntity liveTarget){
                target = liveTarget;
                if (effectContainer.isPresent()){
                    PotionIota effectIota = (PotionIota) effectContainer.get();
                    effect = effectIota.getEffect();
                }
                if (originContainer.isPresent()){
                    EntityIota originIota = ((EntityIota) originContainer.get());
                    if (originIota.getEntity() instanceof LivingEntity liveOrigin){
                        origin = liveOrigin;
                    }
                }
            } else {
                return new Triple<>(cost, new MishapInvalidIota(targetIota, 2, Text.translatable("hexcasting.mishap.invalid_value.class.entity.living")), capturedArgs);
            }
        } else {
            //TODO: replace this with a bespoke mishap
            return new Triple<>(cost, new MishapNotEnoughArgs(1, 0), capturedArgs);
        }
        List<Iota> processedArgs = new ArrayList<>();
        processedArgs.add(new EntityIota(target));
        processedArgs.add(effect == null ? new NullIota() : new PotionIota(effect));
        processedArgs.add(new EntityIota(origin));
        return new Triple<>(cost, null, processedArgs);
    }
    public @Nullable Mishap execute(CastingContext ctx, List<Iota> capturedArgs, Box bounds, BlockPos corner) {
        //TODO: actually make this do the thing
        return null;
    }

    public @NotNull Box getBoundingBox(){
        return this.bounds;
    }
}
