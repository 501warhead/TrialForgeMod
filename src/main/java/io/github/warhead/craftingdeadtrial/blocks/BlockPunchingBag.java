package io.github.warhead.craftingdeadtrial.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.github.warhead.craftingdeadtrial.energy.EnergyActionType;
import io.github.warhead.craftingdeadtrial.energy.EnergyHandler;
import io.github.warhead.craftingdeadtrial.io.IStat;
import io.github.warhead.craftingdeadtrial.io.ServerDataHandler;
import io.github.warhead.craftingdeadtrial.lib.Strings;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockPunchingBag extends BlockCD {

    @SideOnly(Side.CLIENT)
    private Icon blockTop, blockSide;

    public BlockPunchingBag(int id) {
        super(id, Material.cloth);
        this.setUnlocalizedName(Strings.RESOURCE_PREFIX + Strings.PUNCHING_BAG_BLOCK_NAME);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister iconRegister) {
        this.blockTop = iconRegister.registerIcon(String.format("%s.punchingBag_top", getUnwrappedUnlocalizedName(this.getUnlocalizedName())));
        this.blockSide = iconRegister.registerIcon(String.format("%s.punchingBag_side", getUnwrappedUnlocalizedName(this.getUnlocalizedName())));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIcon(int side, int metaData) {
        if (ForgeDirection.getOrientation(side) == ForgeDirection.UP || ForgeDirection.getOrientation(side) == ForgeDirection.DOWN) {
            return blockTop;
        } else {
            return blockSide;
        }
    }

    @Override
    public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9) {
        EnergyHandler handler = null;
        if (par1World.isRemote) {
            handler = EnergyHandler.getOrMakeFromNBT(par5EntityPlayer.getEntityData());
        } else {
            handler = (EnergyHandler) ServerDataHandler.getInstance().getData(par5EntityPlayer, IStat.Type.ENERGY);
        }
        if (handler != null && handler.canDoAction(EnergyActionType.PUNCH_BAG)) {
            if (!par1World.isRemote) {
                handler.onAction(EnergyActionType.PUNCH_BAG);
                handler.setPendingMaxIncrease(handler.getPendingMaxIncrease() + 3);
                par5EntityPlayer.sendChatToPlayer(ChatMessageComponent.createFromText("You train against the punching bag, draining your energy but leaving you feeling stronger than before!"));
            }
        } else return handler == null || handler.canDoAction(EnergyActionType.PUNCH_BAG);
        return true;
    }
}
