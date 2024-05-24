package net.oneironaut.registry;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import net.minecraft.util.Identifier;
import net.oneironaut.casting.DoubleComponent;
import ram.talia.hexal.common.entities.BaseCastingWisp;

public final class OneironautComponents implements EntityComponentInitializer/*, ItemComponentInitializer*/ {
    //public static final ComponentKey<DoubleComponent> WISP_VOLUME = ComponentRegistry.getOrCreate(new Identifier("oneironaut", "wisp_volume"), DoubleComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        //registry.registerFor(BaseCastingWisp.class, WISP_VOLUME, DoubleComponent::new);
        //registry.beginRegistration(BaseCastingWisp.class, WISP_VOLUME);
    }

    /*@Override
    public void registerItemComponentFactories(ItemComponentFactoryRegistry registry) {
    }*/
}
