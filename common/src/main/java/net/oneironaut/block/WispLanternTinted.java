package net.oneironaut.block;

import at.petrak.hexcasting.api.misc.FrozenColorizer;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class WispLanternTinted extends BlockWithEntity/* implements ISplatoonableBlock*/ {

    public WispLanternTinted(Settings settings){
        super(settings);
        //setDefaultState(getDefaultState().with(COLOR, 0));
    }
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state){
        //Oneironaut.LOGGER.info("Creating blockentity.");
        return new WispLanternEntityTinted(pos, state);
    }
    @Override
    public BlockRenderType getRenderType(BlockState state){
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context){
        VoxelShape glass = VoxelShapes.cuboid(4f / 16, 0f / 16, 4f / 16, 12f / 16, 9f / 16, 12f / 16);
        VoxelShape lid = VoxelShapes.cuboid(5f / 16, 8f / 16, 5f / 16, 11f / 16, 10f / 16, 11f / 16);
        return VoxelShapes.union(glass, lid);
    }

    public void splatPigmentOntoBlock(World world, BlockPos pos, FrozenColorizer pigment){
        WispLanternEntityTinted be = (WispLanternEntityTinted) (world.getBlockEntity(pos));
        assert be != null;
        be.setColor(pigment.item(), world.getPlayerByUuid(pigment.owner()));
        be.markDirty();
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit){
        //int color = state.get(COLOR);
        ItemStack item = player.getStackInHand(hand);
        if (IXplatAbstractions.INSTANCE.isColorizer(item)){
            WispLanternEntityTinted be = (WispLanternEntityTinted) world.getBlockEntity(pos);
            assert be != null;
            be.setColor(item, player);
            be.markDirty();
            return ActionResult.SUCCESS;
        } else {
            return ActionResult.FAIL;
        }
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return (_world, _pos, _state, _be) -> ((WispLanternEntityTinted)_be).tick(_world, _pos, _state);
    }
}
