package io.github.warhead.craftingdeadtrial.io;

import io.github.warhead.craftingdeadtrial.energy.EnergyHandler;
import io.github.warhead.craftingdeadtrial.network.packet.PacketCDT;
import io.github.warhead.craftingdeadtrial.thirst.ThirstHandler;
import io.github.warhead.craftingdeadtrial.tick.TickPeriod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * The basic framework for all player "stats" within this mod.
 * <p>
 * Each player will have one of each variant type of Stat. The data for the stats are saved to {@link Entity#getEntityData()}
 *
 * @author 501warhead
 * @see EnergyHandler
 * @see ThirstHandler
 * @see ServerDataHandler
 */
public interface IStat {

    /**
     * Perform a PlayerTick-based update on this stat to provide whatever logic needed to be performed in that tick
     *
     * @param player The player to update for
     * @param world  The world the update is being performed in
     * @param period The period in which the tick is happening. Data is sent to the client on {@link TickPeriod#START}
     * @see cpw.mods.fml.common.TickType#PLAYER
     */
    void update(EntityPlayer player, World world, TickPeriod period);

    /**
     * This method is here to tell when the Stat needs to be sent to the client to update.
     * <p>
     * If the value returned is {@code True} then an update will be sent the player this stat is for.
     *
     * @return Whether or not something significant has changed with this stat.
     */
    boolean hasChanged();

    /**
     * This is called when the client is sent an update packet about this stat.
     * This should be used to set values for {@link #hasChanged()} to the new values so that hasChanged returns false until a new change happens.
     */
    void onSendClientUpdate();

    /**
     * Prepare a packet to be sent to the client in order to update them with new information
     *
     * @return The CraftingDeadTrial packet to send
     */
    PacketCDT preparePacket();

    /**
     * Writes this stat to a tag compound.
     * <p>
     * The most common compound to be written to will be {@link Entity#getEntityData()}
     *
     * @param comp The compound to be written to
     * @return The result of the compound passed in combined with new data
     */
    NBTTagCompound writeToTag(NBTTagCompound comp);

    /**
     * Read the data from a compound tag into this Stat to update it's values.
     * <p>
     * The most common compound to be read from will be {@link Entity#getEntityData()}
     *
     * @param comp The tag compound to read from.
     */
    void readFromTag(NBTTagCompound comp);

    /**
     * An enum value to determine the type of this stat for class instantiation.
     */
    enum Type {
        /**
         * The energy stat, responsible for whether or not a player can perform an action
         */
        ENERGY(EnergyHandler.class),
        /**
         * The thirst stat, responsible for keeping track of how thirsty a player is or is not.
         */
        THIRST(ThirstHandler.class);

        private final Class<? extends IStat> clazz;

        Type(Class<? extends IStat> clazz) {
            this.clazz = clazz;
        }

        public Class<? extends IStat> getClazz() {
            return clazz;
        }
    }
}
