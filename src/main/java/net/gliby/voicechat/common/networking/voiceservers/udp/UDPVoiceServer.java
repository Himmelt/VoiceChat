package net.gliby.voicechat.common.networking.voiceservers.udp;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.gliby.voicechat.common.VoiceChatServer;
import net.gliby.voicechat.common.networking.ServerStreamManager;
import net.gliby.voicechat.common.networking.voiceservers.EnumVoiceNetworkType;
import net.gliby.voicechat.common.networking.voiceservers.VoiceAuthenticatedServer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

public class UDPVoiceServer extends VoiceAuthenticatedServer {

    public static volatile boolean running;
    private final VoiceChatServer voiceChat;
    private final ServerStreamManager manager;
    public Map<Integer, UDPClient> clientMap;
    private UDPVoiceServerHandler handler;
    private UdpServer server;


    public UDPVoiceServer(VoiceChatServer voiceChat) {
        this.voiceChat = voiceChat;
        this.manager = voiceChat.getServerNetwork().getDataManager();
    }

    @Override
    public void closeConnection(int id) {
        UDPClient client = this.clientMap.get(id);
        if (client != null) {
            this.handler.closeConnection(client.socketAddress);
        }

        this.clientMap.remove(id);
    }

    @Override
    public EnumVoiceNetworkType getType() {
        return EnumVoiceNetworkType.UDP;
    }

    public MinecraftServer getMinecraftServer() {
        return voiceChat.getMinecraftServer();
    }

    @Override
    public void handleVoiceData(EntityPlayerMP player, byte[] data, byte divider, int id, boolean end) {
        this.manager.addQueue(player, data, divider, id, end);
    }

    @Override
    public void sendChunkVoiceData(EntityPlayerMP player, int entityID, boolean direct, byte[] samples, byte chunkSize) {
        UDPClient client = this.clientMap.get(player.getEntityId());
        if (client != null) {
            this.sendPacket(new UDPServerChunkVoicePacket(samples, entityID, direct, chunkSize), client);
        }

    }

    @Override
    public void sendEntityPosition(EntityPlayerMP player, int entityID, double x, double y, double z) {
        UDPClient client = this.clientMap.get(player.getEntityId());
        if (client != null) {
            this.sendPacket(new UDPServerEntityPositionPacket(entityID, x, y, z), client);
        }

    }

    public void sendPacket(UDPPacket packet, UDPClient client) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeByte(packet.id());
        packet.write(out);
        byte[] data = out.toByteArray();

        try {
            this.server.send(new DatagramPacket(data, data.length, client.socketAddress));
        } catch (SocketException var6) {
            var6.printStackTrace();
        } catch (IOException var7) {
            var7.printStackTrace();
        }

    }

    @Override
    public void sendVoiceData(EntityPlayerMP player, int entityID, boolean global, byte[] samples) {
        UDPClient client = this.clientMap.get(player.getEntityId());
        if (client != null) {
            this.sendPacket(new UDPServerVoicePacket(samples, entityID, global), client);
        }

    }

    @Override
    public void sendVoiceEnd(EntityPlayerMP player, int entityID) {
        UDPClient client = this.clientMap.get(player.getEntityId());
        if (client != null) {
            this.sendPacket(new UDPServerVoiceEndPacket(entityID), client);
        }

    }

    @Override
    public boolean start() {
        this.clientMap = new HashMap<Integer, UDPClient>();
        this.handler = new UDPVoiceServerHandler(this);
        String hostname = "0.0.0.0";
        MinecraftServer mc = getMinecraftServer();
        if (mc.isDedicatedServer()) {
            hostname = mc.getServerHostname();
        }

        this.server = new UdpServer(VoiceChatServer.getLogger(), hostname, this.voiceChat.getServerSettings().getUDPPort());
        this.server.addUdpServerListener(new UdpServer.Listener() {
            @Override
            public void packetReceived(UdpServer.Event evt) {
                try {
                    UDPVoiceServer.this.handler.read(evt.getPacketAsBytes(), evt.getPacket());
                } catch (Exception var3) {
                    var3.printStackTrace();
                }

            }
        });
        this.server.start();
        return true;
    }

    @Override
    public void stop() {
        running = false;
        this.handler.close();
        this.server.clearUdpListeners();
        this.server.stop();
        this.clientMap.clear();
        this.handler = null;
        this.server = null;
    }
}
