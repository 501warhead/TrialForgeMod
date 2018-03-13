package io.github.warhead.craftingdeadtrial.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import io.github.warhead.craftingdeadtrial.lib.BlockIds;
import io.github.warhead.craftingdeadtrial.lib.Strings;

/**
 * A holder and initializer for all mod-created blocks.
 *
 * @author 501warhead
 */
public class ModBlocks {

    public static BlockCD blockPunchingBag;

    /**
     * Initialize all of our Blocks.
     */
    public static void init() {
        blockPunchingBag = new BlockPunchingBag(BlockIds.PUNCHING_BAG);

        GameRegistry.registerBlock(blockPunchingBag, "block." + Strings.PUNCHING_BAG_BLOCK_NAME);
    }
}
