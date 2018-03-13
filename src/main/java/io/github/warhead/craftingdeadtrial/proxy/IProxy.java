package io.github.warhead.craftingdeadtrial.proxy;

/**
 * The foundation for this Mod's Proxy, allowing methods to be confined to the appropriate {@link cpw.mods.fml.relauncher.Side}
 *
 * @author 501warhead
 * @see ServerProxy
 * @see CommonProxy
 * @see ClientProxy
 */
public interface IProxy {

    /**
     * Method called in the {@link cpw.mods.fml.common.event.FMLInitializationEvent} on mod start up
     */
    void init();

    void initBlockRecipes();

    void initItemRecipes();
}
