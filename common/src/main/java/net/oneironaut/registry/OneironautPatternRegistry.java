package net.oneironaut.registry;

import at.petrak.hexcasting.api.PatternRegistry;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.spell.Action;
import at.petrak.hexcasting.api.spell.math.HexDir;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import kotlin.Triple;
import net.oneironaut.casting.patterns.OpGetDim;
import net.minecraft.util.Identifier;
import net.oneironaut.casting.patterns.spells.OpSplatoon;
import net.oneironaut.casting.patterns.spells.great.OpDimTeleport;
import net.oneironaut.casting.patterns.spells.great.OpInfuseMedia;
import net.oneironaut.casting.patterns.spells.great.OpSwapSpace;

import java.util.ArrayList;
import java.util.List;

import static net.oneironaut.Oneironaut.id;

public class OneironautPatternRegistry {
    public static List<Triple<HexPattern, Identifier, Action>> PATTERNS = new ArrayList<>();
    public static List<Triple<HexPattern, Identifier, Action>> PER_WORLD_PATTERNS = new ArrayList<>();
    // IMPORTANT: be careful to keep the registration calls looking like this, or you'll have to edit the hexdoc pattern regex.
    //public static HexPattern CONGRATS = registerPerWorld(HexPattern.fromAngles("eed", HexDir.WEST), "congrats", new OpCongrats());
    //public static HexPattern SIGNUM = register(HexPattern.fromAngles("edd", HexDir.NORTH_WEST), "signum", new OpSignum());
    public static HexPattern GETDIM_1 = register(HexPattern.fromAngles("wqwqwqwqwqwaeqqe", HexDir.WEST), "getdim1", new OpGetDim(false, MediaConstants.DUST_UNIT / 100));
    public static HexPattern GETDIM_2 = register(HexPattern.fromAngles("wqwqwqwqwqwaqeeq", HexDir.WEST), "getdim2", new OpGetDim(true, MediaConstants.DUST_UNIT / 10));
    public static HexPattern PAINT_CONJURED = register(HexPattern.fromAngles("eqdweeqdwweeqddqdwwwdeww", HexDir.WEST), "paintconjured", new OpSplatoon());
    public static HexPattern DIM_TELEPORT = registerPerWorld(HexPattern.fromAngles("qeewwwweeqeqeewwwweeqdqqdwwwdqeqdwwwdqdadwwdqdwwddadaqadaawww", HexDir.NORTH_EAST), "dimteleport", new OpDimTeleport());
    public static HexPattern INFUSE_MEDIA = registerPerWorld(HexPattern.fromAngles("wwaqqqqqeqqqwwwqqeqqwwwqqweqadadadaqeqeqadadadaqe", HexDir.EAST), "infusemedia", new OpInfuseMedia());
    public static HexPattern SWAP_SPACE = registerPerWorld(HexPattern.fromAngles("wqqqwwwwwqqqwwwqdaqadwqqwdaqadweqeqqqqeqeqaqeqedeqeqa", HexDir.EAST), "swapspace", new OpSwapSpace());
    public static void init() {
        try {
            for (Triple<HexPattern, Identifier, Action> patternTriple : PATTERNS) {
                PatternRegistry.mapPattern(patternTriple.getFirst(), patternTriple.getSecond(), patternTriple.getThird());
            }
            for (Triple<HexPattern, Identifier, Action> patternTriple : PER_WORLD_PATTERNS) {
                PatternRegistry.mapPattern(patternTriple.getFirst(), patternTriple.getSecond(), patternTriple.getThird(), true);
            }
        } catch (PatternRegistry.RegisterPatternException e) {
            e.printStackTrace();
        }
    }

    private static HexPattern register(HexPattern pattern, String name, Action action) {
        Triple<HexPattern, Identifier, Action> triple = new Triple<>(pattern, id(name), action);
        PATTERNS.add(triple);
        return pattern;
    }

    private static HexPattern registerPerWorld(HexPattern pattern, String name, Action action) {
        Triple<HexPattern, Identifier, Action> triple = new Triple<>(pattern, id(name), action);
        PER_WORLD_PATTERNS.add(triple);
        return pattern;
    }
}
