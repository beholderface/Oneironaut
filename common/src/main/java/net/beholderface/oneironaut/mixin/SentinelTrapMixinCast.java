package net.beholderface.oneironaut.mixin;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.SpellCircleContext;
import at.petrak.hexcasting.api.casting.eval.env.CircleCastEnv;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.iota.EntityIota;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.beholderface.oneironaut.block.blockentity.SentinelTrapImpetusEntity;
import net.beholderface.oneironaut.registry.OneironautBlockRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("ConstantConditions")
@Mixin(CastingVM.class)
public abstract class SentinelTrapMixinCast {

    private CastingVM harness = (CastingVM) (Object) this;
    @Inject(method = "<init>(Ljava/util/List;Lat/petrak/hexcasting/api/spell/iota/Iota;ILjava/util/List;ZLat/petrak/hexcasting/api/spell/casting/CastingContext;Lat/petrak/hexcasting/api/misc/FrozenColorizer;)V",
            at = @At(value = "TAIL",
            ordinal = 0), remap = false)
    public void addTargetPlayer(CallbackInfo ci){
        CastingEnvironment ctx = harness.getEnv();
        if (ctx instanceof CircleCastEnv circleEnv){
            World world = ctx.getWorld();
            BlockPos pos = circleEnv.getImpetus().getPos();
            if (world.getBlockState(pos).getBlock().getDefaultState().equals(OneironautBlockRegistry.SENTINEL_TRAP.get().getDefaultState())){
                SentinelTrapImpetusEntity trap = (SentinelTrapImpetusEntity) world.getBlockEntity(pos);
                harness.getImage().getStack().add(new EntityIota(trap.getTargetPlayer()));
            }
        }
    }
}