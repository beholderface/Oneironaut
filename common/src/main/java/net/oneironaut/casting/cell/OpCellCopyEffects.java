package net.oneironaut.casting.cell;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.iota.EntityIota;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.iota.NullIota;
import at.petrak.hexcasting.api.spell.iota.Vec3Iota;
import at.petrak.hexcasting.api.spell.mishaps.*;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import com.mojang.datafixers.util.Pair;
import kotlin.Triple;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.oneironaut.MiscAPIKt;
import net.oneironaut.casting.mishaps.MishapCellMissingRequirement;
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
                    "  C  ",
                    " C C ",
                    "C   C",
                    " C C ",
                    "  C  "
            },
            {
                    "  C  ",
                    "     ",
                    "C   C",
                    "     ",
                    "  C  "
            },
            {
                    "  C  ",
                    "  C  ",
                    "CC CC",
                    "  C  ",
                    "  C  "
            },
            {
                    "1"
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

    private static int copyCostCalculation(StatusEffectInstance effect, CastingContext ctx, LivingEntity target){
        if (effect != null){
            return 1;
        }
        return 0;
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
                if (!ctx.isEntityInRange(target)){
                    return new Triple<>(cost, new MishapEntityTooFarAway(target), capturedArgs);
                }
                if (effectContainer.isPresent()){
                    PotionIota effectIota = (PotionIota) effectContainer.get();
                    effect = effectIota.getEffect();
                }
                if (originContainer.isPresent()){
                    EntityIota originIota = ((EntityIota) originContainer.get());
                    if (originIota.getEntity() instanceof LivingEntity liveOrigin){
                        origin = liveOrigin;
                        if (!ctx.isEntityInRange(origin)){
                            return new Triple<>(cost, new MishapEntityTooFarAway(origin), capturedArgs);
                        }
                    } else {
                        return new Triple<>(cost, new MishapInvalidIota(targetIota, 0, Text.translatable("hexcasting.mishap.invalid_value.class.entity.living")), capturedArgs);
                    }
                    if (origin.equals(target)){
                        return new Triple<>(cost, MishapBadEntity.of(target, "oneironaut:requiresdifferententities"), capturedArgs);
                    }
                }
            } else {
                return new Triple<>(cost, new MishapInvalidIota(targetIota, 2, Text.translatable("hexcasting.mishap.invalid_value.class.entity.living")), capturedArgs);
            }
        } else {
            return new Triple<>(cost, new MishapCellMissingRequirement("hexcasting.mishap.invalid_value.class.entity.living"), capturedArgs);
        }
        if (effect == null){
            var effectsToCopy = origin.getActiveStatusEffects();
            for (StatusEffectInstance effectInstance : effectsToCopy.values()){
                cost += copyCostCalculation(effectInstance, ctx, target);
            }
        } else {
            cost += copyCostCalculation(origin.getStatusEffect(effect), ctx, target);
        }
        List<Iota> processedArgs = new ArrayList<>();
        processedArgs.add(new EntityIota(target));
        processedArgs.add(effect == null ? new NullIota() : new PotionIota(effect));
        processedArgs.add(new EntityIota(origin));
        return new Triple<>(cost, null, processedArgs);
    }
    public @Nullable Mishap execute(CastingContext ctx, List<Iota> capturedArgs, Box bounds, BlockPos corner) {
        Iota maybeEffect = capturedArgs.get(1);
        LivingEntity target = (LivingEntity) ((EntityIota) capturedArgs.get(0)).getEntity();
        StatusEffect effect = maybeEffect.getType() == PotionIota.TYPE ? ((PotionIota) maybeEffect).getEffect() : null;
        LivingEntity origin = (LivingEntity) ((EntityIota) capturedArgs.get(2)).getEntity();
        if (effect == null) {
            for (StatusEffectInstance effectInstance : origin.getStatusEffects()){
                target.addStatusEffect(effectInstance, ctx.getCaster());
            }
        } else {
            target.addStatusEffect(origin.getStatusEffect(effect), ctx.getCaster());
        }
        return null;
    }

    public @NotNull Box getBoundingBox(){
        return this.bounds;
    }
}
