package net.oneironaut.fabric;

import at.petrak.hexcasting.fabric.cc.HexCardinalComponents;
import at.petrak.hexcasting.fabric.cc.adimpl.CCMediaHolder;
import dev.onyxstudios.cca.api.v3.item.ItemComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentInitializer;
import dev.onyxstudios.cca.api.v3.item.ItemComponent;
import net.oneironaut.item.ItemStolenMediaProvider;
import org.jetbrains.annotations.NotNull;

//stolen from gloop
public class OneironautCC implements ItemComponentInitializer {
    @Override
    public void registerItemComponentFactories(@NotNull ItemComponentFactoryRegistry registry) {
        /*registry.register(OneironautItemRegistry.PSUEDOAMETHYST_SHARD.get(), HexCardinalComponents.MEDIA_HOLDER, stack -> new CCMediaHolder.Static(
                PseudoamethystShard::getMedia, 1500, stack
        ));*/


        for(ItemStolenMediaProvider stolenMediaProvider : ItemStolenMediaProvider.allStolenMediaItems){
            registry.register(stolenMediaProvider, HexCardinalComponents.MEDIA_HOLDER, stack -> new CCMediaHolder.Static(
                    stolenMediaProvider::getMediaAmount, stolenMediaProvider.getPriority(), stack
            ){
                @Override
                public int withdrawMedia(int cost, boolean simulate) {
                    ItemStolenMediaProvider thisItem = (ItemStolenMediaProvider)(stack.getItem());
                    if(thisItem.shouldUseOwnWithdrawLogic(stack)){
                        return thisItem.withdrawMedia(stack, cost, simulate);
                    }
                    return super.withdrawMedia(cost, simulate);
                }
            });
        }

    }
}
