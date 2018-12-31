package net.gliby.voicechat.client;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.gliby.voicechat.VoiceChat;
import net.gliby.voicechat.client.debug.Statistics;
import net.gliby.voicechat.client.gui.GuiInGameHandlerVoiceChat;
import net.gliby.voicechat.client.keybindings.KeyManager;
import net.gliby.voicechat.client.keybindings.KeyTickHandler;
import net.gliby.voicechat.client.networking.ClientNetwork;
import net.gliby.voicechat.client.networking.game.ClientDisconnectHandler;
import net.gliby.voicechat.client.networking.game.ClientEventHandler;
import net.gliby.voicechat.client.render.RenderPlayerVoiceIcon;
import net.gliby.voicechat.client.sound.ClientStreamManager;
import net.gliby.voicechat.client.sound.Recorder;
import net.gliby.voicechat.client.sound.SoundSystemWrapper;
import net.gliby.voicechat.common.VoiceChatServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class VoiceChatClient extends VoiceChatServer {

    public static ModMetadata modMetadata;
    @SideOnly(Side.CLIENT)
    private static ClientStreamManager soundManager;
    @SideOnly(Side.CLIENT)
    private static Statistics stats;
    @SideOnly(Side.CLIENT)
    public KeyManager keyManager;
    @SideOnly(Side.CLIENT)
    public SoundSystemWrapper sndSystem;
    public Recorder recorder;
    public Map<String, Integer> specialPlayers = new HashMap<String, Integer>();
    String[] testPlayers = new String[]{"captaindogfish", "starguy1245", "SheheryaB", "arsham123", "Chris9awesome", "TechnoX_X", "bubz052", "McJackson3180", "InfamousArgyle", "jdf2", "XxNotexX0", "SirDenerim", "Frankspark", "smith70831", "killazombiecow", "CraftAeternalis", "choclaterainxx", "dragonballkid4", "TH3_CR33PER", "yetshadow", "KristinnVikarJ", "TheMCBros99", "kevinlame"};
    @SideOnly(Side.CLIENT)
    private File configurationDirectory;
    @SideOnly(Side.CLIENT)
    private Settings settings;
    @SideOnly(Side.CLIENT)
    private ClientNetwork clientNetwork;
    @SideOnly(Side.CLIENT)
    private boolean recorderActive;
    @SideOnly(Side.CLIENT)
    private VoiceChat voiceChat;

    public static synchronized Logger getLogger() {
        return LOGGER;
    }

    public static ModMetadata getModMetadata() {
        return modMetadata;
    }

    public static ClientStreamManager getSoundManager() {
        return soundManager;
    }

    public static Statistics getStatistics() {
        return stats;
    }

    public ClientNetwork getClientNetwork() {
        return this.clientNetwork;
    }

    private SoundManager getMinecraftSoundManager(Minecraft mc) {
        try {
            Field e = SoundHandler.class.getDeclaredFields()[5];
            e.setAccessible(true);
            return (SoundManager) e.get(mc.getSoundHandler());
        } catch (Exception var4) {
            var4.printStackTrace();
            return null;
        }
    }

    public Settings getSettings() {
        return this.settings;
    }

    public String[] getTestPlayers() {
        return this.testPlayers;
    }

    @Override
    public void initMod(VoiceChat voiceChat, FMLInitializationEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        this.voiceChat = voiceChat;
        this.recorder = new Recorder(this);
        this.keyManager.init();
        if (this.settings.isDebug()) {
            getLogger().info("Debug enabled!");
            stats = new Statistics();
        }

        getLogger().info("Started client-side on version (" + this.getVersion() + ")" + "");
        this.clientNetwork = new ClientNetwork(this);
        MinecraftForge.EVENT_BUS.register(new GuiInGameHandlerVoiceChat(this));
        MinecraftForge.EVENT_BUS.register(new RenderPlayerVoiceIcon(this, mc));
        MinecraftForge.EVENT_BUS.register(this.sndSystem = new SoundSystemWrapper(mc.getSoundHandler()));
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler(this));
        FMLCommonHandler.instance().bus().register(new ClientDisconnectHandler());
        FMLCommonHandler.instance().bus().register(new KeyTickHandler(this));
        getLogger().info("Created SoundSystemWrapper: " + this.sndSystem + ".");
    }

    public final boolean isRecorderActive() {
        return this.recorderActive;
    }

    public void setRecorderActive(boolean b) {
        if (this.clientNetwork.voiceClientExists()) {
            this.recorderActive = b;
        }

    }

    @Override
    public void preInitClient(FMLPreInitializationEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        (new UpdatedSoundManager(this, this.getMinecraftSoundManager(mc))).init(event);
        modMetadata = event.getModMetadata();
        this.configurationDirectory = new File(event.getModConfigurationDirectory(), "gliby_vc");
        if (!this.configurationDirectory.exists()) {
            this.configurationDirectory.mkdir();
        }

        this.settings = new Settings(new File(this.configurationDirectory, "ClientSettings.ini"));
        this.settings.init();
        this.keyManager = new KeyManager(this);
        this.specialPlayers.put("theGliby", 1);
        this.specialPlayers.put("Rinto", 1);
        this.specialPlayers.put("DanielSturk", 1);
        this.specialPlayers.put("CraftAeternalis", 3);
        this.specialPlayers.put("YETSHADOW", 5);
        this.specialPlayers.put("McJackson3180", 6);
        this.specialPlayers.put("smith70831", 7);
        this.specialPlayers.put("XxNotexX0", 8);
        this.specialPlayers.put("TheHaxman2", 9);
        soundManager = new ClientStreamManager(Minecraft.getMinecraft(), this);
        soundManager.init();
    }
}
