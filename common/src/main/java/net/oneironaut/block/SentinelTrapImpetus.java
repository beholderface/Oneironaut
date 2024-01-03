package net.oneironaut.block;

import at.petrak.hexcasting.api.block.circle.BlockAbstractImpetus;
import at.petrak.hexcasting.api.block.circle.BlockCircleComponent;
import at.petrak.hexcasting.api.block.circle.BlockEntityAbstractImpetus;
import at.petrak.hexcasting.api.player.Sentinel;
import at.petrak.hexcasting.api.spell.iota.EntityIota;
import at.petrak.hexcasting.common.blocks.circles.impetuses.BlockStoredPlayerImpetus;
import at.petrak.hexcasting.common.blocks.entity.BlockEntityStoredPlayerImpetus;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
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

    public SentinelTrapImpetus(Settings settings){
        super(settings);
    }
    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SentinelTrapImpetusEntity(pos,state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? (_world, _pos, _state, _be) -> ((SentinelTrapImpetusEntity)_be).tick(_world, _pos, _state) : null;
    }

    @Override
    public ActionResult onUse(BlockState pState, World world, BlockPos pPos, PlayerEntity pPlayer, Hand pHand,
                            BlockHitResult pHit) {
        if (world.getBlockEntity(pPos) instanceof SentinelTrapImpetusEntity tile) {
            var usedStack = pPlayer.getStackInHand(pHand);
            var datumContainer = IXplatAbstractions.INSTANCE.findDataHolder(usedStack);
            if (datumContainer != null) {
                if (world instanceof ServerWorld level) {
                    var stored = datumContainer.readIota(level);
                    if (stored instanceof EntityIota eieio) {
                        var entity = eieio.getEntity();
                        if (entity instanceof PlayerEntity player) {
                            // phew, we got something
                            tile.setPlayer(player.getGameProfile(), entity.getUuid());
                            level.updateListeners(pPos, pState, pState, Block.NOTIFY_LISTENERS);

                            world.playSound(pPlayer, pPos, HexSounds.IMPETUS_STOREDPLAYER_DING,
                                    SoundCategory.BLOCKS, 1f, 1f);
                        }
                    }
                }
                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;
    }
}
