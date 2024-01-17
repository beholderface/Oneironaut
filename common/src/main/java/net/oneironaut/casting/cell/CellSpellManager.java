package net.oneironaut.casting.cell;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.iota.IotaType;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.oneironaut.Oneironaut;
import net.oneironaut.block.CellEntity;
import net.oneironaut.registry.OneironautBlockRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CellSpellManager {
    private static final Map<Identifier, ICellSpell> cellSpells = new HashMap<>();

    public static void registerCellSpell(String[][] pattern, Identifier id, ICellSpell spell) throws IllegalArgumentException{
        //iterate over registered patterns and make sure there are no duplicate identifiers or patterns
        for (Identifier checkedID : cellSpells.keySet()){
            if (checkedID.equals(id) || Arrays.deepEquals(cellSpells.get(checkedID).getRawPattern(), pattern)){
                throw new IllegalArgumentException();
            }
        }
        cellSpells.put(id, spell);
        spell.initPattern(pattern);
        Oneironaut.LOGGER.info("Registered cell pattern " + spell.getPattern().toString() + " ("+ spell.getPattern().size() +" blocks) with identifier " + id.toString());
    }

    public static ICellSpell getCellSpell(Identifier id){
        return cellSpells.get(id);
    }

    public static List<BlockPos> stringsToPattern(String[][] stringPattern){
        int currentLayer = 0;
        List<BlockPos> output = new ArrayList<>();
        for (int l = 0; l < stringPattern.length; l++){
            //check for layer indicator, and set the one to check to the indicated one if present
            try {
                currentLayer = Integer.parseInt(stringPattern[l][0]);
            } catch (NumberFormatException exception){
                currentLayer = l;
            }
            String[] currentStrings = stringPattern[currentLayer];
            for (int m = 0; m < currentStrings.length; m++){
                String str = currentStrings[m];
                for (int n = 0; n < str.length(); n++){
                    char currentChar = str.charAt(n);
                    if (currentChar == 'c' || currentChar == 'C'){
                        //I did not design the thing well, thus the out-of-order alphabet
                        output.add(new BlockPos(n, l, m));
                    }
                }
            }
        }
        return output;
    }

    public static @Nullable Pair<BlockPos, ICellSpell> findPattern(CastingContext ctx, Box bounds){
        World world = ctx.getWorld();
        BlockPos lowerCorner = new BlockPos(bounds.minX, bounds.minY, bounds.minZ);
        //iterate over registered patterns and find ones which can fit in the supplied box
        List<ICellSpell> possibleSpells = new ArrayList<>();
        for (ICellSpell spell : cellSpells.values()){
            Box spellBox = spell.getBoundingBox();
            boolean canFit = boxCanFit(bounds, spellBox);
            if (canFit){
                possibleSpells.add(spell);
                Oneironaut.LOGGER.info("Box for " + spell.getTranslationKey() + " can fit in checked zone.");
            } else {
                Oneironaut.LOGGER.info("Box for " + spell.getTranslationKey() + " cannot fit in checked zone.");
            }
        }
        for (ICellSpell spell : possibleSpells){
            Oneironaut.LOGGER.info("Checking box for " + spell.getTranslationKey());
            Box spellBox = spell.getBoundingBox();
            List<BlockPos> foundPattern = null;
            //iterate over all possible positions for the pattern box
            int x = 0, y = 0, z = 0, i = 1;
            double x1 = bounds.getXLength(), y1 = bounds.getYLength(), z1 = bounds.getZLength();
            double x2 = spellBox.getXLength(), y2 = spellBox.getYLength(), z2 = spellBox.getZLength();
            Oneironaut.LOGGER.info("{} - {}, {} - {}, {} - {}", x1, x2, y1, y2, z1, z2);
            for (x = 0; x </*= (x1) - */x2; x++, i++){
                for (y = 0; y </*= (y1) - */y2; y++, i++){
                    for (z = 0; z </*= (z1) - */z2; z++, i++){
                        foundPattern = cellsInBox(world, spellBox.offset(lowerCorner.add(x, y, z)));
                        Oneironaut.LOGGER.info("Pattern tested on iteration " + i +": " + foundPattern.toString() + " (" + foundPattern.size() + " blocks)");
                        if (foundPattern.equals(spell.getPattern())){
                            return new Pair<>(lowerCorner.add(x,y,z), spell);
                        }
                    }
                }
            }
        }
        return null;
    }

    public static boolean boxCanFit(Box outer, Box inner){
        Box worldOriginOuter = new Box(0, 0, 0, outer.getXLength() + 1, outer.getYLength() + 1,
                outer.getZLength() + 1).expand(0.01);
        Vec3d innerCorner = new Vec3d(inner.getXLength(), inner.getYLength(), inner.getZLength());
        //Oneironaut.LOGGER.info("Checking if box " + worldOriginOuter.toString() + " contains " + innerCorner.toString());
        return worldOriginOuter.contains(innerCorner);
    }

    private static List<BlockPos> cellsInBox(World world, Box bounds){
        List<BlockPos> output = new ArrayList<>();
        BlockPos lowerCorner = new BlockPos(bounds.minX, bounds.minY, bounds.minZ);
        BlockPos upperCorner = new BlockPos(bounds.maxX, bounds.maxY, bounds.maxZ);
        Oneironaut.LOGGER.info("lower corner: " + lowerCorner.toShortString());
        Oneironaut.LOGGER.info("upper corner: " + upperCorner.toShortString());
        for (int y = 0; y < bounds.getYLength(); y++){
            for (int z = 0; z < bounds.getZLength(); z++){
                for (int x = 0; x < bounds.getXLength(); x++){
                    BlockPos checkedPos = lowerCorner.add(x, y, z);
                    if (world.getBlockState(checkedPos).getBlock().equals(OneironautBlockRegistry.CELL.get())){
                        Optional<CellEntity> hopefulCell = world.getBlockEntity(checkedPos, OneironautBlockRegistry.CELL_ENTITY.get());
                        if (hopefulCell.isPresent()){
                            if (hopefulCell.get().getVerified()){
                                output.add(checkedPos.subtract(lowerCorner));
                                //Oneironaut.LOGGER.info(checkedPos.toShortString());
                            }
                        }
                    }
                }
            }
        }
        return output;
    }
    public static Optional<Iota> getOptionalIota(List<Iota> args, int index, IotaType<?> type){
        if (args.size() - 1 >= index){
            Iota iota = args.get(index);
            if (iota.getType().equals(type)){
                return Optional.of(iota);
            }
        }
        return Optional.empty();
    }
}
