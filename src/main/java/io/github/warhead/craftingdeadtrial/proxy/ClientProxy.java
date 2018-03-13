package io.github.warhead.craftingdeadtrial.proxy;

import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.github.warhead.craftingdeadtrial.energy.EnergyOverlay;
import io.github.warhead.craftingdeadtrial.thirst.ThirstOverlay;
import net.minecraftforge.common.MinecraftForge;

/**
 * Client-sided proxy for client-only methods
 *
 * @author 501warhead
 */
@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void init() {
        //Prepare our thirst overlay
        ThirstOverlay overlay = new ThirstOverlay();
        //Register it as a tickhandler so that it can update
        TickRegistry.registerTickHandler(overlay, Side.CLIENT);
        //Register it as an event listener so it can receive the renderupdate events.
        MinecraftForge.EVENT_BUS.register(overlay);

        EnergyOverlay energyOverlay = new EnergyOverlay();

        TickRegistry.registerTickHandler(energyOverlay, Side.CLIENT);

        MinecraftForge.EVENT_BUS.register(energyOverlay);
    }
}
