package io.github.warhead.craftingdeadtrial.thirst;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.github.warhead.craftingdeadtrial.CraftingDeadTrial;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.ForgeSubscribe;
import org.lwjgl.opengl.GL11;

import java.util.EnumSet;
import java.util.Random;

/**
 * The Client-Sided Overlay handler to show the player how thirsty they are.
 * Alternatively you can just pose as a girl. That will do the trick just as well.
 *
 * @author 501warhead
 * @see ThirstHandler
 */
@SideOnly(Side.CLIENT)
public class ThirstOverlay implements ITickHandler {

    private static final ResourceLocation THIRST_OVERLAY = new ResourceLocation(CraftingDeadTrial.MODID.toLowerCase() + ":textures/gui/overlay.png");
    private static final ResourceLocation VIGNETTE = new ResourceLocation(CraftingDeadTrial.MODID.toLowerCase() + ":textures/gui/vignette.png");

    private final Minecraft mc = Minecraft.getMinecraft();
    private final Random rnd = new Random();

    private float prevVignetteBrightness;

    private int updateCounter;

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData) {
        // NO:OP
    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData) {
        ++updateCounter;
    }

    @Override
    public EnumSet<TickType> ticks() {
        EnumSet<TickType> ticks = EnumSet.noneOf(TickType.class);
        ticks.add(TickType.CLIENT);
        return ticks;
    }

    @Override
    public String getLabel() {
        return "Thirst";
    }

    @ForgeSubscribe
    public void onPreRenderOverlay(RenderGameOverlayEvent.Pre event) {
        if (event.type == RenderGameOverlayEvent.ElementType.AIR) {
            //If the player currently is underwater and breathing we need to account for their air bar.
            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, -10.0F, 0.0F);
        }
    }

    @ForgeSubscribe
    public void onPostRenderOverlay(RenderGameOverlayEvent.Post event) {
        //Get the current scaled resolution of the players client. Important to account for GUI scale size.
        ScaledResolution res = event.resolution;
        //Get width and height of the resoltuion
        int width = res.getScaledWidth();
        int height = res.getScaledHeight();
        ThirstHandler handler = ThirstHandler.getOrMakeFromNBT(Minecraft.getMinecraft().thePlayer.getEntityData());
        float thirst = handler.getThirst();
        float thirstSaturation = handler.getThirstSaturation();

        if (event.type == RenderGameOverlayEvent.ElementType.ALL) {

        }

        //If the player is currently underwater, or breathing for some reason.
        if (event.type == RenderGameOverlayEvent.ElementType.AIR) {
            GL11.glPopMatrix();
        }
        //If minecraft is rendering the experience bar in this Post then it's time to render our thirst overlay.
        //Thirst operates as 10 waterbottles, like eating food. There can be half waterbottles.
        if (event.type == RenderGameOverlayEvent.ElementType.EXPERIENCE) {
            //Prepare our overlay to be rendered
            mc.getTextureManager().bindTexture(THIRST_OVERLAY);
            //Draw our thirst meter using the overlay
            if (Minecraft.getMinecraft().playerController.isNotCreative()) {
                drawThirstBar(width, height, thirst, thirstSaturation);
                //Bump the GUI a bit so that we dont have overlap
                GuiIngameForge.right_height += 10;
                //If thirst is below 5 render an orange tint on the sides of the screen, growing more defined the thirstier you are.
                if (thirst < 5F) {
                    float brightness = thirst == 0 ? 1 : 1 / thirst;
                    //renderVignette(brightness, width, height);
                }
            }
            //Rebind the icons overlay for MC so that elements are still properly rendered from vanilla
            mc.getTextureManager().bindTexture(Gui.icons);
        }
    }

    /**
     * Draws the thirst bar for the player.
     *
     * @param width            The width of the bar
     * @param height           The height of the bar
     * @param thirst           How thirsty the player is
     * @param thirstSaturation How much saturation they have in their thirst.
     */
    private void drawThirstBar(int width, int height, float thirst, float thirstSaturation) {
        int left = width / 2 + 91;
        int top = height - 49;
        //System.out.println(String.format("W:%d, H:%d, T:%d, L:%d", width, height, top, left));

        //Loop through all 10 possible spots.
        for (int i = 0; i < 10; i++) {
            int bottleHalf = i * 2 + 1;
            int index = 0;
            int bgOffset = 0;
            int startX = left - i * 8 - 9;
            int startY = top;
            //System.out.println(String.format("sx:%d, sy:%d, bottlehalf:%d", startX, startY, bottleHalf));

            //Shake if low
            if (thirstSaturation <= 0F && updateCounter % (thirst * 3 + 1) == 0) {
                startY = top + (rnd.nextInt(3) - 1);
            }
            //Draw empty bottle
            //Empty bottle: 0, 0 - 16, 16
            mc.ingameGUI.drawTexturedModalRect(startX, startY, bgOffset, 0, 9, 9);
            //If the Thirst level is over half of the bottle, aka full.
            if (thirst > bottleHalf) {
                //Full Bottle: 16, 0 - 32, 16
                mc.ingameGUI.drawTexturedModalRect(startX, startY, 9, 0, 9, 9);
            }
            //If the thirst level is equal to half the bottle, aka half.
            if (thirst == bottleHalf) {
                //Half Bottle: 32, 0 - 48, 16
                mc.ingameGUI.drawTexturedModalRect(startX, startY, 18, 0, 9, 9);
            }
        }
    }

    private void renderVignette(float brightness, int width, int height) {
        brightness = 1.0F - brightness;
        if (brightness < 0.0F) {
            brightness = 0.0F;
        }
        if (brightness > 1.0F) {
            brightness = 1.0F;
        }

        this.prevVignetteBrightness = (float) ((double) this.prevVignetteBrightness + (double) (brightness - this.prevVignetteBrightness) * 0.1F);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glBlendFunc(GL11.GL_ZERO, GL11.GL_ONE_MINUS_SRC_COLOR);
        GL11.glColor4f(this.prevVignetteBrightness, this.prevVignetteBrightness, this.prevVignetteBrightness, 1.0F);
        mc.getTextureManager().bindTexture(VIGNETTE);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(0.0D, (double) height, -90.0D, 0.0D, 1.0D);
        tessellator.addVertexWithUV((double) width, (double) height, -90.0D, 1.0D, 1.0D);
        tessellator.addVertexWithUV((double) width, 0.0D, -90.0D, 1.0D, 0.0D);
        tessellator.addVertexWithUV(0.0D, 0.0D, -90.0D, 0.0D, 0.0D);
        tessellator.draw();
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        //Render the orange vignette!
        GL11.glColor4f(1.0F, 0.75F, 0F, 1.0F);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }
}
