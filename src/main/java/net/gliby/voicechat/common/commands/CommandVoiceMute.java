package net.gliby.voicechat.common.commands;

import net.gliby.voicechat.VoiceChat;
import net.gliby.voicechat.common.networking.ServerNetwork;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

import java.util.List;

public class CommandVoiceMute extends CommandBase {

    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, this.getPlayers()) : null;
    }

    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args) {
        return addTabCompletionOptions(sender, args);
    }

    public String getName() {
        return "vmute";
    }

    public String getUsage(ICommandSender par1ICommandSender) {
        return "Usage: /vmute <player>";
    }

    protected String[] getPlayers() {
        return MinecraftServer.getServer().getAllUsernames();
    }

    public int getRequiredPermissionLevel() {
        return 3;
    }

    public boolean isUsernameIndex(String[] args, int index) {
        return isUsernameIndex(index);
    }

    public boolean isUsernameIndex(int index) {
        return index == 0;
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        processCommand(sender, args);
    }

    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 1 && args[0].length() > 0) {
            ServerNetwork network = VoiceChat.getServerInstance().getServerNetwork();
            EntityPlayerMP player = getPlayer(sender, args[0]);
            if (player != null) {
                if (network.getDataManager().mutedPlayers.contains(player.getUniqueID())) {
                    network.getDataManager().mutedPlayers.remove(player.getUniqueID());
                    func_152373_a(sender, this, player.getCommandSenderName() + " has been unmuted.", args[0]);
                    player.addChatMessage(new ChatComponentText("You have been unmuted!"));
                } else {
                    func_152373_a(sender, this, player.getCommandSenderName() + " has been muted.", args[0]);
                    network.getDataManager().mutedPlayers.add(player.getUniqueID());
                    player.addChatMessage(new ChatComponentText("You have been voice muted, you cannot talk untill you have been unmuted."));
                }
            } else {
                sender.addChatMessage(new ChatComponentText("Player not found for vmute."));
            }
        } else {
            throw new WrongUsageException(this.getUsage(sender));
        }
    }

    public String getCommandName() {
        return getName();
    }

    public String getCommandUsage(ICommandSender sender) {
        return getUsage(sender);
    }
}
