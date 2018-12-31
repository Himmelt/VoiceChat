package net.gliby.voicechat.common.api.examples;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.gliby.voicechat.common.api.VoiceChatAPI;
import net.gliby.voicechat.common.api.events.ServerStreamEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

import java.util.List;

public class ExampleStreamHandlerOnlyOP {

    public ExampleStreamHandlerOnlyOP() {
        VoiceChatAPI.instance().setCustomStreamHandler(this);
    }

    @SubscribeEvent
    public void createStream(ServerStreamEvent.StreamCreated event) {
        if (!this.isOP(event.stream.player)) {
            event.stream.player.addChatMessage(new ChatComponentText("Only OP\'s are allowed to talk!"));
        }
    }

    @SubscribeEvent
    public void feedStream(ServerStreamEvent.StreamFeed event) {
        List<?> players = event.stream.player.mcServer.getConfigurationManager().playerEntityList;//.getPlayers();
        EntityPlayerMP speaker = event.stream.player;
        if (this.isOP(speaker)) {
            for (int i = 0; i < players.size(); ++i) {
                Object player = players.get(i);
                if (player instanceof EntityPlayerMP) {
                    EntityPlayerMP mp = (EntityPlayerMP) player;
                    if (this.isOP(mp) && mp.getEntityId() != speaker.getEntityId()) {
                        event.streamManager.feedStreamToPlayer(event.stream, event.voiceLet, mp, false);
                    }
                }
            }
        }
    }

    public boolean isOP(EntityPlayerMP player) {
        return player.mcServer.getConfigurationManager().func_152603_m().func_152683_b(player.getGameProfile()) != null;
    }
}
