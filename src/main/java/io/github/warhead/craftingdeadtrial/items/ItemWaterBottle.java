package io.github.warhead.craftingdeadtrial.items;

import io.github.warhead.craftingdeadtrial.CraftingDeadTrial;
import io.github.warhead.craftingdeadtrial.io.IStat;
import io.github.warhead.craftingdeadtrial.io.ServerDataHandler;
import io.github.warhead.craftingdeadtrial.lib.Strings;
import io.github.warhead.craftingdeadtrial.thirst.ThirstHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

/**
 * A bigger waterbottle than vanilla provides. Think plastic waterbottle.
 *
 * @author 501warhead
 */
public class ItemWaterBottle extends ItemCD {

    public ItemWaterBottle(int id) {
        super(id);
        this.setUnlocalizedName(Strings.RESOURCE_PREFIX + Strings.WATER_BOTTLE_NAME);
        this.setMaxDamage(4);
        this.setCreativeTab(CraftingDeadTrial.craftingDeadTrialTabs);
    }

    @Override
    public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5) {
        super.onUpdate(par1ItemStack, par2World, par3Entity, par4, par5);
    }

    @Override
    public ItemStack onEaten(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
        if (par1ItemStack.getItemDamage() == 4) {
            //If there are no uses left in this bottle then simply return the bottle.
            return par1ItemStack;
        }
        ThirstHandler handler = null;
        if (par2World.isRemote) { //If this is happening client-side
            //Construct thirst handler from nbt
            handler = ThirstHandler.getOrMakeFromNBT(par3EntityPlayer.getEntityData());
        } else { //If this is happening server side
            //Retrieve thirst handler from data
            handler = (ThirstHandler) ServerDataHandler.getInstance().getData(par3EntityPlayer, IStat.Type.THIRST);
        }

        if (handler == null || !handler.isThirsty()) {
            return par1ItemStack;
        }

        if (!par3EntityPlayer.capabilities.isCreativeMode) { //Creative mode override to prevent it from going down
            //Subtract a use
            par1ItemStack.setItemDamage(par1ItemStack.getItemDamage() + 1);
        }

        if (!par2World.isRemote) { //If this is server side
            //Add 10 to thirst (poorly named method... oh well.) and 5 to saturation
            handler.addThirst(10, 5);
        }

        return par1ItemStack;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack par1ItemStack) {
        return 32;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack) {
        return EnumAction.drink;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
        //Fills a water bottle if they're using it on water.

        ThirstHandler handler = null;
        if (par2World.isRemote) { //If this is happening client-side
            //Construct thirst handler from nbt
            handler = ThirstHandler.getOrMakeFromNBT(par3EntityPlayer.getEntityData());
        } else { //If this is happening server side
            //Retrieve thirst handler from data
            handler = (ThirstHandler) ServerDataHandler.getInstance().getData(par3EntityPlayer, IStat.Type.THIRST);
        }
        MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(par2World, par3EntityPlayer, par1ItemStack.getItemDamage() == 4);
        if (mop == null) {
            if (par1ItemStack.getItemDamage() < 4 && handler != null && handler.isThirsty()) {
                par3EntityPlayer.setItemInUse(par1ItemStack, this.getMaxItemUseDuration(par1ItemStack));
            }
            return par1ItemStack;
        } else {
            int blockId = par2World.getBlockId(mop.blockX, mop.blockY, mop.blockZ);
            if (blockId == Block.waterMoving.blockID || blockId == Block.waterStill.blockID) {
                par1ItemStack.setItemDamage(0);
            } else if (par1ItemStack.getItemDamage() < 4 && handler != null && handler.isThirsty()) {
                par3EntityPlayer.setItemInUse(par1ItemStack, this.getMaxItemUseDuration(par1ItemStack));
            }
        }
        return par1ItemStack;
    }
}
