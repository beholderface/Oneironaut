package net.beholderface.oneironaut.casting

import at.petrak.hexcasting.common.network.IMessage
import at.petrak.hexcasting.fabric.cc.HexCardinalComponents
import io.netty.buffer.ByteBuf
import net.beholderface.oneironaut.Oneironaut
import net.beholderface.oneironaut.unbrainsweep
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.ai.goal.GoalSelector
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.passive.VillagerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.village.VillagerProfession

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

            /*for (i in 1 .. numLocs) {
                locs.add(Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()))
            }*/

            return UnBrainsweepPacket(buf.readInt())
        }

        @JvmStatic
        fun handle(self: UnBrainsweepPacket) {
            MinecraftClient.getInstance().execute {
                val world = MinecraftClient.getInstance().world ?: return@execute
                val patient = world.getEntityById(self.patientID) as MobEntity
                patient.unbrainsweep()

                /*val component = HexCardinalComponents.BRAINSWEPT.get(patient)
                component.isBrainswept = false
                patient.isAiDisabled = false
                val brain = patient.brain
                patient.goalSelector = GoalSelector(patient.world.profilerSupplier)
                brain.resetPossibleActivities()
                brain.refreshActivities(patient.world.timeOfDay, patient.world.time)
                if (patient is VillagerEntity){
                    val newData = patient.villagerData.withLevel(0).withProfession(VillagerProfession.NITWIT)
                    patient.villagerData = newData
                }*/
            }
        }
    }
}