package net.beholderface.oneironaut.casting.lichdom;

import at.petrak.hexcasting.api.addldata.ADHexHolder;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironmentComponent;
import at.petrak.hexcasting.api.casting.eval.env.PackagedItemCastEnv;
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.beholderface.oneironaut.Oneironaut;
import net.beholderface.oneironaut.mixin.PlayerCastEnvInvoker;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public class LichMediaExtractComponent implements CastingEnvironmentComponent.ExtractMedia.Pre {
    public final Key<ExtractMedia.Pre> key;
    public final CastingEnvironment environment;
    public LichMediaExtractComponent(CastingEnvironment env){
        this.key = new LichExtractKey();
        this.environment = env;
    }
    @Override
    public long onExtractMedia(long cost, boolean simulate) {
        if (this.environment.getCastingEntity() instanceof ServerPlayerEntity player && cost > 0 && this.environment instanceof PlayerBasedCastEnv playerEnv
        && !(player.isCreative() || player.isSpectator())){
            if (LichdomManager.isPlayerLich(player)){
                boolean canDrawFromInv = true;
                //don't extract from reservoir for things like cyphers and trinkets
                if (playerEnv instanceof PackagedItemCastEnv packagedEnv){
                    assert packagedEnv.getCaster() != null;
                    ItemStack casterStack = packagedEnv.getCaster().getStackInHand(packagedEnv.getCastingHand());
                    ADHexHolder casterHexHolder = IXplatAbstractions.INSTANCE.findHexHolder(casterStack);
                    if (casterHexHolder != null){
                        canDrawFromInv = casterHexHolder.canDrawMediaFromInventory();
                    }
                }
                if (!canDrawFromInv){
                    return cost;
                }
                //the amount to try to withdraw from the lich reservoir if the media in the player's inventory is insufficient
                long noBloodcastDeficit = ((PlayerCastEnvInvoker) playerEnv).extract(cost, false, true);
                if (noBloodcastDeficit > 0){
                    LichData data = LichdomManager.getLichData(player);
                    Oneironaut.LOGGER.info(noBloodcastDeficit / ((double) MediaConstants.DUST_UNIT) + " being withdrawn from lich reservoir, originally containing " + data.getMedia() / ((double) MediaConstants.DUST_UNIT));
                    return cost - data.withdrawMedia(noBloodcastDeficit, simulate);
                }
            }
        }
        return cost;
    }

    @Override
    public Key<ExtractMedia.Pre> getKey() {
        return this.key;
    }

    private static class LichExtractKey implements Key<Pre>{
        //wtf is this sort of class supposed to contain
        //I'm just putting this here in case the contents of the object actually matter
        private final UUID uuid;
        public LichExtractKey(){
            uuid = UUID.randomUUID();
        }
    }
}
