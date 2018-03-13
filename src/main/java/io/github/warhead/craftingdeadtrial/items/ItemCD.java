package io.github.warhead.craftingdeadtrial.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * The base class for all CraftingDeadTrial items (that aren't potions)
 * <p>
 * All items should have a unique ID, preferably declared in {@link io.github.warhead.craftingdeadtrial.lib.ItemIds}
 *
 * @author 501warhead
 */
public class ItemCD extends Item {

    ItemCD(int id) {
        super(id);
        this.maxStackSize = 1;
        this.setNoRepair();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister register) {
        this.itemIcon = register.registerIcon(this.getUnlocalizedName().substring(this.getUnlocalizedName().indexOf(".") + 1));
    }

    /**
     * Get the subtypes of this itemstack, if it has any.
     *
     * @return The subtypes of this item
     */
    public List<ItemStack> getSubTypes() {
        return null;
    }
}
