package net.oneironaut.mixin;

import at.petrak.hexcasting.common.casting.operators.OpBlockAxisRaycast;
import at.petrak.hexcasting.common.casting.operators.OpBlockRaycast;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RaycastContext;
import net.oneironaut.MiscAPIKt;
import net.oneironaut.Oneironaut;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = {OpBlockRaycast.class, OpBlockAxisRaycast.class})
public abstract class BlockRaycastImmunityMixin {
    @Redirect(method = "execute", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/world/ServerWorld;raycast(Lnet/minecraft/world/RaycastContext;)Lnet/minecraft/util/hit/BlockHitResult;"))
    private BlockHitResult nullIfImmune(ServerWorld world, RaycastContext raycastContext){
        BlockHitResult unmodifiedResult = world.raycast(raycastContext);
        if (unmodifiedResult.getType().equals(HitResult.Type.BLOCK)){
            if (world.getBlockState(unmodifiedResult.getBlockPos()).isIn(MiscAPIKt.getBlockTagKey(new Identifier(Oneironaut.MOD_ID, "blocksraycast")))){
                return BlockHitResult.createMissed(raycastContext.getStart(), Direction.DOWN, unmodifiedResult.getBlockPos());
            }
        }
        return unmodifiedResult;
    }
}
