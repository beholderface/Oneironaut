package net.oneironaut.casting.cell;

import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.mishaps.Mishap;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class OpCellHeal implements ICellSpell{

    public static final String[][] healPattern = {
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
    public static final String[][] line = {
            {
                    "CCC"
            }
    };

    private final String[][] rawPattern;
    private List<BlockPos> pattern;// = new ArrayList<>();
    private final int cost = MediaConstants.CRYSTAL_UNIT * 5;
    private final Box bounds;
    private final String translationKey;

    public OpCellHeal(String[][] rawPattern, String translationKey){
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
    public int getCost(){
        return this.cost;
    }

    public @NotNull Pair<Integer, @Nullable Mishap> evaluateConditions(CastingContext ctx, List<Iota> args, Box bounds) {
        //Oneironaut.LOGGER.info("eval method sucessfully called");
        return new Pair<>(this.cost, null);
    }
    public @Nullable Mishap execute(CastingContext ctx, List<Iota> processedArgs, Box bounds, BlockPos corner) {
        //Oneironaut.LOGGER.info("execute method sucessfully called");
        ctx.getCaster().addStatusEffect(new StatusEffectInstance(StatusEffects.INSTANT_HEALTH, 1, 10));
        return null;
    }

    public @NotNull Box getBoundingBox(){
        return this.bounds;
    }
}
