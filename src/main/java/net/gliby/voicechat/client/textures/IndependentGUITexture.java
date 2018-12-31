package net.gliby.voicechat.client.textures;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class IndependentGUITexture {

    public static final IndependentGUITexture TEXTURES = new IndependentGUITexture("gvctextures");
    public static final IndependentGUITexture GUI_WIZARD = new IndependentGUITexture("wizard_gui");
    private static final ResourceLocation steve = new ResourceLocation("textures/entity/steve.png");
    private final ResourceLocation resource;

    public IndependentGUITexture(String texture) {
        this.resource = new ResourceLocation("gvc:textures/gui/" + texture + ".png");
    }

    public static void bindClientPlayer(Minecraft mc) {
        mc.getTextureManager().bindTexture(mc.thePlayer.getLocationSkin());
    }

    public static void bindDefaultPlayer(Minecraft mc) {
        mc.getTextureManager().bindTexture(steve);
    }

    public static void bindPlayer(Minecraft mc, Entity entity) {
        mc.getTextureManager().bindTexture(((AbstractClientPlayer) entity).getLocationSkin());
    }

    public void bindTexture(Minecraft mc) {
        mc.getTextureManager().bindTexture(this.getTexture());
    }

    public ResourceLocation getTexture() {
        return this.resource;
    }

}
