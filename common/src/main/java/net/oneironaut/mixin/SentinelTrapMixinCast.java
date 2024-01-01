package net.oneironaut.mixin;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.casting.CastingHarness;
import at.petrak.hexcasting.api.spell.casting.SpellCircleContext;
import at.petrak.hexcasting.api.spell.iota.EntityIota;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.oneironaut.block.SentinelTrapImpetusEntity;
import net.oneironaut.registry.OneironautBlockRegistry;
import net.oneironaut.registry.OneironautItemRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("ConstantConditions")
@Mixin(CastingHarness.class)
public abstract class SentinelTrapMixinCast {

    private CastingHarness harness = (CastingHarness) (Object) this;
    @Inject(method = "<init>(Ljava/util/List;Lat/petrak/hexcasting/api/spell/iota/Iota;ILjava/util/List;ZLat/petrak/hexcasting/api/spell/casting/CastingContext;Lat/petrak/hexcasting/api/misc/FrozenColorizer;)V",
            at = @At(value = "TAIL",
            ordinal = 0), remap = false)
    public void addTargetPlayer(CallbackInfo ci){
        CastingContext ctx = harness.getCtx();
        if (ctx.getSource().equals(CastingContext.CastSource.SPELL_CIRCLE)){
            SpellCircleContext circlectx = ctx.getSpellCircle();
            World world = ctx.getWorld();
            BlockPos pos = circlectx.getImpetusPos();
            if (world.getBlockState(pos).getBlock().getDefaultState().equals(OneironautBlockRegistry.SENTINEL_TRAP.get().getDefaultState())){
                SentinelTrapImpetusEntity trap = (SentinelTrapImpetusEntity) world.getBlockEntity(pos);
                harness.getStack().add(new EntityIota(trap.getTargetPlayer()));
            }
        }
    }
}