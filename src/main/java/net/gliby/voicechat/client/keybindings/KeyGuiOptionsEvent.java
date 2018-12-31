package net.gliby.voicechat.client.keybindings;

import net.gliby.voicechat.client.VoiceChatClient;
import net.gliby.voicechat.client.gui.options.GuiScreenVoiceChatOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

public class KeyGuiOptionsEvent extends KeyEvent {

    private final VoiceChatClient voiceChat;

    public KeyGuiOptionsEvent(VoiceChatClient voiceChat, EnumBinding keyBind, int keyID, boolean repeating) {
        super(keyBind, keyID, repeating);
        this.voiceChat = voiceChat;
    }

    @Override
    public void keyDown(KeyBinding kb, boolean tickEnd, boolean isRepeat) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc != null && mc.currentScreen == null && mc.theWorld != null && tickEnd) {
            mc.displayGuiScreen(new GuiScreenVoiceChatOptions(this.voiceChat));
        }

    }

    @Override
    public void keyUp(KeyBinding kb, boolean tickEnd) {
    }
}
