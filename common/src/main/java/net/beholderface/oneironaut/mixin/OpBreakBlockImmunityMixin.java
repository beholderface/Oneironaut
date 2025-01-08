package net.beholderface.oneironaut.mixin;

import at.petrak.hexcasting.fabric.xplat.FabricXplatImpl;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.beholderface.oneironaut.MiscAPIKt;
import net.beholderface.oneironaut.Oneironaut;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/*@Mixin(targets = "at.petrak.hexcasting.common.casting.operators.spells.OpBreakBlock$Spell")
public abstract class OpBreakBlockImmunityMixin {
    @WrapOperation(method = "cast", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/world/ServerWorld;breakBlock(Lnet/minecraft/util/math/BlockPos;ZLnet/minecraft/entity/Entity;)Z",
            remap = true), remap = false)
    private boolean dontBreakIfImmune(ServerWorld world, BlockPos pos, boolean b, Entity entity, Operation<Boolean> original){
        BlockState state = world.getBlockState(pos);
        if (state.isIn(MiscAPIKt.getBlockTagKey(new Identifier(Oneironaut.MOD_ID, "hexbreakimmune")))){
            return false;
        } else {
            return original.call(world, pos, b, entity);
        }
    }
}*/

//this should have a significantly wider-reaching effect
@Mixin(FabricXplatImpl.class)
public abstract class OpBreakBlockImmunityMixin {
    @ModifyReturnValue(method = "isBreakingAllowed", at = @At(value = "RETURN", remap = false), remap = false)
    public boolean dontBreakIfImmune(boolean original, @Local PlayerEntity player,
                                     @Local ServerWorld world, @Local BlockPos pos, @Local BlockState state){
        if (!original){
            return false;
        } else {
            return !state.isIn(MiscAPIKt.getBlockTagKey(new Identifier(Oneironaut.MOD_ID, "hexbreakimmune")))
                    && PlayerBlockBreakEvents.BEFORE.invoker()
                    .beforeBlockBreak(world, player, pos, state, world.getBlockEntity(pos));
        }
    }
}