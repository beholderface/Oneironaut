package net.beholderface.oneironaut.network

import at.petrak.hexcasting.common.msgs.IMessage
import io.netty.buffer.ByteBuf
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.decoration.ItemFrameEntity
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.beholderface.oneironaut.Oneironaut


class ItemUpdatePacket(val stack : ItemStack, val entity : Entity?) : IMessage {
    override fun serialize(buf: PacketByteBuf) {
        buf.writeItemStack(stack)
        entity?.id?.let { buf.writeInt(it) }
    }

    override fun getFabricId() = ID

    companion object {
        @JvmField
        val ID: Identifier = Identifier(Oneironaut.MOD_ID, "itemupdate")

        @JvmStatic
        fun deserialise(buffer: ByteBuf): ItemUpdatePacket {
            val buf = PacketByteBuf(buffer)
            val newStack = buf.readItemStack()
            val entityID = buf.readInt()
            val world = MinecraftClient.getInstance().world
            return ItemUpdatePacket(newStack, world?.getEntityById(entityID))
        }

        @JvmStatic
        fun handle(self: ItemUpdatePacket) {
            MinecraftClient.getInstance().execute {
                val stack = self.stack
                val entity = self.entity
                if (entity != null){
                    if (entity is ItemFrameEntity){
                        val frame = entity as ItemFrameEntity
                        frame.heldItemStack = stack
                    } else if (entity is ItemEntity){
                        val item = entity as ItemEntity
                        item.stack = stack
                    }
                }
            }
        }
    }
}