package io.github.warhead.craftingdeadtrial.network.packet;

import cpw.mods.fml.common.network.Player;
import io.github.warhead.craftingdeadtrial.network.PacketTypeHandler;
import net.minecraft.network.INetworkManager;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * An abstract base for all packets CraftingDeadTrial uses.
 *
 * @author 501warhead
 */
public abstract class PacketCDT {

    public PacketTypeHandler type;

    public PacketCDT(PacketTypeHandler type) {
        this.type = type;
    }

    /**
     * Populates a {@code Byte array} with the data from this packet.
     *
     * @return The {@code Byte array}
     */
    public byte[] populate() {
        //Prepare our streams
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            //Write the type of the packet into the data
            dos.writeByte(type.ordinal());
            //Call our abstract method for children to add their own data
            this.writeData(dos);
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }

        //Return the appropriate byte array
        return bos.toByteArray();
    }

    /**
     * Populates this packet with data from an InputStream
     *
     * @param data The data to read from
     */
    public void readPopulate(DataInputStream data) {
        try {
            this.readData(data);
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * This method is called in order to populate variables within the subclasses from the data provided.
     *
     * @param data The Datastream containing the bytes of data in this packet.
     * @throws IOException Generated from various read/write error possibilities
     */
    public abstract void readData(DataInputStream data) throws IOException;

    /**
     * This method is called for subclasses to populate the data output stream with their data
     *
     * @param data The Datastream to write bytes into
     * @throws IOException Generated from various read/write error possibilities
     */
    public abstract void writeData(DataOutputStream data) throws IOException;

    /**
     * Called when the packet is received in order to perform the task needed for this packet
     *
     * @param manager The network manager
     * @param player  The player the packet is for
     */
    public abstract void execute(INetworkManager manager, Player player);
}
