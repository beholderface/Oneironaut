package net.oneironaut.casting;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class DoubleComponent implements AutoSyncedComponent {
    private double value = 1f;
    private Entity entity;

    //public static final ComponentKey<DoubleComponent> VOLUME = ComponentRegistry.getOrCreate(new Identifier("oneironaut", "wisp_volume"), DoubleComponent.class);


    public DoubleComponent(Entity e){
        this.entity = e;
    }
    public double getValue() {
        return this.value;
    }

    public void setValue(double newValue) {
        this.value = newValue;
        //DoubleComponent.VOLUME.sync(this.entity);
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.value = tag.getDouble("value");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putDouble("value", this.value);
    }

    //public static final ComponentKey<DoubleComponent> WISP_VOLUME = ComponentRegistry.getOrCreate(new Identifier("oneironaut", "wisp_volume"), DoubleComponent.class);

}
