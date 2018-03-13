package io.github.warhead.craftingdeadtrial.network;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import io.github.warhead.craftingdeadtrial.network.packet.PacketCDT;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;

/**
 * A simple packet handler to work for {@link PacketCDT}'s
 *
 * @author 501wawrhead
 */
public class PacketHandler implements IPacketHandler {

    @Override
    public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
        //Build a packet from the data provided
        PacketCDT packetCDT = PacketTypeHandler.buildPacket(packet.data);

        if (packetCDT != null) { //If the packet is correct
            //Call our abstract method to allow subclasses to perform the actions they need to.
            packetCDT.execute(manager, player);
        }
    }
}
