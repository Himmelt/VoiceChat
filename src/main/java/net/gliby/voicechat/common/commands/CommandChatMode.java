package net.gliby.voicechat.common.commands;

import net.gliby.voicechat.VoiceChat;
import net.gliby.voicechat.common.networking.ServerStream;
import net.gliby.voicechat.common.networking.ServerStreamManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

import java.util.List;

public class CommandChatMode extends CommandBase {

    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return args.length == 1 ?
                getListOfStringsMatchingLastWord(args, "distance", "global", "world")
                : (args.length == 2 ? getListOfStringsMatchingLastWord(args, this.getListOfPlayerUsernames()) :
                null);
    }

    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args) {
        return addTabCompletionOptions(sender, args);
    }

    public String getChatMode(int chatMode) {
        return chatMode == 0 ? "distance" : (chatMode == 2 ? "global" : (chatMode == 1 ? "world" : "distance"));
    }

    protected int getChatModeFromCommand(ICommandSender par1ICommandSender, String par2Str) {
        return !par2Str.equalsIgnoreCase("distance") && !par2Str.startsWith("d") && !par2Str.equalsIgnoreCase("0") ? (!par2Str.equalsIgnoreCase("world") && !par2Str.startsWith("w") && !par2Str.equalsIgnoreCase("1") ? (!par2Str.equalsIgnoreCase("global") && !par2Str.startsWith("g") && !par2Str.equalsIgnoreCase("2") ? 0 : 2) : 1) : 0;
    }

    public String getName() {
        return "vchatmode";
    }

    public String getUsage(ICommandSender sender) {
        return "/vchatmode <mode> or /vchatmode <mode> [player]";
    }

    protected String[] getListOfPlayerUsernames() {
        return MinecraftServer.getServer().getAllUsernames();
    }

    public int getRequiredPermissionLevel() {
        return 3;
    }

    public String getCommandName() {
        return getName();
    }

    public String getCommandUsage(ICommandSender sender) {
        return getUsage(sender);
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        processCommand(sender, args);
    }

    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length > 0) {
            int chatMode = this.getChatModeFromCommand(sender, args[0]);
            EntityPlayerMP player = null;

            try {
                player = args.length >= 2 ? getPlayer(sender, args[1]) : getCommandSenderAsPlayer(sender);
            } catch (PlayerNotFoundException var7) {
                var7.printStackTrace();
            }

            if (player != null) {
                ServerStreamManager dataManager = VoiceChat.getServerInstance().getServerNetwork().getDataManager();
                dataManager.chatModeMap.put(player.getPersistentID(), chatMode);
                ServerStream stream = dataManager.getStream(player.getEntityId());
                if (stream != null) {
                    stream.dirty = true;
                }

                if (player != sender) {
                    func_152373_a(sender, this, player.getCommandSenderName() + " set chat mode to " + this.getChatMode(chatMode).toUpperCase() + " (" + chatMode + ")", args[0]);
                } else {
                    player.addChatMessage(new ChatComponentText("Set own chat mode to " + this.getChatMode(chatMode).toUpperCase() + " (" + chatMode + ")"));
                    switch (chatMode) {
                        case 0:
                            player.addChatMessage(new ChatComponentText("Only players near you can hear you."));
                            break;
                        case 1:
                            player.addChatMessage(new ChatComponentText("Every player in this world can hear you"));
                            break;
                        case 2:
                            player.addChatMessage(new ChatComponentText("Every player can hear you."));
                    }
                }
            }
        }
    }

    public boolean isUsernameIndex(String[] args, int index) {
        return index == 1;
    }
}
