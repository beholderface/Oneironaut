package net.beholderface.oneironaut.registry;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import net.beholderface.oneironaut.components.BoolComponent;
import net.minecraft.util.Identifier;
import ram.talia.hexal.common.entities.WanderingWisp;

public final class OneironautComponents implements EntityComponentInitializer {
    public static final ComponentKey<BoolComponent> WISP_DECORATIVE = ComponentRegistry.getOrCreate(new Identifier("oneironaut", "wisp_decorative"), BoolComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(WanderingWisp.class, WISP_DECORATIVE, BoolComponent::new);
        //registry.registerFor(BaseCastingWisp.class, WISP_VOLUME, DoubleComponent::new);
        //registry.beginRegistration(BaseCastingWisp.class, WISP_VOLUME);
    }

    /*@Override
    public void registerItemComponentFactories(ItemComponentFactoryRegistry registry) {
    }*/
}
