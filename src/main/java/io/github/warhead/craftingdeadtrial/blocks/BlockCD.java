package io.github.warhead.craftingdeadtrial.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.github.warhead.craftingdeadtrial.CraftingDeadTrial;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;

/**
 * The base class for all CDT blocks, used to keep track of everything and perform some basic setup for all blocks to ensure nothing is missed
 *
 * @author 501warhead
 */
public class BlockCD extends Block {

    public BlockCD(int id) {
        this(id, Material.rock);
    }

    public BlockCD(int id, Material material) {
        super(id, material);
        this.setStepSound(soundStoneFootstep);
        this.setCreativeTab(CraftingDeadTrial.craftingDeadTrialTabs);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister iconRegister) {
        this.blockIcon = iconRegister.registerIcon(String.format("%s", getUnwrappedUnlocalizedName(this.getUnlocalizedName())));
    }

    /**
     * A private method to get the pure block name of this block, without excess data
     *
     * @param unlocalizedName The unlocalized name of the block
     * @return The Unwrapped unlocalized name
     */
    protected String getUnwrappedUnlocalizedName(String unlocalizedName) {
        return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
    }
}
