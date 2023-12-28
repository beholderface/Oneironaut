package net.oneironaut.block;

import at.petrak.hexcasting.api.block.circle.BlockAbstractImpetus;
import at.petrak.hexcasting.api.block.circle.BlockCircleComponent;
import at.petrak.hexcasting.api.player.Sentinel;
import at.petrak.hexcasting.api.spell.iota.EntityIota;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import at.petrak.hexcasting.common.lib.HexSounds;

import net.minecraft.util.math.random.Random;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class SentinelTrapImpetus extends BlockAbstractImpetus {

    private static Map<ServerPlayerEntity, Sentinel> sentinelMap = new HashMap<>();

    public SentinelTrapImpetus(Settings settings){
        super(settings);
    }
    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SentinelTrapImpetusEntity(pos,state);
    }

    //@Override
    public void tick(BlockState pState, ServerWorld world, BlockPos pPos, Random pRandom) {
        Iterator<ServerPlayerEntity> playerIterator = world.getServer().getPlayerManager().getPlayerList().iterator();
        ServerPlayerEntity currentPlayer = null;
        Sentinel currentSentinel = null;
        while (playerIterator.hasNext()){
            currentPlayer = playerIterator.next();
            currentSentinel = IXplatAbstractions.INSTANCE.getSentinel(currentPlayer);
            if(currentSentinel.dimension().equals(world.getRegistryKey())
                    && currentSentinel.position().isInRange(new Vec3d(pPos.getX(), pPos.getY(), pPos.getZ()), 16)){
                if (!(sentinelMap.containsKey(currentPlayer))){
                    if (world.getBlockEntity(pPos) instanceof SentinelTrapImpetusEntity tile && !(world.getBlockState(pPos).get(BlockCircleComponent.ENERGIZED))) {
                        tile.activateSpellCircle(tile.getStoredPlayer());
                    }
                    sentinelMap.put(currentPlayer, currentSentinel);
                }
            } else {
                if (sentinelMap.containsKey(currentPlayer)){
                    sentinelMap.remove(currentPlayer);
                }
            }
        }
        super.scheduledTick(pState, world, pPos, pRandom);
        if (world.getBlockEntity(pPos) instanceof SentinelTrapImpetusEntity tile) {
            tile.updatePlayerProfile();
        }
    }

    //@Override
    public ActionResult use(BlockState pState, World world, BlockPos pPos, PlayerEntity pPlayer, Hand pHand,
                            BlockHitResult pHit) {
        if (world instanceof ServerWorld level
                && level.getBlockEntity(pPos) instanceof SentinelTrapImpetusEntity tile) {
            var usedStack = pPlayer.getStackInHand(pHand);
            if (usedStack.isEmpty() && pPlayer.isSneaky()) {
                tile.clearPlayer();
                tile.sync();
            } else {
                var datumContainer = IXplatAbstractions.INSTANCE.findDataHolder(usedStack);
                if (datumContainer != null) {
                    var stored = datumContainer.readIota(level);
                    if (stored instanceof EntityIota eieio) {
                        var entity = eieio.getEntity();
                        if (entity instanceof PlayerEntity player) {
                            tile.setPlayer(player.getGameProfile(), entity.getUuid());
                            tile.sync();

                            world.playSound(pPlayer, pPos, HexSounds.SPELL_CIRCLE_CAST,
                                    SoundCategory.BLOCKS, 1f, 1f);
                        }
                    }
                }
            }
        }

        return ActionResult.PASS;
    }
}