/*
 * Decompiled with CFR 0_118.
 *
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.management.ServerConfigurationManager
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 */
package net.gliby.voicechat.common.networking;

import net.gliby.voicechat.VoiceChat;
import net.gliby.voicechat.common.VoiceChatServer;
import net.gliby.voicechat.common.networking.packets.MinecraftClientEntityDataPacket;
import net.minecraft.entity.player.EntityPlayerMP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class ServerNetwork {
    public final ServerStreamManager dataManager;
    private final VoiceChatServer voiceChat;
    private String externalAddress;

    public ServerNetwork(VoiceChatServer voiceChat) {
        this.voiceChat = voiceChat;
        this.dataManager = new ServerStreamManager(voiceChat);
    }

    public String getAddress() {
        return this.externalAddress;
    }

    public ServerStreamManager getDataManager() {
        return this.dataManager;
    }

    public String[] getPlayerIPs() {
        ArrayList<EntityPlayerMP> players = VoiceChat.getPlayers(voiceChat.getMinecraftServer());
        String[] ips = new String[players.size()];
        for (int i = 0; i < players.size(); ++i) {
            EntityPlayerMP p = players.get(i);
            ips[i] = p.getPlayerIP();
        }
        return ips;
    }

//    public EntityPlayerMP[] getPlayers() {
//        List<EntityPlayerMP> pl = voiceChat.getMinecraftServer().getPlayerList().getPlayers();
//        return pl.toArray(new EntityPlayerMP[pl.size()]);
//    }

    public void init() {
        if (this.voiceChat.getServerSettings().isUsingProxy()) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    ServerNetwork.this.externalAddress = ServerNetwork.this.retrieveExternalAddress();
                }
            }, "Extrernal Address Retriver Process").start();
        }
        this.dataManager.init();
    }

    private String retrieveExternalAddress() {
        VoiceChat.getLogger().info("Retrieving server address.");
        BufferedReader in = null;
        try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
            return in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return "0.0.0.0";
        }
    }

    public void sendEntityData(EntityPlayerMP player, int entityID, String username, double x, double y, double z) {
        VoiceChat.getDispatcher().sendTo(new MinecraftClientEntityDataPacket(entityID, username, x, y, z), player);
    }

    public void stop() {
        this.dataManager.reset();
    }

}
