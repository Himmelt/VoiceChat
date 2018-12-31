package net.gliby.voicechat.common.networking.packets;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.gliby.voicechat.VoiceChat;
import net.gliby.voicechat.common.networking.MinecraftPacket;

public class MinecraftClientVoicePacket extends MinecraftPacket implements IMessageHandler<MinecraftClientVoicePacket, IMessage> {

    byte divider;
    byte[] samples;
    int entityID;
    boolean direct;


    public MinecraftClientVoicePacket() {
    }

    public MinecraftClientVoicePacket(byte divider, byte[] samples, int entityID, boolean direct) {
        this.divider = divider;
        this.samples = samples;
        this.entityID = entityID;
        this.direct = direct;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.divider = buf.readByte();
        this.entityID = buf.readInt();
        this.direct = buf.readBoolean();
        this.samples = new byte[buf.readableBytes()];
        buf.readBytes(this.samples);
    }

    @Override
    public IMessage onMessage(MinecraftClientVoicePacket packet, MessageContext ctx) {
        if (VoiceChat.getProxyInstance().getClientNetwork().isConnected()) {
            VoiceChat.getProxyInstance().getClientNetwork().getVoiceClient().handlePacket(packet.entityID, packet.samples, packet.divider, packet.direct);
        }

        return null;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(this.divider);
        buf.writeInt(this.entityID);
        buf.writeBoolean(this.direct);
        buf.writeBytes(this.samples);
    }
}
