package net.beholderface.oneironaut.casting;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.ListIota;
import at.petrak.hexcasting.api.casting.iota.NullIota;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public class RodState {

    public RodState(Entity player, boolean active){
        this.timestamp = player.getWorld().getTime();
        this.initialLook = player.getRotationVector();
        this.initialPos = player.getEyePos();
        this.delay = 0;
        this.resetCooldown = 20;
        this.ownerID = player.getUuid();
        this.currentlyCasting = active;
        this.castInProgress = false;
        this.storedIota = new NullIota();
    }

    private final long timestamp;
    private final Vec3d initialLook;
    private final Vec3d initialPos;
    private int delay;
    private int resetCooldown;
    private final UUID ownerID;
    //is a cast loop active?
    private boolean currentlyCasting;
    //is a pattern list currently being processed?
    private boolean castInProgress;
    private Iota storedIota;

    public long getTimestamp(){
        return this.timestamp;
    }

    public Vec3d getInitialPos(){
        return this.initialPos;
    }

    public Vec3d getInitialLook(){
        return this.initialLook;
    }

    public int getDelay(){
        return this.delay;
    }
    public int setDelay(int newDelay){
        //why would you ever want an hour of delay between casts? and negative delay doesn't make sense, it's not like you can schedule a cast in the past
        int boundedDelay = Math.max(0, Math.min(20 * 60 * 60, newDelay));
        this.delay = boundedDelay;
        return boundedDelay - newDelay;
    }
    public int adjustDelay(int delta){
        return this.setDelay(this.getDelay() + delta);
    }

    public int getResetCooldown(){
        return this.resetCooldown;
    }
    public int setResetCooldown(int cooldown){
        int boundedCooldown = Math.max(0, Math.min(20 * 60 * 60, cooldown));
        this.resetCooldown = boundedCooldown;
        return boundedCooldown - cooldown;
    }

    public UUID getOwnerID(){
        return this.ownerID;
    }

    public boolean getCurrentlyCasting() {
        return currentlyCasting;
    }

    public void setCurrentlyCasting(boolean newBool) {
        this.currentlyCasting = newBool;
    }

    public void stopCasting(){
        this.currentlyCasting = false;
    }

    public Iota getStoredIota(){
        return this.storedIota;
    }
    public boolean setStoredIota(Iota newIota){
        if (newIota.getType().equals(ListIota.TYPE)){
            return false;
        }
        this.storedIota = newIota;
        return true;
    }
    public void setCastInProgress(boolean newValue){
        this.castInProgress = newValue;
    }
    public boolean getCastingInProgress(){
        return this.castInProgress;
    }
}
