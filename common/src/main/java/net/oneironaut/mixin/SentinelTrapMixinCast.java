package net.oneironaut.mixin;

import at.petrak.hexcasting.api.block.circle.BlockEntityAbstractImpetus;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.casting.CastingHarness;
import at.petrak.hexcasting.api.spell.casting.SpellCircleContext;
import at.petrak.hexcasting.api.spell.iota.EntityIota;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.oneironaut.Oneironaut;
import net.oneironaut.block.SentinelTrapImpetus;
import net.oneironaut.block.SentinelTrapImpetusEntity;
import net.oneironaut.registry.OneironautThingRegistry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.Map;

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
            if (world.getBlockState(pos).getBlock().getDefaultState().equals(OneironautThingRegistry.SENTINEL_TRAP.get().getDefaultState())){
                SentinelTrapImpetusEntity trap = (SentinelTrapImpetusEntity) world.getBlockEntity(pos);
                harness.getStack().add(new EntityIota(trap.getTargetPlayer()));
            }
        }
    }
}