package net.oneironaut.casting.cell;

import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.iota.Vec3Iota;
import at.petrak.hexcasting.api.spell.mishaps.Mishap;
import at.petrak.hexcasting.api.spell.mishaps.MishapEntityTooFarAway;
import at.petrak.hexcasting.api.spell.mishaps.MishapLocationTooFarAway;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.oneironaut.MiscAPIKt;
import net.oneironaut.registry.OneironautBlockRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class OpCellUnify implements ICellSpell{
    public static final String[][] unifyPattern = {
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

    public OpCellUnify(String[][] rawPattern, String translationKey){
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

    public @NotNull Pair<Integer, @Nullable Mishap> evaluateConditions(CastingContext ctx, List<Iota> capturedArgs, Box bounds) {
        //Oneironaut.LOGGER.info("eval method sucessfully called");
        int cost = (int) ((bounds.getXLength() * bounds.getYLength() * bounds.getZLength()) * 0.25);
        Optional<Iota> boxCorner1Container = CellSpellManager.getOptionalIota(capturedArgs, 0, Vec3Iota.TYPE);
        Optional<Iota> boxCorner2Container = CellSpellManager.getOptionalIota(capturedArgs, 1, Vec3Iota.TYPE);
        if (boxCorner1Container.isPresent() && boxCorner2Container.isPresent()){
            Vec3d boxCorner1 = ((Vec3Iota) boxCorner1Container.get()).getVec3();
            Vec3d boxCorner2 = ((Vec3Iota) boxCorner2Container.get()).getVec3();
            Box box = new Box(boxCorner1, boxCorner2);
            for (Vec3d corner : MiscAPIKt.getBoxCorners(box)){
                if (!ctx.isVecInRange(corner)){
                    return new Pair<>(-1, new MishapLocationTooFarAway(corner, "too_far"));
                }
            }
            cost = (int) ((box.getXLength() * box.getYLength() * box.getZLength()) * 0.25);
        }
        return new Pair<>(cost, null);
    }
    public @Nullable Mishap execute(CastingContext ctx, List<Iota> capturedArgs, Box bounds, BlockPos corner) {
        //Oneironaut.LOGGER.info("execute method sucessfully called");
        Optional<Iota> boxCorner1Container = CellSpellManager.getOptionalIota(capturedArgs, 0, Vec3Iota.TYPE);
        Optional<Iota> boxCorner2Container = CellSpellManager.getOptionalIota(capturedArgs, 1, Vec3Iota.TYPE);
        Box boxToUnify = bounds;
        if (boxCorner1Container.isPresent() && boxCorner2Container.isPresent()){
            boxToUnify = new Box(((Vec3Iota) boxCorner1Container.get()).getVec3(), ((Vec3Iota) boxCorner2Container.get()).getVec3());
        }
        Vec3d lowerCorner = new Vec3d(boxToUnify.minX, boxToUnify.minY, boxToUnify.minZ);
        ServerWorld world = ctx.getWorld();
        for (int i = 0; i < bounds.getXLength(); i++){
            for (int j = 0; j < bounds.getYLength(); j++){
                for (int k = 0; k < bounds.getZLength(); k++){
                    Vec3d offset = new Vec3d(i,j,k);
                    BlockPos targetPos = new BlockPos(lowerCorner.add(offset));
                    if (world.getBlockState(targetPos).getBlock().equals(OneironautBlockRegistry.CELL.get())){
                        world.setBlockState(targetPos, OneironautBlockRegistry.MEDIA_GEL.get().getDefaultState());
                    }
                }
            }
        }
        return null;
    }

    public @NotNull Box getBoundingBox(){
        return this.bounds;
    }
}
