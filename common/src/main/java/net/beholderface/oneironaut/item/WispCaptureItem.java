package net.beholderface.oneironaut.item;

import at.petrak.hexcasting.api.misc.FrozenColorizer;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.spell.ParticleSpray;
import at.petrak.hexcasting.api.utils.MediaHelper;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.common.items.magic.ItemMediaHolder;
import at.petrak.hexcasting.common.lib.HexSounds;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import at.petrak.hexcasting.common.network.MsgCastParticleAck;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import com.mojang.datafixers.util.Either;
import kotlin.collections.CollectionsKt;
import net.beholderface.oneironaut.Oneironaut;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ram.talia.hexal.common.entities.BaseCastingWisp;
import ram.talia.hexal.common.entities.ProjectileWisp;
import ram.talia.hexal.common.entities.TickingWisp;
import ram.talia.hexal.common.lib.HexalEntities;
import ram.talia.hexal.common.network.MsgParticleLinesAck;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.function.Predicate;

public class WispCaptureItem extends ItemMediaHolder {

    public static final Identifier FILLED_PREDICATE = new Identifier(Oneironaut.MOD_ID, "contains_wisp");

    public WispCaptureItem(Settings settings) {
        super(settings);
    }

    public static String WISP_DATA_TAG = "contained_wisp";
    public static String WISP_TYPE_TAG = "wisp_type";
    private static final int COOLDOWN = 20;
    private static final boolean debugMessages = false;

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        NbtCompound data = stack.getOrCreateNbt();
        if (!this.hasWisp(stack, null)){
            user.getItemCooldownManager().set(this, COOLDOWN);
            //idk how to make it get the user's actual reach, but this will do well enough IMO
            Vec3d rayVec = user.getRotationVector().multiply((user.isCreative() ? 5.2 : 4.5) * (user.getHeight() / 1.8 /*in case of pekhui or something*/));
            Vec3d endPos = user.getEyePos().add(rayVec);
            Box box = Box.from(user.getEyePos()).expand(rayVec.length() + 1);
            Predicate<Entity> predicate = (entity)-> entity instanceof TickingWisp || entity instanceof ProjectileWisp;
            EntityHitResult hit = ProjectileUtil.raycast(user, user.getEyePos(), endPos, box, predicate, 999999);
            if (hit != null){
                boolean captured = this.captureWisp(stack, (BaseCastingWisp) hit.getEntity(), user);
                return captured ? TypedActionResult.success(stack, true) : TypedActionResult.fail(stack);
            } else {
                Oneironaut.boolLogger("Raycast did not find anything." + world.isClient, debugMessages);
            }
        } else if (user.isSneaking() && world instanceof ServerWorld serverWorld){
            user.getItemCooldownManager().set(this, COOLDOWN / 2);
            this.discardWisp(stack, user);
            return TypedActionResult.success(stack, false);
        } else if (this.getWispType(stack) == HexalEntities.PROJECTILE_WISP){
            user.getItemCooldownManager().set(this, COOLDOWN);
            boolean released = this.releaseWisp(stack, user.getEyePos().add(user.getRotationVector().multiply(0.25)), user);
            return released ? TypedActionResult.success(stack, true) : TypedActionResult.fail(stack);
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
        boolean sneaking = user != null && user.isSneaking();
        if (this.hasWisp(stack, world) && nbt.contains(ItemMediaHolder.TAG_MEDIA) && !sneaking){
            if (user != null){
                user.getItemCooldownManager().set(this, COOLDOWN);
                BlockPos spawnPos = context.getBlockPos().add(context.getSide().getVector());
                Vec3d spawnVec = Vec3d.ofCenter(new Vec3i(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ()));
                boolean released  = this.releaseWisp(stack, spawnVec, user);
                return released ? ActionResult.SUCCESS : ActionResult.FAIL;
            }
        }
        return ActionResult.PASS;
    }

