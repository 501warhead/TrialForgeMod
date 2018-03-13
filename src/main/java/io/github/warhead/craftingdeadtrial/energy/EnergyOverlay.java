package io.github.warhead.craftingdeadtrial.energy;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.github.warhead.craftingdeadtrial.CraftingDeadTrial;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.ForgeSubscribe;

import java.util.EnumSet;
import java.util.Random;

/**
 * The overlay handler for energy.
 *
 * @author 501warhead
 * @see EnergyHandler
 */
@SideOnly(Side.CLIENT)
public final class EnergyOverlay implements ITickHandler {

    //Our overlay textures
    private static ResourceLocation OVERLAY = new ResourceLocation(CraftingDeadTrial.MODID.toLowerCase() + ":textures/gui/overlay.png");

    //An accessor value for minecraft for ease.
    private final Minecraft mc = Minecraft.getMinecraft();
    //A random for moving visual elements
    private final Random rnd = new Random();

    //An update counter to measure intervals so moving pieces don't start spazzing out.
    private int updateCounter = 0;

    /**
     * A simple default constructor.
     */
    public EnergyOverlay() {
    }

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData) {
        //NO:OP
    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData) {
        //Update our counter for moving elements.
        ++updateCounter;
    }

    @Override
    public EnumSet<TickType> ticks() {
        //Set this listener to only listen to client ticks. (Render ticks are too fast for moving elements.)
        EnumSet<TickType> set = EnumSet.noneOf(TickType.class);
        set.add(TickType.CLIENT);
        return set;
    }

    @Override
    public String getLabel() {
        //A simple label for JVM debuggers
        return "EnergyOverlay";
    }

    @ForgeSubscribe
    public void onPreRender(RenderGameOverlayEvent.Pre e) {
        //NO:OP
    }

    @ForgeSubscribe
    public void onPostRender(RenderGameOverlayEvent.Post e) {
        //Don't render energy assets if the player is in creative or if this is the incorrect phase of drawing
        if (mc.playerController.isNotCreative()
                && e.type == RenderGameOverlayEvent.ElementType.EXPERIENCE) {
            //Get our resolution for element scaling
            ScaledResolution res = e.resolution;
            //Get appropriate width and heights for element drawing
            int width = res.getScaledWidth();
            int height = res.getScaledHeight();
            //Get our energyhandler for value reading, as we're client side we must establish it from the players NBT data.
            EnergyHandler handler = EnergyHandler.getOrMakeFromNBT(Minecraft.getMinecraft().thePlayer.getEntityData());
            //Get our current energy values
            int currentEnergy = handler.getCurrentEnergy();
            int maxEnergy = handler.getMaxEnergy();

            //Bind our overlay texture
            mc.getTextureManager().bindTexture(OVERLAY);
            //Call our draw method for the circle object
            drawEnergyCircle(width, height, currentEnergy, maxEnergy);
            //Rebind the GUI icons for default minecraft. might not be needed but I'd rather not have missing elements to debug
            mc.getTextureManager().bindTexture(Gui.icons);
        }
    }

    private void drawEnergyCircle(int width, int height, int currentEnergy, int maxEnergy) {
        //Beware. Math is not my forte so this is just a bunch of short circuit hacks so I don't spend like 8 hours working out this math
        //Cut the width in half and place the start point so that a 16x16 is exactly centered
        int left = width / 2 - 8;
        //Place the element neatly above the experience bar, below the hover text of items, and in between health and thirst/hunger
        int top = height - 53;
        //Set up our offset for the spritesheet reading
        int bgOffset = 16;
        //Get the current % of energy that we are currently at (0.0 - 1.0)
        float energyPerc = currentEnergy == 0 ? 0 : (float) currentEnergy / (float) maxEnergy;
        //Convert the energy percent to the appropriate amount of pixels
        int energySize = Math.max((int) (16 - (energyPerc * (float) (17))), 0);
        if (energyPerc < 0.2) { //If their energy is low
            if (updateCounter % (currentEnergy * 3 + 1) == 0) { //If enough time has passed since the last shake
                //Shake the energy bar
                left += rnd.nextInt(3) - 1;
            }
        }

        //Draw our "empty" energy bar
        mc.ingameGUI.drawTexturedModalRect(left, top, 0, 9, 16, 16 + 9);
        if (currentEnergy > 0) { //If the player has energy
            if (energyPerc < 0.5) { //If the player is below half energy
                //Switch the sprite to the yellow colored texture
                bgOffset += 16;
            }
            if (energyPerc < 0.2) { //If the player is low on energy
                //Switch the sprite to the red colored texture
                bgOffset += 16;

            }
            //Draw our remaining energy, offsetting the remaining energy by enough so that it lines up with our "empty" sprite.
            mc.ingameGUI.drawTexturedModalRect(left, top + energySize, bgOffset, 9 + energySize, 16, 16 + 9);
        }
    }
}
