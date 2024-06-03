package net.beholderface.oneironaut.block;

import at.petrak.hexcasting.api.block.circle.BlockEntityAbstractImpetus;
import at.petrak.hexcasting.api.utils.NBTHelper;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import net.beholderface.oneironaut.registry.OneironautBlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.beholderface.oneironaut.registry.OneironautBlockRegistry;
import net.beholderface.oneironaut.registry.OneironautItemRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SentinelTrapImpetusEntity extends BlockEntityAbstractImpetus {
    public static final String TAG_STORED_PLAYER = "stored_player";
    public static final String TAG_STORED_PLAYER_PROFILE = "stored_player_profile";

    private GameProfile storedPlayerProfile = null;
    private UUID storedPlayer = null;
    public static final String TAG_TARGET_PLAYER = "target_player";
    private UUID targetPlayer = null;

    private GameProfile cachedDisplayProfile = null;
    private ItemStack cachedDisplayStack = null;


    public SentinelTrapImpetusEntity(BlockPos pos, BlockState state){
        super(OneironautBlockRegistry.SENTINEL_TRAP_ENTITY.get(), pos, state);
    }

    @Override
    public boolean activatorAlwaysInRange() {
        return true;
    }

    protected @Nullable
    GameProfile getPlayerName() {
        PlayerEntity player = getStoredPlayer();
        if (player != null) {
            return player.getGameProfile();
        }

        return this.storedPlayerProfile;
    }

    public void setPlayer(GameProfile profile, UUID player) {
        this.storedPlayerProfile = profile;
        this.storedPlayer = player;
        this.markDirty();
    }

    public void clearPlayer() {
        this.storedPlayerProfile = null;
        this.storedPlayer = null;
    }

    public void updatePlayerProfile() {
        PlayerEntity player = getStoredPlayer();
        if (player != null) {
            GameProfile newProfile = player.getGameProfile();
            if (!newProfile.equals(this.storedPlayerProfile)) {
                this.storedPlayerProfile = newProfile;
                this.markDirty();
            }
        } else {
            this.storedPlayerProfile = null;
        }
    }
    public @Nullable
    PlayerEntity getStoredPlayer() {
        assert this.world != null;
        if (this.storedPlayer != null){
            return this.world.getPlayerByUuid(this.storedPlayer);
        } else {
            return null;
        }
        //return this.storedPlayer;
    }

    public @Nullable PlayerEntity getTargetPlayer(){
        assert this.world != null;
        if (this.storedPlayer != null){
            return this.world.getPlayerByUuid(this.targetPlayer);
        } else {
            return null;
        }
    }

    public void setTargetPlayer(UUID player) {
        this.targetPlayer = player;
        this.markDirty();
    }

    public void applyScryingLensOverlay(List<Pair<ItemStack, Text>> lines,
                                        BlockState state, BlockPos pos, PlayerEntity observer,
                                        World world,
                                        Direction hitFace) {
        super.applyScryingLensOverlay(lines, state, pos, observer, world, hitFace);

        var name = this.getPlayerName();
        if (name != null) {
            if (!name.equals(cachedDisplayProfile) || cachedDisplayStack == null) {
                cachedDisplayProfile = name;
                var head = new ItemStack(Items.PLAYER_HEAD);
                NBTHelper.put(head, "SkullOwner", net.minecraft.nbt.NbtHelper.writeGameProfile(new NbtCompound(), name));
                head.getItem().postProcessNbt(head.getOrCreateNbt());
                cachedDisplayStack = head;
            }
            lines.add(new Pair<>(cachedDisplayStack,
                    Text.translatable("hexcasting.tooltip.lens.impetus.storedplayer", name.getName())));
        } else {
            lines.add(new Pair<>(new ItemStack(Items.BARRIER),
                    Text.translatable("hexcasting.tooltip.lens.impetus.storedplayer.none")));
        }
    }
    @Override
    protected void saveModData(NbtCompound tag) {
        super.saveModData(tag);
        if (this.storedPlayer != null) {
            tag.putUuid(TAG_STORED_PLAYER, this.storedPlayer);
        }
        if (this.targetPlayer != null){
            tag.putUuid(TAG_TARGET_PLAYER, this.targetPlayer);
        }
        if (this.storedPlayerProfile != null) {
            tag.put(TAG_STORED_PLAYER_PROFILE, net.minecraft.nbt.NbtHelper.writeGameProfile(new NbtCompound(), storedPlayerProfile));
        }
    }

    @Override
    protected void loadModData(NbtCompound tag) {
        super.loadModData(tag);
        if (tag.contains(TAG_STORED_PLAYER, NbtElement.INT_ARRAY_TYPE)) {
            this.storedPlayer = tag.getUuid(TAG_STORED_PLAYER);
        } else {
            this.storedPlayer = null;
        }
        if (tag.contains(TAG_TARGET_PLAYER, NbtElement.INT_ARRAY_TYPE)){
            this.targetPlayer = tag.getUuid(TAG_TARGET_PLAYER);
        } else {
            this.targetPlayer = null;
        }
        if (tag.contains(TAG_STORED_PLAYER_PROFILE, NbtElement.COMPOUND_TYPE)) {
            this.storedPlayerProfile = net.minecraft.nbt.NbtHelper.toGameProfile(tag.getCompound(TAG_STORED_PLAYER_PROFILE));
        } else {
            this.storedPlayerProfile = null;
        }
    }

    public static Map<RegistryKey<World>, Map<BlockPos, Vec3d>> trapLocationMap = new HashMap<>();
    //@Override
    public void tick(World world, BlockPos pos, BlockState state) {
        RegistryKey<World> worldKey = world.getRegistryKey();
        if (!(trapLocationMap.containsKey(worldKey))){
            Map<BlockPos, Vec3d> newMap = new HashMap<BlockPos, Vec3d>();
            newMap.put(pos, new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
            trapLocationMap.put(worldKey, newMap);
            //Oneironaut.LOGGER.info("Created map and did a thing");
        } else {
            Map<BlockPos, Vec3d> existingMap = trapLocationMap.get(worldKey);
            if (!(existingMap.containsKey(pos))){
                existingMap.put(pos, new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
                //Oneironaut.LOGGER.info("did a thing with existing map");
            }
        }
    }

}
