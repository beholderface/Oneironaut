package net.beholderface.oneironaut.casting

import at.petrak.hexcasting.api.HexAPI.modLoc
import at.petrak.hexcasting.api.misc.FrozenColorizer
import at.petrak.hexcasting.common.network.IMessage
import at.petrak.hexcasting.common.lib.HexSounds
import at.petrak.hexcasting.common.particles.ConjureParticleOptions
import io.netty.buffer.ByteBuf
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.entity.projectile.ExplosiveProjectileEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.sound.SoundCategory
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.beholderface.oneironaut.Oneironaut
import net.beholderface.oneironaut.OneironautClient
import ram.talia.hexal.api.nextColour
import java.util.ConcurrentModificationException
import java.util.UUID


class FireballUpdatePacket(val targetVelocity : Vec3d, val entity : ExplosiveProjectileEntity?) : IMessage {
    override fun serialize(buf: PacketByteBuf) {
        buf.writeDouble(targetVelocity.x)
        buf.writeDouble(targetVelocity.y)
        buf.writeDouble(targetVelocity.z)
        entity?.x?.let { buf.writeDouble(it) }
        entity?.y?.let { buf.writeDouble(it) }
        entity?.z?.let { buf.writeDouble(it) }
        entity?.id?.let { buf.writeInt(it) }
    }

    override fun getFabricId() = ID

    companion object {
        @JvmField
        val ID: Identifier = Identifier(Oneironaut.MOD_ID, "fireballupdate")

        @JvmStatic
        fun deserialise(buffer: ByteBuf): FireballUpdatePacket {
            val buf = PacketByteBuf(buffer)
            val targetVelocity = Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble())
            val entityPos = Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble())
            //val entityUUID = buf.readUuid()
            val entityID = buf.readInt()
            var foundEntity : ExplosiveProjectileEntity? = null
                    val world = MinecraftClient.getInstance().world
                    foundEntity = world?.getEntityById(entityID) as ExplosiveProjectileEntity
            return FireballUpdatePacket(targetVelocity, foundEntity)
        }

        @JvmStatic
        fun handle(self: FireballUpdatePacket) {
            MinecraftClient.getInstance().execute {
                val targetVelocity = self.targetVelocity
                val entityToUpdate = self.entity
                if (entityToUpdate != null){
                    //OneironautClient.updatedFireballMap[entityToUpdate] = targetVelocity
                    //doing it all here gave me a ConcurrentModificationException
                    try {
                        entityToUpdate.powerX = targetVelocity.x
                        entityToUpdate.powerY = targetVelocity.y
                        entityToUpdate.powerZ = targetVelocity.z
                    } catch (e : ConcurrentModificationException){
                        Oneironaut.LOGGER.debug("oopsie, concurrent modification!\n$e")
                    }
                }
            }
        }
    }
}