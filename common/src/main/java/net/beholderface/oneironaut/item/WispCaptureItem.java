package net.beholderface.oneironaut.item;

import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.utils.MediaHelper;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.common.items.magic.ItemMediaHolder;
import at.petrak.hexcasting.common.lib.HexSounds;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import com.mojang.datafixers.util.Either;
import kotlin.collections.CollectionsKt;
import net.beholderface.oneironaut.Oneironaut;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ram.talia.hexal.common.entities.TickingWisp;
import ram.talia.hexal.common.lib.HexalEntities;
import ram.talia.hexal.common.network.MsgParticleLinesAck;

import java.util.List;
import java.util.function.Predicate;

public class WispCaptureItem extends ItemMediaHolder {

    public static final Identifier FILLED_PREDICATE = new Identifier(Oneironaut.MOD_ID, "contains_wisp");

    public WispCaptureItem(Settings settings) {
        super(settings);
    }

    public static String WISP_DATA_TAG = "contained_wisp";
    public static String WISP_TIMESTAMP = "wisp_capture_time";
    private static final int COOLDOWN = 20;
    private static final boolean debugMessages = false;

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!this.hasWisp(stack, null)){
            user.getItemCooldownManager().set(this, COOLDOWN);
            //idk how to make it get the user's actual reach, but this will do well enough IMO
            Vec3d rayVec = user.getRotationVector().multiply((user.isCreative() ? 5.2 : 4.5) * (user.getHeight() / 1.8 /*in case of pekhui or something*/));
            Vec3d endPos = user.getEyePos().add(rayVec);
            Box box = Box.from(user.getEyePos()).expand(rayVec.length() + 1);
            Predicate<Entity> predicate = (entity)-> entity instanceof TickingWisp;
            List<Entity> possibleWisps = world.getOtherEntities(user, box, predicate);
            Oneironaut.boolLogger("Possible wisps: " + possibleWisps.toString() + ", isClient: " + world.isClient, debugMessages);
            EntityHitResult hit = ProjectileUtil.raycast(user, user.getEyePos(), endPos, box, predicate, 999999);
            if (hit != null){
                TickingWisp wisp = (TickingWisp) hit.getEntity();
                Entity allegedlyNotWisp = hit.getEntity();
                Oneironaut.boolLogger("Raycast hit wisp at position " + allegedlyNotWisp.getPos().toString() + "." + world.isClient, debugMessages);
                if (!world.isClient){
                    int cost = MediaConstants.SHARD_UNIT;
                    if (wisp.getCaster() != user){
                        cost = (int) Math.ceil(wisp.getMedia() * 1.5);
                    }
                    if (this.getMedia(stack) >= cost){
                        this.setMedia(stack, this.getMedia(stack) - cost);
                        NbtCompound wispData = wisp.writeNbt(new NbtCompound());
                        NbtCompound data = stack.getOrCreateNbt();
                        NBTHelper.putCompound(data, WISP_DATA_TAG, wispData);
                        //data.putLong(WISP_TIMESTAMP, world.getTime());
                        allegedlyNotWisp.kill();
                        Oneironaut.boolLogger("Captured wisp for " + cost / MediaConstants.DUST_UNIT + " dust", debugMessages);
                        IXplatAbstractions.INSTANCE.sendPacketNear(user.getEyePos(), 128.0, (ServerWorld) world,
                                new MsgParticleLinesAck(CollectionsKt.listOf(user.getEyePos(), allegedlyNotWisp.getPos().add(0.0, 0.05, 0.0)), wisp.colouriser()));
                        world.playSoundFromEntity(null, user, HexSounds.CAST_HERMES, SoundCategory.PLAYERS, 1f, 1f, world.random.nextLong());
                    } else {
                        world.playSoundFromEntity(null, user, HexSounds.FAIL_PATTERN, SoundCategory.PLAYERS, 1f, 1f, world.random.nextLong());
                        Oneironaut.boolLogger("Could not capture wisp.", debugMessages);
                        return TypedActionResult.fail(stack);
                    }
                }
                return TypedActionResult.success(stack, true);
            } else {
                Oneironaut.boolLogger("Raycast did not find anything." + world.isClient, debugMessages);
            }
        }
        return TypedActionResult.pass(stack);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        ItemStack stack = context.getStack();
        NbtCompound nbt = stack.getOrCreateNbt();
        World world = context.getWorld();
        PlayerEntity user = context.getPlayer();
        //release the wisp adjacent to the clicked block
        if (this.hasWisp(stack, world) && nbt.contains(ItemMediaHolder.TAG_MEDIA)){
            if (user != null){
                user.getItemCooldownManager().set(this, COOLDOWN);
            }
            if (nbt.getInt(ItemMediaHolder.TAG_MEDIA) >= MediaConstants.SHARD_UNIT) {
                Oneironaut.boolLogger("Releasing contained wisp", debugMessages);
                this.setMedia(stack, this.getMedia(stack) - MediaConstants.SHARD_UNIT);
                TickingWisp wisp = new TickingWisp(HexalEntities.TICKING_WISP, world);
                Entity allegedlyNotWisp = (Entity) wisp;
                BlockPos spawnPos = context.getBlockPos().add(context.getSide().getVector());
                Vec3d spawnVec = Vec3d.ofCenter(new Vec3i(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ()));
                if (world instanceof ServerWorld serverWorld) {
                    NbtCompound storedNbt = this.getWispData(stack, world);
                    NbtList posList = new NbtList();
                    posList.add(NbtDouble.of(spawnVec.x));
                    posList.add(NbtDouble.of(spawnVec.y));
                    posList.add(NbtDouble.of(spawnVec.z));
                    NBTHelper.putList(storedNbt, "Pos", posList);
                    serverWorld.playSoundFromEntity(null, user, HexSounds.CAST_HERMES,
                            SoundCategory.PLAYERS, 1f, 1f, world.random.nextLong());
                    wisp.readNbt(storedNbt);
                }
                nbt.remove(WISP_DATA_TAG);

                world.spawnEntity(wisp);
                if (user != null && world instanceof ServerWorld serverWorld) {
                    IXplatAbstractions.INSTANCE.sendPacketNear(user.getEyePos(), 128.0, serverWorld,
                            new MsgParticleLinesAck(CollectionsKt.listOf(user.getEyePos(), allegedlyNotWisp.getPos().add(0.0, 0.05, 0.0)), wisp.colouriser()));
                }
            } else {
                Oneironaut.boolLogger("Insufficient media to release wisp", debugMessages);
                if (world instanceof ServerWorld serverWorld) {
                    serverWorld.playSoundFromEntity(null, user, HexSounds.FAIL_PATTERN,
                            SoundCategory.PLAYERS, 1f, 1f, world.random.nextLong());
                }
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Nullable
    public NbtCompound getWispData(ItemStack stack, @Nullable World world){
        NbtCompound nbt = stack.getOrCreateNbt();
        long timestamp = nbt.contains(WISP_TIMESTAMP) ? nbt.getLong(WISP_TIMESTAMP) : Long.MIN_VALUE;
        if (nbt.contains(WISP_DATA_TAG)){
            NbtCompound data = nbt.getCompound(WISP_DATA_TAG);
            //data.remove("Pos");
            /*if (world != null){
                //to make sure it doesn't try to resummon the wisp the same tick it was captured
                if (timestamp < world.getTime()){
                    return data;
                }
            } else {
                //for if you don't care about the above comment
                return data;
            }*/
            return data;
        }
        return null;
    }

    public boolean hasWisp(ItemStack stack, World world){
        return this.getWispData(stack, world) != null;
    }

    @Override
    public int getMaxMedia(ItemStack stack) {
        return MediaConstants.CRYSTAL_UNIT * 64;
    }

    @Override
    public boolean canProvideMedia(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canRecharge(ItemStack stack) {
        return true;
    }

    @Override
    public void appendTooltip(ItemStack pStack, @Nullable World world, List<Text> pTooltipComponents, TooltipContext context) {
        super.appendTooltip(pStack, world, pTooltipComponents, context);
        if (this.hasWisp(pStack, world)){
            pTooltipComponents.add(Text.translatable("oneironaut.tooltip.wispcapturedevice.haswisp"));
        } else {
            pTooltipComponents.add(Text.translatable("oneironaut.tooltip.wispcapturedevice.nowisp"));
        }
    }
}
