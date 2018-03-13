package io.github.warhead.craftingdeadtrial.proxy;

import cpw.mods.fml.common.registry.GameRegistry;
import io.github.warhead.craftingdeadtrial.blocks.ModBlocks;
import io.github.warhead.craftingdeadtrial.items.ModItems;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

/**
 * A common proxy, or the place for methods that should be performed on server and client.
 *
 * @author 501warhead
 */
public abstract class CommonProxy implements IProxy {

    @Override
    public void initBlockRecipes() {
        GameRegistry.addRecipe(new ItemStack(ModBlocks.blockPunchingBag, 1, 0), "www", "wiw", "www", 'w', Block.cloth, 'i', Item.ingotIron);
    }

    @Override
    public void initItemRecipes() {
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.waterBottle, 1, 0), new ItemStack(Item.potion, 1, 0));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.waterBottle, 1, 0), "isi", "i i", " i ", 'i', Block.glass, 's', Block.blockLapis));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.woodBottle, 1, 0), " b ", " w ", " z ", 'b', Block.woodenButton, 'w', Block.wood, 'z', Item.bowlEmpty));
    }
}
