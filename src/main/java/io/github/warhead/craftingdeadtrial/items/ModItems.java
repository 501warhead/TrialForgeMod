package io.github.warhead.craftingdeadtrial.items;

import cpw.mods.fml.common.registry.GameRegistry;
import io.github.warhead.craftingdeadtrial.lib.ItemIds;
import io.github.warhead.craftingdeadtrial.lib.Strings;

/**
 * A general file to handle and hold mod-created items.
 *
 * @author 501warhead
 */
public final class ModItems {


    public static ItemCD waterBottle;
    public static ItemCD woodBottle;

    /**
     * Initialize all of our items during Pre-Init.
     */
    public static void init() {
        waterBottle = new ItemWaterBottle(ItemIds.WATER_BOTTLE);
        woodBottle = new ItemWoodBottle(ItemIds.WOOD_BOTTLE);

        GameRegistry.registerItem(waterBottle, "item." + Strings.WATER_BOTTLE_NAME);
        GameRegistry.registerItem(woodBottle, "item." + Strings.WOOD_BOTTLE_NAME);
    }
}
