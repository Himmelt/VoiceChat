package net.gliby.voicechat.client.gui;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.gliby.voicechat.VoiceChat;
import net.gliby.voicechat.client.VoiceChatClient;
import net.gliby.voicechat.client.debug.Statistics;
import net.gliby.voicechat.client.gui.options.GuiScreenOptionsWizard;
import net.gliby.voicechat.client.sound.ClientStream;
import net.gliby.voicechat.client.textures.IndependentGUITexture;
import net.gliby.voicechat.common.MathUtility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Text;
import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import java.util.ArrayList;

public class GuiInGameHandlerVoiceChat extends Gui {

    private final VoiceChatClient voiceChat;
    private final Minecraft mc;
    private long lastFrame;
    private long lastFPS;
    private float fade = 0.0F;
    private ScaledResolution scaled;
    private Vector2f position;
    private UIPosition positionUI;

    public GuiInGameHandlerVoiceChat(VoiceChatClient voiceChat) {
        this.voiceChat = voiceChat;
        this.mc = Minecraft.getMinecraft();
    }

    public void calcDelta() {
        if (this.getTime() - this.lastFPS > 1000L) {
            this.lastFPS += 1000L;
        }

    }

    public int getDelta() {
        long time = this.getTime();
        int delta = (int) (time - this.lastFrame);
        this.lastFrame = time;
        return delta;
    }

    private Vector2f getPosition(int width, int height, UIPosition uiPositionSpeak) {
        return uiPositionSpeak.type == 0 ? new Vector2f(uiPositionSpeak.x * (float) width, uiPositionSpeak.y * (float) height) : new Vector2f(uiPositionSpeak.x, uiPositionSpeak.y);
    }

    public long getTime() {
        return Sys.getTime() * 1000L / Sys.getTimerResolution();
    }

    @SubscribeEvent
    public void render(Text text) {
        if (text.type == ElementType.DEBUG && VoiceChat.getProxyInstance().getSettings().isDebug()) {
            VoiceChat.getProxyInstance();
            Statistics stats = VoiceChatClient.getStatistics();
            if (stats != null) {
                int settings = 1 | ValueFormat.PRECISION(2) | 192;
                String encodedAvg = ValueFormat.format((long) stats.getEncodedAverageDataReceived(), settings);
                String decodedAvg = ValueFormat.format((long) stats.getDecodedAverageDataReceived(), settings);
                String encodedData = ValueFormat.format((long) stats.getEncodedDataReceived(), settings);
                String decodedData = ValueFormat.format((long) stats.getDecodedDataReceived(), settings);
                ArrayList<String> right = text.right;
                right.add("Voice Chat Debug Info");
                right.add("VC Data [ENC AVG]: " + encodedAvg + "");
                right.add("VC Data [DEC AVG]: " + decodedAvg + "");
                right.add("VC Data [ENC REC]: " + encodedData + "");
                right.add("VC Data [DEC REC]: " + decodedData + "");
            }
        }

    }

