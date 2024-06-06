package net.beholderface.oneironaut.components;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;

public class BoolComponent implements AutoSyncedComponent {
    private boolean value = false;
    private Entity entity;

    public BoolComponent(Entity e){
        this.entity = e;
    }
    public boolean getValue() {
        return this.value;
    }

    public void setValue(boolean newValue) {
        this.value = newValue;
        //DoubleComponent.VOLUME.sync(this.entity);
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.value = tag.getBoolean("value");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putBoolean("value", this.value);
    }

}
