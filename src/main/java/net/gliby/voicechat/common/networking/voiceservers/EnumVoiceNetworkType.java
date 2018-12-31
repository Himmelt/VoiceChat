package net.gliby.voicechat.common.networking.voiceservers;


public enum EnumVoiceNetworkType {

    MINECRAFT("MINECRAFT", 0, "Minecraft", false),
    UDP("UDP", 1, "UDP", true);
    public boolean authRequired;
    public String name;

    EnumVoiceNetworkType(String var1, int var2, String name, boolean authRequired) {
        this.name = name;
        this.authRequired = authRequired;
    }
}
