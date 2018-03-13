package io.github.warhead.craftingdeadtrial.network;

import io.github.warhead.craftingdeadtrial.lib.Reference;
import io.github.warhead.craftingdeadtrial.network.packet.PacketCDT;
import io.github.warhead.craftingdeadtrial.network.packet.PacketStatUpdate;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

/**
 * A scalable packet type handler to allow population of packets.
 *
 * @author 501warhead
 */
public enum PacketTypeHandler {
    /**
     * Update the values of a client's stats.
     * <p>
     * Server -> Client
     */
    STAT_UPDATE(PacketStatUpdate.class);

    //The class of the packet, used for instantiation
    private Class<? extends PacketCDT> clazz;

    PacketTypeHandler(Class<? extends PacketCDT> clazz) {
        this.clazz = clazz;
    }

    /**
     * Build a Packet from the provided byte array of data
     *
     * @param data The data to build from
     * @return A packet constructed from the data
     */
    public static PacketCDT buildPacket(byte[] data) {
        //Prepare a stream from the data for usage
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        //Get the type of packet that this is
        int sel = stream.read();
        //Prepare an input stream from our ByteArrayInputStream
        DataInputStream dstream = new DataInputStream(stream);

        //Setup the value here for scope
        PacketCDT packet = null;
        try {
            //Create a new instance of the specified packet using the ID retrieved, operates off of ordinal.
            packet = values()[sel].clazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        if (packet != null) { //If the packet creation was successful
            //Call our read method, which will also call our abstract read method for our subclasses.
            packet.readPopulate(dstream);
        } else {
            System.out.println("[CraftingDeadTrial] buildPacket created a null packet. This should be looked into.");
        }

        return packet;
    }

    /**
     * Build a new packet from an Enum value, will be devoid of data aside from defaults.
     *
     * @param handler The type of packet
     * @return A new, empty packet of the specified type.
     */
    public static PacketCDT buildPacket(PacketTypeHandler handler) {
        //Establish the packet here for scope values
        PacketCDT packet = null;
        try {
            //Using the handler passed in build a packet from the appropriate value. Selects from values for security reasons.
            packet = values()[handler.ordinal()].clazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        return packet;
    }

    /**
     * Populates a Minecraft-compatible packet from a {@link PacketCDT}
     *
     * @param packetCDT The packet to populate
     * @return A Minecraft-compatible packet
     */
    public static Packet populatePacket(PacketCDT packetCDT) {
        //Call our method to populate a byte array within the PacketCDT abstract
        byte[] data = packetCDT.populate();

        //Create a new Packet250
        Packet250CustomPayload packet250 = new Packet250CustomPayload();
        //Setup packet data from our provided data
        packet250.channel = Reference.CHANNEL_NAME;
        packet250.data = data;
        packet250.length = data.length;
        //We don't do anything with chunks or blocks so this will always be false. If this changes this will change with it.
        packet250.isChunkDataPacket = false;

        return packet250;
    }
}
