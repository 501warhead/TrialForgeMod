package io.github.warhead.craftingdeadtrial.lib;

import io.github.warhead.craftingdeadtrial.CraftingDeadTrial;

/**
 * A basic class for me to store strings I might need.
 * <p>
 * Final to protect from overwrite and protected constructor to prevent instantiation
 *
 * @author 501warhead
 */
public final class Strings {

    public static final String RESOURCE_PREFIX = CraftingDeadTrial.MODID.toLowerCase() + ":";
    public static final String WATER_BOTTLE_NAME = "waterBottle";
    public static final String WOOD_BOTTLE_NAME = "woodBottle";
    public static final String PUNCHING_BAG_BLOCK_NAME = "blockPunchingBag";

    protected Strings() {
    }
}
