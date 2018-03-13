package io.github.warhead.craftingdeadtrial.event;

import io.github.warhead.craftingdeadtrial.io.IStat;
import io.github.warhead.craftingdeadtrial.io.ServerDataHandler;
import io.github.warhead.craftingdeadtrial.thirst.ThirstHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSaddle;
import net.minecraftforge.event.Event;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class ThirstListener {

    @ForgeSubscribe
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK
                && e.useBlock == Event.Result.ALLOW
                && e.entityPlayer.worldObj.getBlockId(e.x, e.y, e.z) == Block.cauldron.blockID) {
            int dmg = Block.cauldron.getDamageValue(e.entityPlayer.worldObj, e.x, e.y, e.z);
            if (dmg > 0) {
                //TODO increment thirst
                e.entityPlayer.worldObj.setBlockMetadataWithNotify(e.x, e.y, e.z, dmg - 1, 2);

            }
        } else if (e.useItem == Event.Result.ALLOW
                && e.entityPlayer.getHeldItem().itemID == ItemSaddle.potion.itemID
                && (e.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK
                || e.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR)) {

        }

    }

    @ForgeSubscribe
    public void onPlayerDeath(LivingDeathEvent e) {
        if (e.entity instanceof EntityPlayer && e.entity.worldObj.isRemote) {
            EntityPlayer pl = (EntityPlayer) e.entity;
            ThirstHandler handler = (ThirstHandler) ServerDataHandler.getInstance().getData(pl, IStat.Type.THIRST);
            if (handler != null) {
                //TODO set thirst back to full
            }
        }
    }
}
