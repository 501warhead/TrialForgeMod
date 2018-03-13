package io.github.warhead.craftingdeadtrial.energy;

import io.github.warhead.craftingdeadtrial.io.IStat;
import io.github.warhead.craftingdeadtrial.network.packet.PacketCDT;
import io.github.warhead.craftingdeadtrial.network.packet.PacketStatUpdate;
import io.github.warhead.craftingdeadtrial.tick.TickPeriod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * The handler for Energy related data.
 * <p>
 * Each player will have one instance of this on the server side.
 *
 * @author 501warhead
 */
public final class EnergyHandler implements IStat {

    public static final IStat.Type Type = IStat.Type.ENERGY;
    //The current energy the player has
    private int currentEnergy;
    //The maximum amount of energy the player has, they'll get this when they sleep.
    private int maxEnergy;
    //The previous energy of the player, used to determine if a packet should be sent
    private int prevEnergy;
    //The previous maximum energy of the player, used to determine if a packet should be sent
    private int prevMaxEnergy;
    //The pending maximum energy increase that comes when the player next sleeps
    private int pendingMaxIncrease;

    /**
     * A default constructor that sets the various variables to default values.
     */
    public EnergyHandler() {
        this.currentEnergy = 100;
        this.maxEnergy = 100;
        this.pendingMaxIncrease = 0;
    }

    /**
     * Get the EnergyHandler from the NBTCompound data.
     *
     * @param compound The compound to read from
     * @return The energy handler if the tag compound has a key of "currentEnergy" - will be null if there is no key!
     */
    public static EnergyHandler getFromNBT(NBTTagCompound compound) {
        if (!compound.hasKey("currentEnergy")) {
            return null;
        }
        EnergyHandler handler = new EnergyHandler();
        handler.readFromTag(compound);
        return handler;
    }

    /**
     * Returns an EnergyHandler that was either made from the NBT compound or a new instance entirely.
     *
     * @param compound The tag compound to get from. Must contain a key of "currentEnergy" and "maxEnergy" - both {@code Integers}
     * @return The Energy Handler
     */
    public static EnergyHandler getOrMakeFromNBT(NBTTagCompound compound) {
        if (!compound.hasKey("currentEnergy")) {
            return new EnergyHandler();
        }
        EnergyHandler handler = new EnergyHandler();
        handler.readFromTag(compound);
        return handler;
    }

    @Override
    public void update(EntityPlayer player, World world, TickPeriod period) {
    }

    /**
     * @return This players current Energy level
     */
    public int getCurrentEnergy() {
        return currentEnergy;
    }

    /**
     * Set the current energy of the player
     *
     * @param currentEnergy The energy to set
     */
    public void setCurrentEnergy(int currentEnergy) {
        this.currentEnergy = currentEnergy;
    }

    /**
     * Get the maximum amount of energy the player can have. This number will be what current energy is set to when the player rests or {@link #resetEnergy()} is called
     *
     * @return The maximum energy this player has
     */
    public int getMaxEnergy() {
        return maxEnergy;
    }

    /**
     * Set the maximum energy the player can get when {@link #resetEnergy()} is called.
     *
     * @param maxEnergy The maximum energy this player can reach
     */
    public void setMaxEnergy(int maxEnergy) {
        this.maxEnergy = maxEnergy;
    }

    /**
     * Attempt an action perform and check to see if it is capable of being done.
     *
     * @param action The action to attempt
     * @return whether or not the player succeeded in performing an action
     */
    public boolean onAction(EnergyActionType action) {
        this.currentEnergy -= action.energyUsed;
        if (currentEnergy < 0) {
            currentEnergy += action.energyUsed;
            return false;
        }
        return currentEnergy > 0;
    }

    /**
     * Check to see whether this player is capable of performing an action
     *
     * @param action The action to check
     * @return Whether or not the player can succeed in performing an action
     */
    public boolean canDoAction(EnergyActionType action) {
        return currentEnergy > action.energyUsed;
    }

    /**
     * Gets the pending maximum increase. This will be added to {@link #getMaxEnergy()} when {@link #resetEnergy()} is called
     *
     * @return The pending maximum increase
     */
    public int getPendingMaxIncrease() {
        return pendingMaxIncrease;
    }

    /**
     * Set the pending maximum energy increase. This will be added to {@link #getMaxEnergy()} when {@link #resetEnergy()} is called.
     *
     * @param pendingMaxIncrease The new Pending Max Increase.
     */
    public void setPendingMaxIncrease(int pendingMaxIncrease) {
        this.pendingMaxIncrease = pendingMaxIncrease;
    }

    /**
     * Increments {@link #getMaxEnergy()} by {@link #getPendingMaxIncrease()} and then sets {@link #getCurrentEnergy()} to the combined total.
     * <p>
     * Called when the player sleeps.
     */
    public void resetEnergy() {
        this.maxEnergy += this.pendingMaxIncrease;
        this.pendingMaxIncrease = 0;
        this.currentEnergy = this.maxEnergy;
    }

    @Override
    public PacketCDT preparePacket() {
        return new PacketStatUpdate(Type, writeToTag(new NBTTagCompound()));
    }

    @Override
    public NBTTagCompound writeToTag(NBTTagCompound comp) {
        comp.setInteger("currentEnergy", currentEnergy);
        comp.setInteger("maxEnergy", maxEnergy);
        comp.setInteger("pendingMaxIncrease", pendingMaxIncrease);
        return comp;
    }

    @Override
    public void readFromTag(NBTTagCompound comp) {
        if (!comp.hasKey("currentEnergy")) {
            return;
        }
        this.currentEnergy = comp.getInteger("currentEnergy");
        this.maxEnergy = comp.getInteger("maxEnergy");
        this.pendingMaxIncrease = comp.getInteger("pendingMaxIncrease");
    }

    @Override
    public boolean hasChanged() {
        return prevEnergy != currentEnergy || maxEnergy != prevMaxEnergy;
    }

    @Override
    public void onSendClientUpdate() {
        this.prevEnergy = currentEnergy;
        this.prevMaxEnergy = maxEnergy;
    }
}
