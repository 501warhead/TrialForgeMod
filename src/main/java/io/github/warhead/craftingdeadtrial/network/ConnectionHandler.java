package io.github.warhead.craftingdeadtrial.network;

import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.Player;
import io.github.warhead.craftingdeadtrial.io.ServerDataHandler;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.server.MinecraftServer;

/**
 * A simple implementation of {@link IConnectionHandler}
 * <p>
 * When a player joins the server init the values stored in their entity NBT
 * Once this is done send an update to the client to refresh values
 *
 * @author 501warhead
 * @see ServerDataHandler
 * @see io.github.warhead.craftingdeadtrial.energy.EnergyHandler
 * @see io.github.warhead.craftingdeadtrial.thirst.ThirstHandler
 */
public class ConnectionHandler implements IConnectionHandler {

    @Override
    public void playerLoggedIn(Player player, NetHandler netHandler, INetworkManager manager) {
        //Setup all the stats this player needs and send the data back to them to ensure sync
        ServerDataHandler.getInstance().initPlayer(player);
    }

    @Override
    public String connectionReceived(NetLoginHandler netHandler, INetworkManager manager) {
        //NO:OP
        return null;
    }

    @Override
    public void connectionOpened(NetHandler netClientHandler, String server, int port, INetworkManager manager) {
        //NO:OP
    }

    @Override
    public void connectionOpened(NetHandler netClientHandler, MinecraftServer server, INetworkManager manager) {
        //NO:OP
    }

    @Override
    public void connectionClosed(INetworkManager manager) {
        //NO:OP
    }

    @Override
    public void clientLoggedIn(NetHandler clientHandler, INetworkManager manager, Packet1Login login) {
        //NO:OP
    }
}