    @SubscribeEvent
    public void renderInGameGui(RenderGameOverlayEvent.Post event) {
        if (event.type == ElementType.HOTBAR) {
            if (this.scaled == null) {
                this.getDelta();
                this.lastFPS = this.getTime();
                if (this.voiceChat.getSettings().isSetupNeeded()) {
                    this.mc.displayGuiScreen(new GuiScreenOptionsWizard(this.voiceChat, null));
                }
            }

            this.scaled = new ScaledResolution(this.mc, mc.displayWidth, mc.displayHeight);
            int width = this.scaled.getScaledWidth();
            int height = this.scaled.getScaledHeight();
            int delta = this.getDelta();
            this.calcDelta();
            if (!VoiceChat.getProxyInstance().isRecorderActive()) {
                if (this.fade > 0.0F) {
                    this.fade -= 0.01F * (float) delta;
                } else {
                    this.fade = 0.0F;
                }
            } else if (this.fade < 1.0F && VoiceChat.getProxyInstance().isRecorderActive()) {
                this.fade += 0.01F * (float) delta;
            } else {
                this.fade = 1.0F;
            }

            if (this.fade != 0.0F) {
                this.positionUI = this.voiceChat.getSettings().getUIPositionSpeak();
                this.position = this.getPosition(width, height, this.positionUI);
                if (this.positionUI.scale != 0.0F) {

                    GL11.glPushMatrix();
                    GL11.glEnable(3042);
                    GL11.glBlendFunc(770, 771);
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, this.fade * this.voiceChat.getSettings().getUIOpacity());
                    IndependentGUITexture.TEXTURES.bindTexture(this.mc);
                    GL11.glTranslatef(this.position.x + (float) this.positionUI.info.offsetX, this.position.y + (float) this.positionUI.info.offsetY, 0.0F);
                    GL11.glScalef(this.positionUI.scale, this.positionUI.scale, 1.0F);
                    this.drawTexturedModalRect(0, 0, 0, 0, 54, 46);
                    switch ((int) ((float) (Minecraft.getSystemTime() % 1000L) / 350.0F)) {
                        case 0:
                            this.drawTexturedModalRect(12, -3, 0, 47, 22, 49);
                            break;
                        case 1:
                            this.drawTexturedModalRect(31, -3, 23, 47, 14, 49);
                            break;
                        case 2:
                            this.drawTexturedModalRect(40, -3, 38, 47, 16, 49);
                    }

                    this.mc.getTextureManager().bindTexture(this.mc.thePlayer.getLocationSkin());
                    GL11.glScalef(0.6F, 0.3F, 0.0F);
                    GL11.glTranslatef(0.0F, 47.0F, 0.0F);
                    this.drawTexturedModalRect(0, 0, 32, 64, 32, 64);
                    GL11.glDisable(3042);
                    GL11.glPopMatrix();

                    ///////
                    /*GL11.glPushMatrix();
                    GL11.glEnable(3042);
                    GL11.glBlendFunc(770, 771);
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, this.fade * this.voiceChat.getSettings().getUIOpacity());
                    IndependentGUITexture.TEXTURES.bindTexture(this.mc);
                    GL11.glTranslatef(this.position.x + (float) this.positionUI.info.offsetX, this.position.y + (float) this.positionUI.info.offsetY, 0.0F);
                    GL11.glScalef(this.positionUI.scale, this.positionUI.scale, 1.0F);
                    this.drawTexturedModalRect(0, 0, 0, 0, 54, 46);
                    switch ((int) ((float) (Minecraft.getSystemTime() % 1000L) / 350.0F)) {
                        case 0:
                            this.drawTexturedModalRect(12, -3, 0, 47, 22, 49);
                            break;
                        case 1:
                            this.drawTexturedModalRect(31, -3, 23, 47, 14, 49);
                            break;
                        case 2:
                            this.drawTexturedModalRect(40, -3, 38, 47, 16, 49);
                    }

                    this.mc.getTextureManager().bindTexture(this.mc.thePlayer.getLocationSkin());
                    GL11.glTranslatef(0.0F, 14.0F, 0.0F);
                    GL11.glScalef(2.4F, 2.4F, 0.0F);
                    Gui.drawScaledCustomSizeModalRect(0, 0, 8.0F, 8.0F, 8, 8, 8, 8, 64.0F, 64.0F);
                    if (this.mc.thePlayer != null && this.mc.thePlayer.isWearing(EnumPlayerModelParts.HAT)) {
                        Gui.drawScaledCustomSizeModalRect(0, 0, 40.0F, 8.0F, 8, 8, 8, 8, 64.0F, 64.0F);
                    }

                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    GL11.glDisable(3042);
                    GL11.glPopMatrix();*/
                    /////
                }
            }

            if (!VoiceChatClient.getSoundManager().currentStreams.isEmpty() && this.voiceChat.getSettings().isVoicePlateAllowed()) {
                float scale = 0.0F;
                this.positionUI = this.voiceChat.getSettings().getUIPositionPlate();
                this.position = this.getPosition(width, height, this.positionUI);
                GL11.glEnable(3042);
                GL11.glBlendFunc(770, 771);

                for (int i = 0; i < VoiceChatClient.getSoundManager().currentStreams.size(); ++i) {
                    ClientStream stream = VoiceChatClient.getSoundManager().currentStreams.get(i);
                    if (stream != null) {
                        String s = stream.player.entityName();
                        boolean playerExists = stream.player.getPlayer() != null;
                        int length = this.mc.fontRenderer.getStringWidth(s);
                        scale = 0.75F * this.positionUI.scale;
                        GL11.glPushMatrix();
                        GL11.glTranslatef(this.position.x + (float) this.positionUI.info.offsetX, this.position.y + (float) this.positionUI.info.offsetY + (float) (i * 23) * scale, 0.0F);
                        GL11.glScalef(scale, scale, 0.0F);
                        GL11.glColor4f(1.0F, 1.0F, 1.0F, this.voiceChat.getSettings().getUIOpacity());
                        GL11.glTranslatef(0.0F, 0.0F, 0.0F);
                        IndependentGUITexture.TEXTURES.bindTexture(this.mc);
                        this.drawTexturedModalRect(0, 0, 56, stream.special * 22, 109, 22);
                        GL11.glPushMatrix();
                        scale = MathUtility.clamp(50.5F / (float) length, 0.0F, 1.25F);
                        GL11.glTranslatef(25.0F + scale / 2.0F, 11.0F - (float) (this.mc.fontRenderer.FONT_HEIGHT - 1) * scale / 2.0F, 0.0F);
                        GL11.glScalef(scale, scale, 0.0F);
                        this.drawString(this.mc.fontRenderer, s, 0, 0, -1);
                        GL11.glPopMatrix();
                        GL11.glPushMatrix();
                        GL11.glTranslatef(3.0F, 3.0F, 0.0F);
                        GL11.glScalef(0.48F, 0.24F, 0.0F);
                        if (playerExists) {
                            IndependentGUITexture.bindPlayer(this.mc, stream.player.getPlayer());
                        } else {
                            IndependentGUITexture.bindDefaultPlayer(this.mc);
                        }

                        GL11.glColor4f(1.0F, 1.0F, 1.0F, this.voiceChat.getSettings().getUIOpacity());
                        this.drawTexturedModalRect(0, 0, 32, 64, 32, 64);
                        this.drawTexturedModalRect(0, 0, 160, 64, 32, 64);
                        GL11.glPopMatrix();
                        GL11.glPopMatrix();

                        /*GL11.glPushMatrix();
                        GL11.glTranslatef(this.position.x + (float) this.positionUI.info.offsetX, this.position.y + (float) this.positionUI.info.offsetY + (float) (i * 23) * scale, 0.0F);
                        GL11.glScalef(scale, scale, 0.0F);
                        GL11.glColor4f(1.0F, 1.0F, 1.0F, this.voiceChat.getSettings().getUIOpacity());
                        GL11.glTranslatef(0.0F, 0.0F, 0.0F);
                        IndependentGUITexture.TEXTURES.bindTexture(this.mc);
                        this.drawTexturedModalRect(0, 0, 56, stream.special * 22, 109, 22);
                        GL11.glPushMatrix();
                        scale = MathUtility.clamp(50.5F / (float) length, 0.0F, 1.25F);
                        GL11.glTranslatef(25.0F + scale / 2.0F, 11.0F - (float) (this.mc.fontRenderer.FONT_HEIGHT - 1) * scale / 2.0F, 0.0F);
                        GL11.glScalef(scale, scale, 0.0F);
                        this.drawString(this.mc.fontRenderer, s, 0, 0, -1);
                        GL11.glPopMatrix();
                        GL11.glPushMatrix();
                        if (playerExists) {
                            IndependentGUITexture.bindPlayer(this.mc, stream.player.getPlayer());
                        } else {
                            IndependentGUITexture.bindDefaultPlayer(this.mc);
                        }

                        GL11.glColor4f(1.0F, 1.0F, 1.0F, this.voiceChat.getSettings().getUIOpacity());
                        GL11.glTranslatef(3.25F, 3.25F, 0.0F);
                        GL11.glScalef(2.0F, 2.0F, 0.0F);
                        Gui.drawScaledCustomSizeModalRect(0, 0, 8.0F, 8.0F, 8, 8, 8, 8, 64.0F, 64.0F);
                        if (this.mc.thePlayer != null && this.mc.thePlayer.isWearing(EnumPlayerModelParts.HAT)) {
                            Gui.drawScaledCustomSizeModalRect(0, 0, 40.0F, 8.0F, 8, 8, 8, 8, 64.0F, 64.0F);
                        }

                        GL11.glPopMatrix();
                        GL11.glPopMatrix();*/
                    }
                }

                GL11.glDisable(3042);
            }

            if (VoiceChatClient.getSoundManager().currentStreams.isEmpty()) {
                VoiceChatClient.getSoundManager().volumeControlStop();
            } else if (this.voiceChat.getSettings().isVolumeControlled()) {
                VoiceChatClient.getSoundManager().volumeControlStart();
            }
        }

    }
}
