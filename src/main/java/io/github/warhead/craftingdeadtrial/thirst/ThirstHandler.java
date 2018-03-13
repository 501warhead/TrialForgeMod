package io.github.warhead.craftingdeadtrial.thirst;

import io.github.warhead.craftingdeadtrial.io.IStat;
import io.github.warhead.craftingdeadtrial.network.packet.PacketCDT;
import io.github.warhead.craftingdeadtrial.network.packet.PacketStatUpdate;
import io.github.warhead.craftingdeadtrial.tick.TickPeriod;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

/**
 * The handler for Thirst related data.
 * <p>
 * Each player will have one instance of this on the server side.
 *
 * @author 501warhead
 */
public class ThirstHandler implements IStat {

    public static final IStat.Type Type = IStat.Type.THIRST;
    //Thirst level. Similar to hunger.
    private int thirst;
    //The previous level of thirst, used to tell when to send data to the client.
    private int previousLevel;
    //The timer for damage to the player if they are at 0 thirst
    private int timer;
    //The saturation of thirst, will be used first when draining thirst
    private float thirstSaturation;
    //The "Exhaustion" level of this player's thirst, used to determine when to drain thirst.
    private float thirstExhaustion;
    //A vector storing movement when the player is ticked in order to drain thirst as needed.
    private Vec3 movement;

    /**
     * A default constructor for instantiation
     */
    public ThirstHandler() {
        thirst = 20;
        thirstSaturation = 5F;
    }

    /**
     * Get the ThirstHandler from the NBTCompound data.
     *
     * @param compound The compound to read from
     * @return The thirst handler if the tag compound has a key of "thirst" - will be null if there is no key!
     */
    public static ThirstHandler getFromNBT(NBTTagCompound compound) {
        if (!compound.hasKey("thirst")) return null;
        ThirstHandler handler = new ThirstHandler();
        handler.readFromTag(compound);
        return handler;
    }

    /**
     * Returns a ThirstHandler that was either made from the NBT compound or a new instance entirely.
     *
     * @param compound The tag compound to get from. Must contain a key of "thirst" ({@code Integer}), "thirstSaturation" ({@code Float}, "thirstExhaustion" ({@code Float}), "thirstTimer" ({@code Integer}
     * @return The Thirst Handler
     */
    public static ThirstHandler getOrMakeFromNBT(NBTTagCompound compound) {
        if (!compound.hasKey("thirst")) return new ThirstHandler();
        ThirstHandler handler = new ThirstHandler();
        handler.readFromTag(compound);
        return handler;
    }

    /**
     * Perform a tick-based update on this thirst handler.
     * <p>
     * This will operate logic to determine if thirst should decrease and at what rate.
     *
     * @param pl    The player being ticked.
     * @param world The world being ticked in.
     */
    @Override
    public void update(EntityPlayer pl, World world, TickPeriod period) {
        if (pl.capabilities.isCreativeMode) { //Don't drain thirst if the player is in creative
            return;
        }

        if (period == TickPeriod.START) { //The PlayerTick has started
            if (movement != null) { //If there is a movement vector on record
                //Create a new vector from the players current positiion
                Vec3 vector = Vec3.createVectorHelper(pl.posX, pl.posY, pl.posZ);
                //Subtract the vectors
                vector = vector.subtract(movement);
                //Absolute the result
                vector.xCoord = Math.abs(vector.xCoord);
                vector.yCoord = Math.abs(vector.yCoord);
                vector.zCoord = Math.abs(vector.zCoord);
                //Calculate the distance travelled
                int dis = (int) Math.round((Math.sqrt(vector.xCoord * vector.xCoord + vector.yCoord * vector.yCoord + vector.zCoord * vector.zCoord)) * 100.0F);

                //If the distance is more than 0 trigger a movement call for potential thirst drain.
                if (dis > 0) movementTrigger(pl, dis);
            }
        } else if (period == TickPeriod.END) { //The PlayerTick is ending
            //Set the movement vector to our current position for the tick start
            this.movement = Vec3.createVectorHelper(pl.posX, pl.posY, pl.posZ);

            if (this.thirstExhaustion > 4.0F) { //If the thirst exhaustion has stacked up over 4
                //Remove 4 from thirst exhaustion
                this.thirstExhaustion -= 4.0F;

                if (this.thirstSaturation > 0) { //If the player has thirst saturation
                    //Set thirst saturation, clamping the bottom at 0
                    this.thirstSaturation = Math.max(thirstSaturation -= 1.0F, 0.0F);
                } else if (world.difficultySetting != 0) { //If the world is not in peaceful
                    //Drain thirst by 1, down to 0
                    this.thirst = Math.max(this.thirst - 1, 0);
                }
            }
            if (this.thirst <= 0) { //If the player has run out of thirst
                //Increase our damage timer.
                ++this.timer;

                if (this.timer >= 80) { //If enough time has passed
                    if ((world.difficultySetting == 1 && pl.getHealth() > 10F) //If the difficulty is easy and the player has over 5 hearts
                            || (world.difficultySetting == 2 && pl.getHealth() > 5F) //If the difficulty is normal and the player has over 2 1/2 hearts
                            || (world.difficultySetting == 3)) { //Or if the difficulty is hard
                        //Damage the player by 1
                        pl.attackEntityFrom(DamageSource.starve, 1.0F);
                    }
                    //Reset timer
                    this.timer = 0;
                }
            } else {
                //ensure that the timer doesn't freeze and always starts from 0 if thirst is increased
                this.timer = 0;
            }

            //If the player is below 3 thirst bars
            if (pl.isSprinting() && thirst <= 6F) {
                //Cancel sprinting
                pl.setSprinting(false);
            }
        }
    }

