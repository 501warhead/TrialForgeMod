package io.github.warhead.craftingdeadtrial.tick;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import io.github.warhead.craftingdeadtrial.energy.EnergyHandler;
import io.github.warhead.craftingdeadtrial.io.IStat;
import io.github.warhead.craftingdeadtrial.io.ServerDataHandler;
import io.github.warhead.craftingdeadtrial.network.PacketTypeHandler;
import io.github.warhead.craftingdeadtrial.thirst.ThirstHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.util.EnumSet;
import java.util.Set;

/**
 * The TickHandler which handles updating the stats of players, triggering tick-based logic.
 *
 * @author 501warhead
 * @see EnergyHandler#update(EntityPlayer, World, TickPeriod)
 * @see ThirstHandler#update(EntityPlayer, World, TickPeriod)
 */
public class PlayerTickHandler implements ITickHandler {

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData) {
        //Get the player
        EntityPlayer pl = (EntityPlayer) tickData[0];
        if (!pl.worldObj.isRemote) { //If this tick is server side (it should always be, but never assume.)
            //Grab the stats for this player
            Set<IStat> stats = ServerDataHandler.getInstance().getData(pl.username);
            if (stats != null) { //If the stats were present
                for (IStat stat : stats) { //Loop through all stats
                    //Perform a tick-based update on this stat
                    stat.update(pl, pl.worldObj, TickPeriod.START);
                    if (stat.hasChanged()) { //If the stat has changed since the last time this was run (e.g. during the update)
                        //Write the stat data to the entitydata
                        stat.writeToTag(pl.getEntityData());
                        //Trigger this method to update the hasChanged values, if needed
                        stat.onSendClientUpdate();
                        //Send the packet to the player to update them
                        PacketDispatcher.sendPacketToPlayer(PacketTypeHandler.populatePacket(stat.preparePacket()), (Player) pl);
                    }
                }
            }
        }
    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData) {
        //Get the player
        EntityPlayer pl = (EntityPlayer) tickData[0];
        if (!pl.worldObj.isRemote) { //If this tick is server side (it should always be, but never assume.)
            //Get the players stats
            Set<IStat> stats = ServerDataHandler.getInstance().getData(pl.username);
            if (stats != null) { //If the player has stats
                for (IStat stat : stats) { //Loop through stats
                    //Perform a tick-based update on this stat
                    stat.update(pl, pl.worldObj, TickPeriod.END);
                }
            }
        }
    }

    @Override
    public EnumSet<TickType> ticks() {
        //Set this to listen to player ticks only
        EnumSet<TickType> type = EnumSet.noneOf(TickType.class);
        type.add(TickType.PLAYER);
        return type;
    }

    @Override
    public String getLabel() {
        //A simple label for debugging purposes
        return "CDT-Player";
    }
}
