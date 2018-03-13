package io.github.warhead.craftingdeadtrial.event;

import io.github.warhead.craftingdeadtrial.energy.EnergyActionType;
import io.github.warhead.craftingdeadtrial.energy.EnergyHandler;
import io.github.warhead.craftingdeadtrial.io.IStat;
import io.github.warhead.craftingdeadtrial.io.ServerDataHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EnumStatus;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;

import java.util.Arrays;
import java.util.List;

public class EnergyListener {

    /**
     * A list of blocks that when broken or attempted to be broken will require or consume energy.
     * <p>
     * Think stardew valley/harvest moon style.
     */
    public static final List<Integer> significantBlocks = Arrays.asList(
            Block.oreCoal.blockID,
            Block.oreDiamond.blockID,
            Block.oreEmerald.blockID,
            Block.oreGold.blockID,
            Block.oreIron.blockID,
            Block.oreLapis.blockID,
            Block.obsidian.blockID,
            Block.mobSpawner.blockID
    );

    @ForgeSubscribe
    public void onBlockBreak(PlayerEvent.HarvestCheck e) {
        if (e.entityPlayer.worldObj.isRemote) return;
        EnergyHandler handler = (EnergyHandler) ServerDataHandler.getInstance().getData(e.entityPlayer, IStat.Type.ENERGY);
        if (significantBlocks.contains(e.block.blockID)) {
            if (!handler.onAction(EnergyActionType.MINE_ORE) && e.isCancelable()) {
                e.setCanceled(true);
            }
        } else if (e.block.blockID == Block.wood.blockID) {
            if (!handler.onAction(EnergyActionType.CHOP_WOOD) && e.isCancelable()) {
                e.setCanceled(true);
            }
        }
    }

    @ForgeSubscribe
    public void onBreak(PlayerEvent.BreakSpeed e) {
        if (e.entityPlayer.worldObj.isRemote) return;
        EnergyHandler handler = (EnergyHandler) ServerDataHandler.getInstance().getData(e.entityPlayer, IStat.Type.ENERGY);
        if (significantBlocks.contains(e.block.blockID) && !e.entityPlayer.worldObj.isRemote) {
            if (!handler.canDoAction(EnergyActionType.MINE_ORE)) {
                //New speed is 20% of the original. Super slow. Go sleep.
                //TODO Shake the bar a bit to remind them of low energy.
                e.newSpeed = e.originalSpeed * 0.2F;
            }
        } else if (e.block.blockID == Block.wood.blockID && !e.entityPlayer.worldObj.isRemote) {
            if (!handler.canDoAction(EnergyActionType.CHOP_WOOD)) {
                e.newSpeed = e.originalSpeed * 0.2F;
            }
        }
    }

    @ForgeSubscribe
    public void onSleep(PlayerSleepInBedEvent e) {
        if (e.entityPlayer.worldObj.isRemote) return;
        EnergyHandler handler = (EnergyHandler) ServerDataHandler.getInstance().getData(e.entityPlayer, IStat.Type.ENERGY);
        if ((e.result == EnumStatus.OK || e.result == null) && !e.isCanceled() && handler != null) {
            handler.resetEnergy();
        }
    }
}
