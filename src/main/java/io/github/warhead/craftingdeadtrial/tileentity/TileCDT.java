package io.github.warhead.craftingdeadtrial.tileentity;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

public class TileCDT extends TileEntity {

    protected ForgeDirection direction;
    protected byte state;
    protected String customName;

    public TileCDT() {
        this.direction = ForgeDirection.SOUTH;
        this.state = 0;
        this.customName = "";
    }

    public ForgeDirection getDirection() {
        return direction;
    }

    public void setDirection(ForgeDirection direction) {
        this.direction = direction;
    }

    public byte getState() {
        return state;
    }

    public void setState(byte state) {
        this.state = state;
    }

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }


    @Override
    public String toString() {
        return "TileCDT{" +
                "direction=" + direction +
                ", state=" + state +
                ", customName='" + customName + '\'' +
                ", xCoord=" + xCoord +
                ", yCoord=" + yCoord +
                ", zCoord=" + zCoord +
                ", tileEntityInvalid=" + tileEntityInvalid +
                ", blockType=" + blockType.blockID +
                '}';
    }
}
