package net.beholderface.oneironaut.fabric;

import at.petrak.hexcasting.fabric.cc.HexCardinalComponents;
import at.petrak.hexcasting.fabric.cc.adimpl.CCMediaHolder;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.item.ItemComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentInitializer;
import net.beholderface.oneironaut.components.BoolComponent;
import net.minecraft.util.Identifier;
import net.beholderface.oneironaut.components.DoubleComponent;
import net.beholderface.oneironaut.item.ItemStolenMediaProvider;
import org.jetbrains.annotations.NotNull;
import ram.talia.hexal.common.entities.WanderingWisp;

import static net.beholderface.oneironaut.registry.OneironautComponents.WISP_DECORATIVE;

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
                public long withdrawMedia(long cost, boolean simulate) {
                    ItemStolenMediaProvider thisItem = (ItemStolenMediaProvider)(stack.getItem());
                    if(thisItem.shouldUseOwnWithdrawLogic(stack)){
                        return thisItem.withdrawMedia(stack, cost, simulate);
                    }
                    return super.withdrawMedia(cost, simulate);
                }
            });
        }
    }

    //public static final ComponentKey<DoubleComponent> WISP_VOLUME = ComponentRegistry.getOrCreate(new Identifier("oneironaut", "wisp_volume"), DoubleComponent.class);
    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(WanderingWisp.class, WISP_DECORATIVE, BoolComponent::new);
        //registry.registerFor(BaseCastingWisp.class, WISP_VOLUME, DoubleComponent::new);
    }
}
