package net.beholderface.oneironaut.fabric.mixin;

import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.utils.MediaHelper;
import at.petrak.hexcasting.common.items.ItemLens;
import at.petrak.hexcasting.common.lib.HexItems;
import net.beholderface.oneironaut.casting.lichdom.LichData;
import net.beholderface.oneironaut.casting.lichdom.LichdomManager;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static at.petrak.hexcasting.common.items.magic.ItemMediaHolder.HEX_COLOR;
import static net.beholderface.oneironaut.item.BottomlessCastingItem.DUST_AMOUNT;
import static net.beholderface.oneironaut.item.BottomlessCastingItem.PERCENTAGE;

@Mixin(Item.class)
public class LensMediaTooltipMixin{
    @Unique
    private static final String MEDIA_TAG = "lichmedia";
    @Inject(method = "inventoryTick(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;IZ)V",
    at = @At(value = "HEAD"))
    public void putMediaValue(ItemStack stack, World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if (stack.getItem() != HexItems.SCRYING_LENS || world.isClient){
            return;
        }
        if (entity instanceof ServerPlayerEntity player) {
            NbtCompound nbt = stack.getOrCreateNbt();
            if (LichdomManager.isPlayerLich(player)) {
                long existingValue = -1L;
                if (nbt.contains(MEDIA_TAG)) {
                    existingValue = nbt.getLong(MEDIA_TAG);
                }
                LichData lichData = LichdomManager.getLichData(player);
                if (lichData.getMedia() != existingValue) {
                    nbt.putLong(MEDIA_TAG, lichData.getMedia());
                }
            } else if (nbt.contains(MEDIA_TAG)) {
                nbt.remove(MEDIA_TAG);
            }
        }
    }

    @Inject(method = "appendTooltip(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Ljava/util/List;Lnet/minecraft/client/item/TooltipContext;)V",
    at = @At(value = "TAIL"))
    public void applyMediaTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context, CallbackInfo ci) {
        if (stack.getItem() != HexItems.SCRYING_LENS){
            return;
        }
        NbtCompound nbt = stack.getOrCreateNbt();
        if (nbt.contains(MEDIA_TAG)){
            long maxMedia = LichData.maxInternalMedia;
            long media = nbt.getLong(MEDIA_TAG);
            float fullness = ((float) media) / ((float) maxMedia);

            TextColor color = TextColor.fromRgb(MediaHelper.mediaBarColor(media, maxMedia));

            var mediamount = Text.literal(DUST_AMOUNT.format(media / (float) MediaConstants.DUST_UNIT));
            var percentFull = Text.literal(PERCENTAGE.format(100f * fullness) + "%");
            var maxCapacity = Text.translatable("hexcasting.tooltip.media", DUST_AMOUNT.format(maxMedia / (float) MediaConstants.DUST_UNIT));

            mediamount.styled(style -> style.withColor(HEX_COLOR));
            maxCapacity.styled(style -> style.withColor(HEX_COLOR));
            percentFull.styled(style -> style.withColor(color));

            tooltip.add(
                    Text.translatable("oneironaut.tooltip.lich_media_amount",
                            mediamount, maxCapacity, percentFull));
        }
    }
}
