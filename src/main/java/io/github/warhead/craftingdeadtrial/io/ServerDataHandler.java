package io.github.warhead.craftingdeadtrial.io;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import io.github.warhead.craftingdeadtrial.energy.EnergyHandler;
import io.github.warhead.craftingdeadtrial.network.PacketTypeHandler;
import io.github.warhead.craftingdeadtrial.thirst.ThirstHandler;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Map;
import java.util.Set;

/**
 * A class to hold data for the server side of the mod, keeping track of all the data for the various players.
 *
 * @author 501warhead
 */
public class ServerDataHandler {

    private static ServerDataHandler INSTANCE = new ServerDataHandler();
    private Map<String, Set<IStat>> data;

    private ServerDataHandler() {
        data = Maps.newHashMap();
    }

    /**
     * @return The singleton instance of the ServerDataHandler. Only relevant on {@link cpw.mods.fml.relauncher.Side#SERVER}
     */
    public static ServerDataHandler getInstance() {
        return INSTANCE;
    }

    public Set<IStat> getData(String key) {
        return data.get(key);
    }

    public Set<IStat> getData(EntityPlayer player) {
        return getData(player.username);
    }

    /**
     * Gets the specified stat data for a player
     *
     * @param username The username of the player to get the data for
     * @param type     The type of data to get
     * @return The Stat Handler instance for the player of the specified type
     */
    public IStat getData(String username, IStat.Type type) {
        //Get the stat from our hashmap
        Set<IStat> stat = data.get(username);
        if (stat != null) {
            //Loop through the players stats to find the one that matches the correct criteria
            for (IStat s : stat) {
                //If the stat is the instance of the type we're looking for then return this stat as it's correct.
                if (type.getClazz().isInstance(s)) {
                    return s;
                }
            }
            return null;
        } else {
            return null;
        }
    }

    /**
     * Gets the specified stat data for a player
     *
     * @param player The player to retrieve data for
     * @param type   The type of data to get
     * @return The Stat Handler instance for the player of the specified type
     */
    public IStat getData(EntityPlayer player, IStat.Type type) {
        return getData(player.username, type);
    }

    /**
     * Initializes the stat data for a player on login.
     *
     * @param player The player to instantiate data for.
     */
    public void initPlayer(Player player) {
        //Get the player from the Player interface
        EntityPlayer pl = (EntityPlayer) player;
        //Prepare the Set for the new data
        Set<IStat> data = Sets.newHashSet();
        //Construct Energy and Thirst from the custom NBT data for the player
        data.add(ThirstHandler.getOrMakeFromNBT(pl.getEntityData()));
        data.add(EnergyHandler.getOrMakeFromNBT(pl.getEntityData()));
        //Add the new data to the hashmap
        this.data.put(pl.username, data);
        //Loop through the new stats and send update packets to the player to ensure Sync
        for (IStat stat : data) {
            PacketDispatcher.sendPacketToPlayer(PacketTypeHandler.populatePacket(stat.preparePacket()), (Player) pl);
        }
    }
}
