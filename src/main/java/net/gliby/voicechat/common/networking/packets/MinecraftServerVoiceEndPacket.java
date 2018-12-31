package net.gliby.voicechat.common.networking.packets;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.gliby.voicechat.VoiceChat;
import net.gliby.voicechat.common.networking.MinecraftPacket;

public class MinecraftServerVoiceEndPacket extends MinecraftPacket implements IMessageHandler<MinecraftServerVoiceEndPacket, IMessage> {

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public IMessage onMessage(MinecraftServerVoiceEndPacket packet, MessageContext ctx) {
        VoiceChat.getServerInstance().getVoiceServer().handleVoiceData(ctx.getServerHandler().playerEntity, null, (byte) 0, ctx.getServerHandler().playerEntity.getEntityId(), true);
        return null;
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }
}
