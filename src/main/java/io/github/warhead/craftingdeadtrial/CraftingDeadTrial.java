package io.github.warhead.craftingdeadtrial;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import io.github.warhead.craftingdeadtrial.blocks.ModBlocks;
import io.github.warhead.craftingdeadtrial.command.CDTCommand;
import io.github.warhead.craftingdeadtrial.event.EnergyListener;
import io.github.warhead.craftingdeadtrial.event.ThirstListener;
import io.github.warhead.craftingdeadtrial.items.ModItems;
import io.github.warhead.craftingdeadtrial.lib.Reference;
import io.github.warhead.craftingdeadtrial.network.ConnectionHandler;
import io.github.warhead.craftingdeadtrial.network.PacketHandler;
import io.github.warhead.craftingdeadtrial.proxy.IProxy;
import io.github.warhead.craftingdeadtrial.tick.PlayerTickHandler;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = CraftingDeadTrial.MODID, version = CraftingDeadTrial.VERSION)
@NetworkMod(clientSideRequired = true, serverSideRequired = false, channels = {Reference.CHANNEL_NAME}, packetHandler = PacketHandler.class)
public class CraftingDeadTrial {
    public static final String MODID = "CraftingDeadTrial";
    public static final String VERSION = "1.0";

    @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
    public static IProxy proxy;

    public static CreativeTabs craftingDeadTrialTabs = new CDTCreativeTab(CreativeTabs.getNextID(), MODID);

    @Mod.Instance(MODID)
    public static CraftingDeadTrial instance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        //Instantiate Blocks
        ModBlocks.init();
        //Instantiate items
        ModItems.init();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        //Register our connection handler so that we can send the proper information about their current state to the client
        NetworkRegistry.instance().registerConnectionHandler(new ConnectionHandler());

        //Register the listener that provides events for energy
        MinecraftForge.EVENT_BUS.register(new EnergyListener());

        //Register the listener that provides events for thirst.
        MinecraftForge.EVENT_BUS.register(new ThirstListener());

        //Register the PlayerTickHandler for our stats
        TickRegistry.registerTickHandler(new PlayerTickHandler(), Side.SERVER);

        //Call the init phase of our proxy.
        proxy.init();

        proxy.initItemRecipes();

        proxy.initBlockRecipes();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        //NO:OP
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        //Register our command
        event.registerServerCommand(new CDTCommand());
    }
}
