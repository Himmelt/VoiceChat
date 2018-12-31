package net.gliby.voicechat.client.gui;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.ModMetadata;
import net.gliby.gman.ModInfo;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class GuiScreenDonate extends GuiScreen {

    private final GuiScreen parent;
    private final ModInfo info;
    private final ModMetadata modMetadata;
    private ResourceLocation cachedLogo;
    private Dimension cachedLogoDimensions;

    public GuiScreenDonate(ModInfo info, ModMetadata modMetadata, GuiScreen parent) {
        this.parent = parent;
        this.info = info;
        this.modMetadata = modMetadata;
    }

    @Override
    public void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0:
                this.mc.displayGuiScreen(this.parent);
                break;
            case 1:
                this.openURL(this.info.donateURL);
        }

    }

    @Override
    public void drawScreen(int x, int y, float tick) {
        this.renderModLogo(this.info.modId, this.modMetadata, false);
        this.drawBackground(0);
        GL11.glPushMatrix();
        GL11.glTranslatef((float) (this.width / 2 - this.cachedLogoDimensions.width / 2), (float) (this.height / 2 - (this.cachedLogoDimensions.height + 60)), 0.0F);
        this.renderModLogo(this.info.modId, this.modMetadata, true);
        GL11.glPopMatrix();
        String s = I18n.format("menu.gman.supportGliby.description");
        this.fontRendererObj.drawSplitString(s, this.width / 2 - 150, this.height / 2 - 50, 300, -1);
        String s1 = I18n.format("menu.gman.supportGliby.contact");
        this.fontRendererObj.drawSplitString(s1, this.width / 2 - 150, this.height / 2 + 35, 300, -1);
        super.drawScreen(x, y, tick);
    }

    @Override
    public void initGui() {
        buttonList.add(new GuiButton(0, this.width / 2 - 50, this.height - 34, 100, 20, I18n.format("gui.back")));
        buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 2, I18n.format("menu.gman.donate")));
    }

    private void openURL(String par1URI) {
        try {
            Class<?> clazz = Class.forName("java.awt.Desktop");
            Object object = clazz.getMethod("getDesktop", new Class[0]).invoke(null);
            clazz.getMethod("browse", new Class[]{URI.class}).invoke(object, new URI(par1URI));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }

    private void renderModLogo(String modId, ModMetadata modMetadata, boolean draw) {
        String logoFile = modMetadata.logoFile;
        if (!logoFile.isEmpty()) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            TextureManager tm = this.mc.getTextureManager();
            IResourcePack pack = FMLClientHandler.instance().getResourcePackFor(modId);

            try {
                if (this.cachedLogo == null) {
                    BufferedImage e = null;
                    if (pack != null) {
                        e = pack.getPackImage();
                    } else {
                        InputStream logoResource = this.getClass().getResourceAsStream(logoFile);
                        if (logoResource != null) {
                            e = ImageIO.read(logoResource);
                        }
                    }

                    if (e != null) {
                        this.cachedLogo = tm.getDynamicTextureLocation("modlogo", new DynamicTexture(e));
                        this.cachedLogoDimensions = new Dimension(e.getWidth(), e.getHeight());
                    }
                }

                if (this.cachedLogo != null && draw) {

                    this.mc.renderEngine.bindTexture(this.cachedLogo);
                    double scaleX = (double) this.cachedLogoDimensions.width / 200.0D;
                    double scaleY = (double) this.cachedLogoDimensions.height / 65.0D;
                    double scale = 1.0D;
                    if (scaleX > 1.0D || scaleY > 1.0D) {
                        scale = 1.0D / Math.max(scaleX, scaleY);
                    }

                    Dimension var10000 = this.cachedLogoDimensions;
                    var10000.width = (int) ((double) var10000.width * scale);
                    var10000 = this.cachedLogoDimensions;
                    var10000.height = (int) ((double) var10000.height * scale);
                    Tessellator tess = Tessellator.instance;
                    tess.startDrawingQuads();
                    tess.addVertexWithUV(0.0D, (double) this.cachedLogoDimensions.height, (double) this.zLevel, 0.0D, 1.0D);
                    tess.addVertexWithUV((double) (0 + this.cachedLogoDimensions.width), (double) (0 + this.cachedLogoDimensions.height), (double) this.zLevel, 1.0D, 1.0D);
                    tess.addVertexWithUV((double) (0 + this.cachedLogoDimensions.width), 0.0D, (double) this.zLevel, 1.0D, 0.0D);
                    tess.addVertexWithUV(0.0D, 0.0D, (double) this.zLevel, 0.0D, 0.0D);
                    tess.draw();


                    /*this.mc.renderEngine.bindTexture(this.cachedLogo);
                    double e1 = (double) this.cachedLogoDimensions.width / 200.0D;
                    double scaleY = (double) this.cachedLogoDimensions.height / 65.0D;
                    double scale = 1.0D;
                    if (e1 > 1.0D || scaleY > 1.0D) {
                        scale = 1.0D / Math.max(e1, scaleY);
                    }

                    this.cachedLogoDimensions.width = (int) ((double) this.cachedLogoDimensions.width * scale);
                    this.cachedLogoDimensions.height = (int) ((double) this.cachedLogoDimensions.height * scale);
                    Tessellator tessellator = Tessellator.instance;
                    BufferBuilder renderer = tessellator.getBuffer();
                    renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
                    renderer.pos(0.0D, (double) this.cachedLogoDimensions.height, (double) this.zLevel).tex(0.0D, 1.0D).endVertex();
                    renderer.pos((double) (this.cachedLogoDimensions.width), (double) (this.cachedLogoDimensions.height), (double) this.zLevel).tex(1.0D, 1.0D).endVertex();
                    renderer.pos((double) (this.cachedLogoDimensions.width), 0.0D, (double) this.zLevel).tex(1.0D, 0.0D).endVertex();
                    renderer.pos(0.0D, 0.0D, (double) this.zLevel).tex(0.0D, 0.0D).endVertex();
                    tessellator.draw();*/
                }
            } catch (IOException var15) {
                var15.printStackTrace();
            }
        }
    }
}
