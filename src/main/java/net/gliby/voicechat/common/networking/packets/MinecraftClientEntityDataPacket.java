package net.gliby.voicechat.common.networking.packets;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.gliby.voicechat.VoiceChat;
import net.gliby.voicechat.common.networking.MinecraftPacket;

public class MinecraftClientEntityDataPacket extends MinecraftPacket implements IMessageHandler<MinecraftClientEntityDataPacket, IMessage> {

    private int entityID;
    private String username;
    private double x;
    private double y;
    private double z;


    public MinecraftClientEntityDataPacket() {
    }

    public MinecraftClientEntityDataPacket(int entityID, String username, double x, double y, double z) {
        this.entityID = entityID;
        this.username = username;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.entityID = buf.readInt();
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.username = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public IMessage onMessage(MinecraftClientEntityDataPacket packet, MessageContext ctx) {
        if (VoiceChat.getProxyInstance().getClientNetwork().isConnected()) {
            VoiceChat.getProxyInstance().getClientNetwork().handleEntityData(packet.entityID, packet.username, packet.x, packet.y, packet.z);
        }

        return null;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.entityID);
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        ByteBufUtils.writeUTF8String(buf, this.username);
    }
}
