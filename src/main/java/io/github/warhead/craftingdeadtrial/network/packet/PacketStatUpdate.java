package io.github.warhead.craftingdeadtrial.network.packet;

import cpw.mods.fml.common.network.Player;
import io.github.warhead.craftingdeadtrial.io.IStat;
import io.github.warhead.craftingdeadtrial.network.PacketTypeHandler;
import io.github.warhead.craftingdeadtrial.util.PacketUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Handles an update to a {@link IStat}.
 * <p>
 * Server -> Client
 *
 * @author 501warhead
 * @see IStat#preparePacket()
 */
public class PacketStatUpdate extends PacketCDT {

    //The Type of Stat this is for
    private String id;
    //The Data of this Stat
    private NBTTagCompound data;

    /**
     * A default constructor used for class instantiation
     */
    public PacketStatUpdate() {
        super(PacketTypeHandler.STAT_UPDATE);
    }

    /**
     * Constructs a new packet for a specific type of stat with an appropriate NBTTagCompound containing data
     *
     * @param type The type of stat this packet is for
     * @param data The data to send
     */
    public PacketStatUpdate(IStat.Type type, NBTTagCompound data) {
        //Call our super with the PacketHandlerType of this class
        super(PacketTypeHandler.STAT_UPDATE);
        //Set the type of Stat
        this.id = type.name();
        //Set the Data
        this.data = data;
    }

    @Override
    public void readData(DataInputStream data) throws IOException {
        //Read our string using Mojang-cloned methods. Works up to 255
        this.id = PacketUtil.readString(data, 255);
        //Read the size of our data for array population
        short size = data.readShort();
        if (size < 0) { //If somehow the size of the data is empty
            throw new IOException("NBTData was empty");
        } else {
            //Create a new byte array
            byte[] bytes = new byte[size];
            //Read the data into the new array fully
            data.readFully(bytes);
            //Use Mojang's CompressedStreamTools to decompile this into a NBTTagCompound
            this.data = CompressedStreamTools.decompress(bytes);
        }
    }

    @Override
    public void writeData(DataOutputStream data) throws IOException {
        //Check to see if the string is too big to be handled by provided methods
        if (id.length() > 32767) {
            throw new IOException("String too big");
        } else {
            //Write the length of the string first into the data
            data.writeShort(id.length());
            //Write the characters of the string
            data.writeChars(id);
        }
        //Compress our NBTTagCompound into a Byte array for transfer
        byte[] dt = CompressedStreamTools.compress(this.data);
        //Write the length of the Byte array first for later use
        data.writeShort(dt.length);
        //Write the data of the NBTTagCompound into the byte array
        data.write(dt);
    }

    @Override
    public void execute(INetworkManager manager, Player player) {
        //Get the type of Stat this is for from the String ID
        IStat.Type type = IStat.Type.valueOf(id);
        //Cast the Player interface to something more workable
        EntityPlayer pl = (EntityPlayer) player;
        try {
            //Create a new instance of this stat with default values
            IStat stat = type.getClazz().newInstance();
            //Populate the instance of this stat with the NBTTagCompound that we received
            stat.readFromTag(data);
            //Write the data from this stat to the entity data of the player so that it is persistent on client side. Might not be needed.
            stat.writeToTag(pl.getEntityData());
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace(System.err);
        }
    }
}
