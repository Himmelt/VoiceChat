package net.gliby.voicechat.client.keybindings;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import net.gliby.voicechat.client.VoiceChatClient;

public class KeyTickHandler {

    VoiceChatClient voiceChat;

    public KeyTickHandler(VoiceChatClient voiceChat) {
        this.voiceChat = voiceChat;
    }

    @SubscribeEvent
    public void tick(TickEvent event) {
        if (event.type == TickEvent.Type.PLAYER && event.side == Side.CLIENT) {
            this.voiceChat.keyManager.keyEvent(null);
        }
    }
}