    private void movementTrigger(EntityPlayer pl, int dis) {
        if (pl.isInsideOfMaterial(Material.water)) { //If the player is in water
            this.addThirstExhaustion(0.015F * (float) dis * 0.01F);
        } else if (pl.isInWater()) { //If the player is swimming
            this.addThirstExhaustion(0.015F * (float) dis * 0.01F);
        } else if (pl.onGround) { //If the player is on the ground
            if (pl.isSprinting()) { //If the player is sprinting
                this.addThirstExhaustion(0.099999994F * (float) dis * 0.01F);
            } else { //If the player is walking
                this.addThirstExhaustion(0.01F * (float) dis * 0.01F);
            }
        }
    }

    @Override
    public boolean hasChanged() {
        return this.thirst != this.previousLevel;
    }

    @Override
    public void onSendClientUpdate() {
        this.previousLevel = this.thirst;
    }

    /**
     * @return The current thirst levels
     */
    public int getThirst() {
        return thirst;
    }

    /**
     * Set the value for Thirst. This is clamped between 0 and 20
     *
     * @param thirst A value for thirst between 0F and 20F
     */
    public void setThirst(int thirst) {
        this.thirst = Math.max(0, Math.min(20, thirst));
    }

    /**
     * @return The current thirst saturation levels.
     */
    public float getThirstSaturation() {
        return thirstSaturation;
    }

    /**
     * Set the value for thirst saturation. This is clamped between 0 and 5.
     *
     * @param thirstSaturation The value for thirst saturation between 0F and 20F
     */
    public void setThirstSaturation(float thirstSaturation) {
        this.thirstSaturation = Math.max(0, Math.min(5F, thirstSaturation));
    }

    /**
     * Get the thirst "exhaustion" level
     * <p>
     * This increases as various actions are taken. At >4 it will trigger a thirst drain.
     *
     * @return The exhaustion level
     */
    public float getThirstExhaustion() {
        return thirstExhaustion;
    }

    /**
     * Set the thirst "exhaustion" level
     * <p>
     * This increases as various actions are taken. At >4 it will trigger a thirst drain.
     */
    public void setThirstExhaustion(float thirstExhaustion) {
        this.thirstExhaustion = thirstExhaustion;
    }

    /**
     * Get the timer for damage
     *
     * @return
     */
    public int getTimer() {
        return timer;
    }

    public boolean isThirsty() {
        return thirst < 20;
    }

    public void addThirst(int thirst, float thirstSaturation) {
        this.setThirst(this.thirst + thirst);
        this.setThirstSaturation(this.thirstSaturation + thirstSaturation);
    }

    @Override
    public PacketCDT preparePacket() {
        return new PacketStatUpdate(Type, writeToTag(new NBTTagCompound()));
    }

    public void addThirstExhaustion(float amount) {
        this.thirstExhaustion = Math.min(this.thirstExhaustion + amount, 40.0F);
    }

    @Override
    public NBTTagCompound writeToTag(NBTTagCompound comp) {
        comp.setInteger("thirst", thirst);
        comp.setFloat("thirstSaturation", thirstSaturation);
        comp.setInteger("thirstTimer", timer);
        comp.setFloat("thirstExhaustion", thirstExhaustion);
        return comp;
    }

    @Override
    public void readFromTag(NBTTagCompound comp) {
        if (!comp.hasKey("thirst")) {
            return;
        }
        this.thirst = comp.getInteger("thirst");
        this.thirstSaturation = comp.getFloat("thirstSaturation");
        this.thirstExhaustion = comp.getFloat("thirstExhaustion");
        this.timer = comp.getInteger("thirstTimer");
    }
}
