package net.beholderface.oneironaut.casting
import net.minecraft.client.sound.MovingSoundInstance
import net.minecraft.client.sound.SoundInstance
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.sound.SoundCategory
import ram.talia.hexal.common.lib.HexalSounds


//I tried to get the rod to make wisp sounds while casting, didn't care enough to finish
class RodCastInstance(val caster : PlayerEntity)
    : MovingSoundInstance(HexalSounds.WISP_CASTING_CONTINUE.mainEvent, SoundCategory.PLAYERS, SoundInstance.createRandom()) {
    override fun getX() = caster.x
    override fun getY() = caster.y
    override fun getZ() = caster.z

    private var active: Boolean
    private var keepAlive = 0

    init {
        repeat = true
        active = true
        volume = 0.05f // initialises to this, increases later
        repeatDelay = 0
        keepAlive()
    }

    fun fadeOut() {
        active = false
    }

    fun keepAlive() {
        keepAlive = 3
    }

    override fun tick() {

        if (active) {
            volume = Math.min(1f, volume + .25f)
            keepAlive--
            if (keepAlive == 0)
                fadeOut()
            return
        }
        volume = Math.max(0f, volume - .25f)
        if (volume < 0.00001)
            setDone()
    }
}