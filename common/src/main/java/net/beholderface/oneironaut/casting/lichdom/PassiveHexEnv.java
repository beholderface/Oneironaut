package net.beholderface.oneironaut.casting.lichdom;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;

public class PassiveHexEnv extends PlayerBasedCastEnv {
    public final LichData lichData;
    protected PassiveHexEnv(ServerPlayerEntity caster, LichData lichData) {
        super(caster, Hand.OFF_HAND);
        this.lichData = lichData;
    }

    @Override
    protected long extractMediaEnvironment(long cost, boolean simulate) {
        return this.lichData.withdrawMedia(cost, simulate);
    }

    @Override
    public Hand getCastingHand() {
        return Hand.OFF_HAND;
    }

    @Override
    public FrozenPigment getPigment() {
        return IXplatAbstractions.INSTANCE.getPigment(this.caster);
    }
}
