package io.github.warhead.craftingdeadtrial;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.github.warhead.craftingdeadtrial.lib.ItemIds;
import net.minecraft.creativetab.CreativeTabs;

public class CDTCreativeTab extends CreativeTabs {

    CDTCreativeTab(int par1, String par2Str) {
        super(par1, par2Str);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getTabIconItemIndex() {
        return ItemIds.WATER_BOTTLE + 256;
    }
}
