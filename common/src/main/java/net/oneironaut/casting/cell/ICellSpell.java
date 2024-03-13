package net.oneironaut.casting.cell;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.mishaps.Mishap;
import com.mojang.datafixers.util.Pair;
import kotlin.Triple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ICellSpell {
    //this should never be called outside of CellSpellManager.
    //if you do that anyway, your computer will go back in time and eat your elbowcaps.
    void initPattern(String[][] pattern);
    @NotNull List<BlockPos> getPattern();
    //the returned value should be the media cost, in raw units.
    @NotNull Triple<Integer, @Nullable Mishap, List<Iota>> evaluateConditions(CastingContext ctx, List<Iota> capturedArgs, Box bounds);
    @Nullable Mishap execute(CastingContext ctx, List<Iota> capturedArgs, Box bounds, BlockPos corner);
    @NotNull Box getBoundingBox();
    /*
    pattern definition uses syntax very similar to patchouli multiblock definition,
    except you can designate indices that a given plane is the same as, to streamline the process.
    top-level array: y axis, starting from 0.
    the next level of array is the Z axis, and the final strings are the X axis.
    it seems it doesn't like really small patterns, and I don't know why.
    it doesn't work with a 3x1x1 pattern, but 3x3x3 works fine, so I guess the minimum is somewhere in there ¯\_(ツ)_/¯
    */
    String[][] getRawPattern();
    String getTranslationKey();
}
