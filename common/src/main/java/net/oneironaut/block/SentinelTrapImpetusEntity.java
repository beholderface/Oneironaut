package net.oneironaut.block;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.block.circle.BlockAbstractImpetus;
import at.petrak.hexcasting.api.block.circle.BlockCircleComponent;
import at.petrak.hexcasting.api.block.circle.BlockEntityAbstractImpetus;
import at.petrak.hexcasting.api.misc.FrozenColorizer;
import at.petrak.hexcasting.api.spell.ParticleSpray;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.casting.CastingHarness;
import at.petrak.hexcasting.api.spell.casting.SpellCircleContext;
import at.petrak.hexcasting.api.spell.iota.EntityIota;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.iota.PatternIota;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.common.lib.HexItems;
import at.petrak.hexcasting.common.lib.HexSounds;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.oneironaut.registry.OneironautThingRegistry;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SentinelTrapImpetusEntity extends BlockEntityAbstractImpetus {
    public static final String TAG_STORED_PLAYER = "stored_player";
    public static final String TAG_STORED_PLAYER_PROFILE = "stored_player_profile";

    private GameProfile storedPlayerProfile = null;
    private UUID storedPlayer = null;

    private GameProfile cachedDisplayProfile = null;
    private ItemStack cachedDisplayStack = null;


    public SentinelTrapImpetusEntity(BlockPos pos, BlockState state){
        super(OneironautThingRegistry.SENTINEL_TRAP_ENTITY.get(), pos, state);
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
        ServerPlayerEntity player = getStoredPlayer();
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
    @Nullable
    public ServerPlayerEntity getStoredPlayer() {
        if (this.storedPlayer == null) {
            return null;
        }
        if (!(this.world instanceof ServerWorld slevel)) {
            HexAPI.LOGGER.error("Called getStoredPlayer on the client");
            return null;
        }
        var e = slevel.getEntity(this.storedPlayer);
        if (e instanceof ServerPlayerEntity player) {
            return player;
        } else {
            HexAPI.LOGGER.error("Entity {} stored in a trap impetus wasn't a player somehow", e);
            return null;
        }
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
                    Text.translatable("hexcasting.tooltip.lens.impetus.redstone.bound", name.getName())));
        } else {
            lines.add(new Pair<>(new ItemStack(Items.BARRIER),
                    Text.translatable("hexcasting.tooltip.lens.impetus.redstone.bound.none")));
        }
    }
    @Override
    protected void saveModData(NbtCompound tag) {
        super.saveModData(tag);
        if (this.storedPlayer != null) {
            tag.putUuid(TAG_STORED_PLAYER, this.storedPlayer);
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
        if (tag.contains(TAG_STORED_PLAYER_PROFILE, NbtElement.COMPOUND_TYPE)) {
            this.storedPlayerProfile = net.minecraft.nbt.NbtHelper.toGameProfile(tag.getCompound(TAG_STORED_PLAYER_PROFILE));
        } else {
            this.storedPlayerProfile = null;
        }
    }

    @Override
    private void castSpell(ServerPlayerEntity triggeringPlayer) {
        var player = this.getPlayer();

        if (player instanceof ServerPlayerEntity splayer) {
            var bounds = getBounds(this.trackedBlocks);

            var ctx = new CastingContext(splayer, Hand.MAIN_HAND,
                    new SpellCircleContext(this.getPos(), bounds, this.activatorAlwaysInRange()));
            var harness = new CastingHarness(ctx);
            harness.getStack().add(new EntityIota(triggeringPlayer));
            BlockPos erroredPos = null;
            for (var tracked : this.trackedBlocks) {
                var bs = this.world.getBlockState(tracked);
                if (bs.getBlock() instanceof BlockCircleComponent cc) {
                    var newPattern = cc.getPattern(tracked, bs, this.world);
                    if (newPattern != null) {
                        var info = harness.executeIota(new PatternIota(newPattern), splayer.getWorld());
                        if (!info.getResolutionType().getSuccess()) {
                            erroredPos = tracked;
                            break;
                        }
                    }
                }
            }

            if (erroredPos != null) {
                this.sfx(erroredPos, false);
            } else {
                this.setLastMishap(null);
            }

            this.markDirty();
        }
    }

}
