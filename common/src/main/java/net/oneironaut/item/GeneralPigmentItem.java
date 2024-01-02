package net.oneironaut.item;

import at.petrak.hexcasting.api.addldata.ADColorizer;
import at.petrak.hexcasting.api.item.ColorizerItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class GeneralPigmentItem extends Item implements ColorizerItem {
    public static int[] colors_noosphere = {0x7A00C1, 0x46006F, 0x120049, 0x3200CB, 0x6724DD};
    public static int[] colors_flame = {0xff0000, 0xff4600, 0xff8400, 0xffb300, 0x003994, 0xff0000, 0xff4600, 0xff8400, 0xffb300, 0x003994, 0xffffff};
    public static int[] colors_echo = {0x034150, 0x0a5060, 0x034150, 0x0a5060, 0x009295, 0x29dfeb};
    private final int[] colors;
    public GeneralPigmentItem(Settings settings, int[] colors) {
        super(settings);
        this.colors = colors;
    }

    @Override
    public int color(ItemStack stack, UUID owner, float time, Vec3d position) {
        return ADColorizer.morphBetweenColors(this.colors, new Vec3d(0.1, 0.1, 0.1), time / 20 / 20, position);
    }
    /*@Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

    }*/
}
