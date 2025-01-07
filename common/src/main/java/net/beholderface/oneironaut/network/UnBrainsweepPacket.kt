package net.beholderface.oneironaut.network

import at.petrak.hexcasting.common.msgs.IMessage
import io.netty.buffer.ByteBuf
import net.beholderface.oneironaut.Oneironaut
import net.beholderface.oneironaut.unbrainsweep
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.mob.MobEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

class UnBrainsweepPacket(val patientID : Int) : IMessage {
    override fun serialize(buf: PacketByteBuf?) {
        buf!!.writeInt(patientID)
    }

    override fun getFabricId(): Identifier {
        return ID
    }

    companion object {
        @JvmField
        val ID: Identifier = Identifier(Oneironaut.MOD_ID, "unbrainsweep")

        @JvmStatic
        fun deserialise(buffer: ByteBuf): UnBrainsweepPacket {
            val buf = PacketByteBuf(buffer)

            return UnBrainsweepPacket(buf.readInt())
        }

        @JvmStatic
        fun handle(self: UnBrainsweepPacket) {
            MinecraftClient.getInstance().execute {
                val world = MinecraftClient.getInstance().world ?: return@execute
                val patient = world.getEntityById(self.patientID) as MobEntity
                patient.unbrainsweep()
            }
        }
    }
}