    private boolean captureWisp(ItemStack stack, BaseCastingWisp wisp, @NotNull PlayerEntity user){
        World world = user.world;
        NbtCompound stackNbt = stack.getOrCreateNbt();
        int cost = MediaConstants.SHARD_UNIT;
        if (wisp.getCaster() != user){
            cost = (int) Math.ceil(wisp.getMedia() * 1.5);
        }
        if (!world.isClient){
            if (this.getMedia(stack) >= cost || user.isCreative()){
                this.deductMedia(stack, cost, user);
                NbtCompound wispData = wisp.writeNbt(new NbtCompound());
                NBTHelper.putCompound(stackNbt, WISP_DATA_TAG, wispData);
                ((Entity) wisp).kill();
                Oneironaut.boolLogger("Captured wisp for " + cost / MediaConstants.DUST_UNIT + " dust", debugMessages);
                IXplatAbstractions.INSTANCE.sendPacketNear(user.getEyePos(), 128.0, (ServerWorld) world,
                        new MsgParticleLinesAck(CollectionsKt.listOf(user.getEyePos(), ((Entity) wisp).getPos().add(0.0, 0.05, 0.0)), wisp.colouriser()));
                world.playSoundFromEntity(null, user, HexSounds.CAST_HERMES, SoundCategory.PLAYERS, 1f, 1f, world.random.nextLong());
                if (wisp instanceof TickingWisp tickingWisp){
                    stackNbt.putString(WISP_TYPE_TAG, "ticking");
                } else if (wisp instanceof ProjectileWisp projectileWisp){
                    stackNbt.putString(WISP_TYPE_TAG, "projectile");
                }
                return true;
            } else {
                world.playSoundFromEntity(null, user, HexSounds.FAIL_PATTERN, SoundCategory.PLAYERS, 1f, 1f, world.random.nextLong());
            }
        }
        return false;
    }
    private boolean releaseWisp(ItemStack stack, Vec3d spawnPos, @NotNull PlayerEntity user){
        NbtCompound nbt = stack.getOrCreateNbt();
        World world = user.world;
        if (this.getMedia(stack) >= MediaConstants.SHARD_UNIT || user.isCreative()) {
            Oneironaut.boolLogger("Releasing contained wisp", debugMessages);
            this.deductMedia(stack, MediaConstants.SHARD_UNIT, user);
            EntityType<?> wispType = this.getWispType(stack);
            BaseCastingWisp wisp = null;
            if (wispType == HexalEntities.TICKING_WISP){
                wisp = new TickingWisp(HexalEntities.TICKING_WISP, world);
            } else if (wispType == HexalEntities.PROJECTILE_WISP){
                wisp = new ProjectileWisp(HexalEntities.PROJECTILE_WISP, world);
            }
            if (wisp != null){
                if (world instanceof ServerWorld serverWorld) {
                    NbtCompound storedNbt = this.getWispData(stack, world);
                    NbtList posList = new NbtList();
                    posList.add(NbtDouble.of(spawnPos.x));
                    posList.add(NbtDouble.of(spawnPos.y));
                    posList.add(NbtDouble.of(spawnPos.z));
                    NBTHelper.putList(storedNbt, "Pos", posList);
                    serverWorld.playSoundFromEntity(null, user, HexSounds.CAST_HERMES,
                            SoundCategory.PLAYERS, 1f, 1f, world.random.nextLong());
                    if (wisp instanceof ProjectileWisp projectileWisp){
                        double speed = projectileWisp.getVelocity().length();
                        Vec3d direction = user.getRotationVector();
                        NbtList motionList = new NbtList();
                        motionList.add(NbtDouble.of(direction.x));
                        motionList.add(NbtDouble.of(direction.y));
                        motionList.add(NbtDouble.of(direction.z));
                        NBTHelper.putList(storedNbt, "Motion", motionList);
                        //projectileWisp.setVelocity(direction.multiply(speed));
                    }
                    wisp.readNbt(storedNbt);
                }
                nbt.remove(WISP_DATA_TAG);
                world.spawnEntity(wisp);
                if (world instanceof ServerWorld serverWorld) {
                    IXplatAbstractions.INSTANCE.sendPacketNear(user.getEyePos(), 128.0, serverWorld,
                            new MsgParticleLinesAck(CollectionsKt.listOf(user.getEyePos(), ((Entity) wisp).getPos().add(0.0, 0.05, 0.0)), wisp.colouriser()));
                    return true;
                }
            }
        } else {
            Oneironaut.boolLogger("Insufficient media to release wisp", debugMessages);
            if (world instanceof ServerWorld serverWorld) {
                serverWorld.playSoundFromEntity(null, user, HexSounds.FAIL_PATTERN,
                        SoundCategory.PLAYERS, 1f, 1f, world.random.nextLong());
            }
        }
        return false;
    }
    private void discardWisp(ItemStack stack, @Nullable PlayerEntity user){
        NbtCompound data = stack.getOrCreateNbt();
        NbtCompound formerWispData = this.getWispData(stack, null);
        assert formerWispData != null;
        FrozenColorizer colorizer = FrozenColorizer.fromNBT(formerWispData.getCompound("colouriser"));
        //int media = formerWispData.getInt("media");
        data.remove(WISP_DATA_TAG);
        if (user != null){
            World world = user.world;
            if (world instanceof ServerWorld serverWorld){
                world.playSoundFromEntity(null, user, HexSounds.ABACUS_SHAKE, SoundCategory.PLAYERS, 1f, 1f, world.random.nextLong());
                IXplatAbstractions.INSTANCE.sendPacketNear(user.getEyePos(), 128.0, serverWorld, new MsgCastParticleAck
                        (ParticleSpray.burst(user.getPos().add(0.0, 0.125, 0.0), 1.0, 64), colorizer));
            }
        }

    }

