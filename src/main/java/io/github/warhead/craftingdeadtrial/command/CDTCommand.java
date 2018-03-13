package io.github.warhead.craftingdeadtrial.command;

import com.google.common.collect.Lists;
import io.github.warhead.craftingdeadtrial.energy.EnergyHandler;
import io.github.warhead.craftingdeadtrial.io.IStat;
import io.github.warhead.craftingdeadtrial.io.ServerDataHandler;
import io.github.warhead.craftingdeadtrial.thirst.ThirstHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatMessageComponent;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;

/**
 * The central commands for the CraftingDeadTrial
 *
 * @author 501warhead
 */
public class CDTCommand extends CommandBase {

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public List getCommandAliases() {
        return Lists.newArrayList("cdt");
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender) {
        return super.canCommandSenderUseCommand(par1ICommandSender);
    }

    @Override
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
        return super.addTabCompletionOptions(par1ICommandSender, par2ArrayOfStr);
    }

    @Override
    public boolean isUsernameIndex(String[] par1ArrayOfStr, int par2) {
        return super.isUsernameIndex(par1ArrayOfStr, par2);
    }

    @Override
    public String getCommandName() {
        return "craftingdeadtrial";
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        return "commands.craftingdeadtrial.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        //Check to see if the player is a player, and not a console or some other weirdness.
        if (!(sender instanceof EntityPlayerMP)) {
            //Send error message
            sender.sendChatToPlayer(ChatMessageComponent.createFromText("You must be logged in to run this command"));
            return;
        }
        //Cast sender to player for commands
        EntityPlayerMP pl = (EntityPlayerMP) sender;
        if (args.length == 0) {
            sendHelp(sender);
            return;
        }
        //Commands
        if (args[0].equalsIgnoreCase("thirst")) { //Check Thirst
            //Get the thirst handler of the player
            ThirstHandler handler = (ThirstHandler) ServerDataHandler.getInstance().getData(pl, IStat.Type.THIRST);
            if (handler == null) {
                //If the thirst handler was null (it shouldn't be) send an error message.
                pl.sendChatToPlayer(ChatMessageComponent.createFromText("ThirstHandler was null! Cannot Parse!"));
                return;
            }
            if (args.length > 1 && NumberUtils.isDigits(args[1])) {
                handler.setThirst(Integer.valueOf(args[1]));
                pl.sendChatToPlayer(ChatMessageComponent.createFromText(String.format("Set thirst to %d", handler.getThirst())));
            }
            //Send the players current thirst stats to their chat.
            pl.sendChatToPlayer(ChatMessageComponent.createFromText(String.format("Current Thirst: %d, Thirst Saturation: %f, Thirst Exhaustion: %f, Thirst Timer: %d",
                    handler.getThirst(),
                    handler.getThirstSaturation(),
                    handler.getThirstExhaustion(),
                    handler.getTimer())));
        } else if (args[0].equalsIgnoreCase("energy")) { //Check Energy
            //Get the energy handler of the player
            EnergyHandler handler = (EnergyHandler) ServerDataHandler.getInstance().getData(pl, IStat.Type.ENERGY);
            if (handler == null) {
                //If the energy handler was null (it shouldn't be) send an error messsage.
                pl.sendChatToPlayer(ChatMessageComponent.createFromText("EnergyHandler was null! Cannot Parse!"));
                return;
            }
            //Send the players current and max energy to their chat.
            pl.sendChatToPlayer(ChatMessageComponent.createFromText(String.format("Current Energy: %d, Max Energy: %d",
                    handler.getCurrentEnergy(),
                    handler.getMaxEnergy())));
        } else {
            sendHelp(sender);
        }
    }

    /**
     * I need it.
     *
     * @param sender The sender to send help to
     */
    private void sendHelp(ICommandSender sender) {
        sender.sendChatToPlayer(ChatMessageComponent.createFromText("~~~ CD Trial Plugin ~~~\n" +
                "/cdt energy: Print out current energy levels\n" +
                "/cdt thirst: Print out current thirst levels\n" +
                "/cdt thirst <#>: Set your current thirst to the provided number, between 0 and 20"));
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
