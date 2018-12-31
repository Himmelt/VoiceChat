package net.gliby.voicechat.common.api;

import cpw.mods.fml.common.eventhandler.EventBus;
import net.gliby.voicechat.VoiceChat;
import net.gliby.voicechat.common.networking.ServerStreamHandler;

public class VoiceChatAPI {

    private static VoiceChatAPI instance;
    private ServerStreamHandler handler;
    private EventBus eventBus;

    public static VoiceChatAPI instance() {
        return instance;
    }

    public EventBus bus() {
        return this.eventBus;
    }

    public void init() {
        instance = this;
        this.eventBus = new EventBus();
        this.bus().register(this.handler = new ServerStreamHandler(VoiceChat.getServerInstance()));
    }

    public void setCustomStreamHandler(net.gliby.voicechat.common.api.examples.ExampleStreamHandlerOnlyOP eventHandler) {
        this.eventBus = null;
        this.eventBus = new EventBus();
        this.eventBus.register(eventHandler);
    }
}
