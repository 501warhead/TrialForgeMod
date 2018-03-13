package io.github.warhead.craftingdeadtrial.proxy;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Server sided proxy
 *
 * @author 501warhead
 */
@SideOnly(Side.SERVER)
public class ServerProxy extends CommonProxy {

    @Override
    public void init() {
        //NO:OP
    }
}
