package net.beholderface.oneironaut.network

import at.petrak.hexcasting.common.msgs.IMessage
import io.netty.buffer.ByteBuf
import net.beholderface.oneironaut.Oneironaut
import net.minecraft.client.MinecraftClient
import net.minecraft.network.PacketByteBuf
import net.minecraft.registry.Registries
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.util.Identifier

class SpoopyScreamPacket(val sound : SoundEvent, val pitch : Float) : IMessage {
    override fun serialize(buf: PacketByteBuf) {
        buf.writeIdentifier(sound.id)
        buf.writeFloat(pitch)
    }

    override fun getFabricId(): Identifier {
        return ID
    }

    companion object {
        @JvmField
        val ID: Identifier = Identifier(Oneironaut.MOD_ID, "scream")

        @JvmStatic
        fun deserialise(buffer: ByteBuf): SpoopyScreamPacket {
            val buf = PacketByteBuf(buffer)
            val sound = Registries.SOUND_EVENT.get(buf.readIdentifier())

            return SpoopyScreamPacket(sound!!, buf.readFloat())
        }

        @JvmStatic
        fun handle(self: SpoopyScreamPacket) {
            MinecraftClient.getInstance().execute {
                val world = MinecraftClient.getInstance().world ?: return@execute
                val you = MinecraftClient.getInstance().player!!
                val pos = you.eyePos.subtract(you.rotationVector)
                world.playSound(pos.x, pos.y, pos.z, self.sound, SoundCategory.HOSTILE, 3f, self.pitch, true)
            }
        }
    }
}