package net.oneironaut.casting.cell;

import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.mishaps.Mishap;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import net.oneironaut.Oneironaut;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class OpCellExplosion implements ICellSpell{
    public static final String[][] explosionPattern = {{
            "     ",
            "  C  ",
            " C C ",
            "  C  ",
            "     ",
    }, {
            "  C  ",
            " C C ",
            "C   C",
            " C C ",
            "  C  ",
    }, {
        //so, this "plane" would be the same as the above one
        "1"
    }, {"1" }, {"1" }, {"1" }, {"1" }, {"1" }, {
        //and this one is the same as the first
        "0"
    }};

    private final String[][] rawPattern;
    private List<BlockPos> pattern;// = new ArrayList<>();
    private final int cost = MediaConstants.CRYSTAL_UNIT * 5;
    private final Box bounds;
    private final String translationKey;

    public OpCellExplosion(String[][] rawPattern, String translationKey){
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
        Vec3d targetPoint = ctx.getCaster().getPos().add(ctx.getCaster().getEyePos()).multiply(0.5);
        ctx.getWorld().createExplosion(ctx.getCaster(), targetPoint.x, targetPoint.y, targetPoint.z, 20, false, Explosion.DestructionType.BREAK);
        return null;
    }

    public @NotNull Box getBoundingBox(){
        return this.bounds;
    }
}