    @Nullable
    public NbtCompound getWispData(ItemStack stack, @Nullable World world){
        NbtCompound nbt = stack.getOrCreateNbt();
        if (nbt.contains(WISP_DATA_TAG)){
            return nbt.getCompound(WISP_DATA_TAG);
        }
        return null;
    }

    @Nullable
    public EntityType<?> getWispType(ItemStack stack){
        NbtCompound nbt = stack.getOrCreateNbt();
        if (nbt.contains(WISP_TYPE_TAG)){
            String typeString = nbt.getString(WISP_TYPE_TAG);
            if (typeString.equals("ticking")){
                return HexalEntities.TICKING_WISP;
            } else if (typeString.equals("projectile")){
                return HexalEntities.PROJECTILE_WISP;
            }
        }
        return null;
    }

    private void deductMedia(ItemStack stack, int amount, PlayerEntity player){
        if (!player.isCreative()){
            this.setMedia(stack, this.getMedia(stack) - amount);
        }
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
        if (this.hasWisp(pStack, null)){
            String hashString = "???";
            NbtCompound wispData = this.getWispData(pStack, null);
            assert wispData != null;
            int media = wispData.getInt("media");
            NbtElement hexData = wispData.get("hex");
            assert hexData != null;
            String nbtString = hexData.toString();
            /*if (world != null && world.getTime() % 100 == 0){
                Oneironaut.LOGGER.info(nbtString);
            }*/
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                digest.update(nbtString.getBytes(StandardCharsets.UTF_8));
                hashString = new String(digest.digest());
            } catch (NoSuchAlgorithmException exception){
                //do nothing? idk
            }
            Text unstyled = Text.translatable("oneironaut.tooltip.wispcapturedevice.haswisp", (media / MediaConstants.DUST_UNIT), hashString);
            if (world != null){
                Style coloredStyle = unstyled.getStyle().withColor(
                        FrozenColorizer.fromNBT(wispData.getCompound("colouriser")).getColor(world.getTime(), Vec3d.ZERO)
                );
                pTooltipComponents.add(unstyled.copy().setStyle(coloredStyle));
            } else {
                pTooltipComponents.add(unstyled);
            }
        } else {
            pTooltipComponents.add(Text.translatable("oneironaut.tooltip.wispcapturedevice.nowisp"));
        }
    }
}
