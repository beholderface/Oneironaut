package net.oneironaut.fabric;

import at.petrak.hexcasting.fabric.cc.HexCardinalComponents;
import at.petrak.hexcasting.fabric.cc.adimpl.CCMediaHolder;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.item.ItemComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentInitializer;
import net.minecraft.util.Identifier;
import net.oneironaut.casting.DoubleComponent;
import net.oneironaut.item.ItemStolenMediaProvider;
import org.jetbrains.annotations.NotNull;
import ram.talia.hexal.common.entities.BaseCastingWisp;

//stolen from gloop
public class OneironautCC implements ItemComponentInitializer, EntityComponentInitializer {
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

    public static final ComponentKey<DoubleComponent> WISP_VOLUME = ComponentRegistry.getOrCreate(new Identifier("oneironaut", "wisp_volume"), DoubleComponent.class);
    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        //registry.registerFor(BaseCastingWisp.class, WISP_VOLUME, DoubleComponent::new);
    }
}
