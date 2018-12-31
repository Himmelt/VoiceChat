package net.gliby.voicechat.client.networking.game;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;
import net.gliby.voicechat.VoiceChat;

public class ClientDisconnectHandler {
    @SubscribeEvent
    public void onClientDisconnected(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            VoiceChat.getProxyInstance().getClientNetwork().stopClientNetwork();
        }
    }
}
