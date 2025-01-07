package net.beholderface.oneironaut.block;

import at.petrak.hexcasting.api.pigment.FrozenPigment;
import net.beholderface.oneironaut.block.blockentity.WispLanternEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
//import net.minecraft.state.StateManager;
//import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
//import at.petrak.hexcasting.common.lib.HexItems;
import at.petrak.hexcasting.xplat.IXplatAbstractions;

public class WispLantern extends BlockWithEntity/* implements ISplatoonableBlock*/ {

    public WispLantern(Settings settings){
        super(settings);
        //setDefaultState(getDefaultState().with(COLOR, 0));
    }
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state){
        //Oneironaut.LOGGER.info("Creating blockentity.");
        return new WispLanternEntity(pos, state);
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

    public void splatPigmentOntoBlock(World world, BlockPos pos, FrozenPigment pigment){
        WispLanternEntity be = (WispLanternEntity)(world.getBlockEntity(pos));
        assert be != null;
        be.setColor(pigment.item(), world.getPlayerByUuid(pigment.owner()));
        be.markDirty();
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit){
        //int color = state.get(COLOR);
        ItemStack item = player.getStackInHand(hand);
        if (IXplatAbstractions.INSTANCE.isPigment(item)){
            WispLanternEntity be = (WispLanternEntity) world.getBlockEntity(pos);
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
        return world.isClient ? (_world, _pos, _state, _be) -> ((WispLanternEntity)_be).tick(_world, _pos, _state) : null;
    }
}
