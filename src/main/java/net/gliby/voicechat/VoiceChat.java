package net.gliby.voicechat;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.gliby.voicechat.client.VoiceChatClient;
import net.gliby.voicechat.common.VoiceChatServer;
import net.gliby.voicechat.common.networking.packets.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

@Mod(
        modid = VoiceChat.MOD_ID,
        name = "Gliby\'s Voice Chat Mod",
        version = "1.7.10-0.6.2",
        acceptedMinecraftVersions = "[1.7.10]"
)
public class VoiceChat {

    public static final String MOD_ID = "gvc";
    @Mod.Instance
    public static VoiceChat instance;
    @SidedProxy(
            modId = "gvc",
            clientSide = "net.gliby.voicechat.client.VoiceChatClient",
            serverSide = "net.gliby.voicechat.common.VoiceChatServer"
    )
    public static VoiceChatServer proxy;
    public static SimpleNetworkWrapper DISPATCH;

    public static SimpleNetworkWrapper getDispatcher() {
        return DISPATCH;
    }

    public static VoiceChat getInstance() {
        return instance;
    }

    public static Logger getLogger() {
        return VoiceChatServer.getLogger();
    }

    public static VoiceChatClient getProxyInstance() {
        return (VoiceChatClient) proxy;
    }

    public static VoiceChatServer getServerInstance() {
        return proxy;
    }

    public static synchronized VoiceChat getSynchronizedInstance() {
        return instance;
    }

    public static synchronized VoiceChatClient getSynchronizedProxyInstance() {
        return (VoiceChatClient) proxy;
    }

    public static synchronized VoiceChatClient getSynchronizedServerInstance() {
        return (VoiceChatClient) proxy;
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.initMod(this, event);
    }

    @Mod.EventHandler
    public void serverAboutToStart(FMLServerAboutToStartEvent event) {
        proxy.serverAboutToStart(event);
    }

    @Mod.EventHandler
    public void initServer(FMLServerStartedEvent event) {
        proxy.initServer(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInitMod(this, event);
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        this.registerNetwork();
        proxy.commonInit(event);
        proxy.preInitClient(event);
    }

    @Mod.EventHandler
    public void preInitServer(FMLServerStartingEvent event) {
        proxy.preInitServer(event);
    }

    @Mod.EventHandler
    public void aboutToStartServer(FMLServerAboutToStartEvent event) {
        proxy.aboutToStartServer(event);
    }

    private void registerNetwork() {
        DISPATCH = NetworkRegistry.INSTANCE.newSimpleChannel("GVC");
        DISPATCH.registerMessage(MinecraftServerVoicePacket.class, MinecraftServerVoicePacket.class, 1, Side.SERVER);
        DISPATCH.registerMessage(MinecraftServerVoiceEndPacket.class, MinecraftServerVoiceEndPacket.class, 2, Side.SERVER);
        DISPATCH.registerMessage(MinecraftClientVoiceEndPacket.class, MinecraftClientVoiceEndPacket.class, 9, Side.SERVER);
        DISPATCH.registerMessage(MinecraftClientVoicePacket.class, MinecraftClientVoicePacket.class, 3, Side.CLIENT);
        DISPATCH.registerMessage(MinecraftClientEntityDataPacket.class, MinecraftClientEntityDataPacket.class, 4, Side.CLIENT);
        DISPATCH.registerMessage(MinecraftClientEntityPositionPacket.class, MinecraftClientEntityPositionPacket.class, 5, Side.CLIENT);
        DISPATCH.registerMessage(MinecraftClientVoiceServerPacket.class, MinecraftClientVoiceServerPacket.class, 6, Side.CLIENT);
        DISPATCH.registerMessage(MinecraftClientVoiceAuthenticatedServer.class, MinecraftClientVoiceAuthenticatedServer.class, 7, Side.CLIENT);
    }

    @Mod.EventHandler
    public void stopServer(FMLServerStoppedEvent event) {
        proxy.stop();
        getLogger().info("Stopped Voice Server.");
    }

    public static ArrayList<EntityPlayerMP> getPlayers(MinecraftServer server) {
        List<?> entities = server.getConfigurationManager().playerEntityList;
        ArrayList<EntityPlayerMP> players = new ArrayList<>();
        for (Object o : entities) {
            if (o instanceof EntityPlayerMP) players.add((EntityPlayerMP) o);
        }
        return players;
    }
}
