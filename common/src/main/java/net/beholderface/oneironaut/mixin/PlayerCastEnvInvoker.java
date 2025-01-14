package net.beholderface.oneironaut.mixin;

import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PlayerBasedCastEnv.class)
public interface PlayerCastEnvInvoker {
    @Invoker("extractMediaFromInventory")
    long extract(long costLeft, boolean allowOvercast, boolean simulate);
    //naming it just canOvercast results in an infinite loop
    @Invoker("canOvercast")
    boolean canItOvercast();
}
