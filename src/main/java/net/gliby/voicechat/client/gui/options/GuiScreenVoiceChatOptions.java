package net.gliby.voicechat.client.gui.options;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.gliby.voicechat.client.VoiceChatClient;
import net.gliby.voicechat.client.device.Device;
import net.gliby.voicechat.client.gui.*;
import net.gliby.voicechat.client.sound.MicrophoneTester;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import org.lwjgl.opengl.GL11;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class GuiScreenVoiceChatOptions extends GuiScreen {

    private final VoiceChatClient voiceChat;
    private final MicrophoneTester tester;
    private GuiCustomButton advancedOptions;
    private GuiCustomButton mutePlayer;
    private GuiBoostSlider boostSlider;
    private GuiBoostSlider voiceVolume;
    private GuiDropDownMenu dropDown;
    private GuiButton UIPosition;
    private GuiButton microphoneMode;
    private List<String> warningMessages;
    private String updateMessage;


    public GuiScreenVoiceChatOptions(VoiceChatClient voiceChat) {
        this.voiceChat = voiceChat;
        this.tester = new MicrophoneTester(voiceChat);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0:
                if (button instanceof GuiDropDownMenu && !this.voiceChat.getSettings().getDeviceHandler().isEmpty()) {
                    ((GuiDropDownMenu) button).dropDownMenu = !((GuiDropDownMenu) button).dropDownMenu;
                }
                break;
            case 1:
                this.voiceChat.getSettings().getConfiguration().save();
                this.mc.displayGuiScreen(new GuiScreenDonate(this.voiceChat.getModInfo(), VoiceChatClient.getModMetadata(), this));
                break;
            case 2:
                if (!this.tester.recording) {
                    this.tester.start();
                } else {
                    this.tester.stop();
                }

                button.displayString = this.tester.recording ? I18n.format("menu.microphoneStopTest") : I18n.format("menu.microphoneTest");
                break;
            case 3:
                this.voiceChat.getSettings().getConfiguration().save();
                this.mc.displayGuiScreen(null);
                break;
            case 4:
                this.mc.displayGuiScreen(new GuiScreenOptionsUI(this.voiceChat, this));
                break;
            case 5:
                if (!this.dropDown.dropDownMenu) {
                    this.microphoneMode.visible = true;
                    this.microphoneMode.enabled = true;
                    this.voiceChat.getSettings().setSpeakMode(this.voiceChat.getSettings().getSpeakMode() == 0 ? 1 : 0);
                    this.microphoneMode.displayString = I18n.format("menu.speakMode") + ": " + (this.voiceChat.getSettings().getSpeakMode() == 0 ? I18n.format("menu.speakModePushToTalk") : I18n.format("menu.speakModeToggleToTalk"));
                } else if (this.voiceChat.getSettings().getDeviceHandler().isEmpty()) {
                    this.microphoneMode.visible = false;
                    this.microphoneMode.enabled = false;
                }
                break;
            case 897:
                if (!this.dropDown.dropDownMenu) {
                    this.mc.displayGuiScreen(new GuiScreenLocalMute(this, this.voiceChat));
                }
                break;
            case 898:
                if (!this.dropDown.dropDownMenu) {
                    this.mc.displayGuiScreen(new GuiScreenOptionsWizard(this.voiceChat, this));
                }
                break;
            case 899:
                if (!this.dropDown.dropDownMenu) {
                    this.mc.displayGuiScreen(new GuiScreenVoiceChatOptionsAdvanced(this.voiceChat, this));
                }
        }

    }

    @Override
    public void drawScreen(int x, int y, float tick) {
        this.drawDefaultBackground();
        GL11.glPushMatrix();
        float scale = 1.5F;
        GL11.glTranslatef((float) (this.width / 2) - (float) (this.fontRendererObj.getStringWidth("Gliby\'s Voice Chat Options") / 2) * 1.5F, 0.0F, 0.0F);
        GL11.glScalef(1.5F, 1.5F, 0.0F);
        this.drawString(this.fontRendererObj, "Gliby\'s Voice Chat Options", 0, 6, -1);
        GL11.glPopMatrix();

        for (int i = 0; i < this.warningMessages.size(); ++i) {
            int warnY = i * this.fontRendererObj.FONT_HEIGHT + this.height / 2 + 66 - this.fontRendererObj.FONT_HEIGHT * this.warningMessages.size() / 2;
            this.drawCenteredString(this.fontRendererObj, this.warningMessages.get(i), this.width / 2, warnY, -1);
        }

        super.drawScreen(x, y, tick);
    }

    public boolean inBounds(int x, int y, int posX, int posY, int width, int height) {
        return x >= posX && y >= posY && x < posX + width && y < posY + height;
    }

    @Override
    public void initGui() {
        String[] array = new String[this.voiceChat.getSettings().getDeviceHandler().getDevices().size()];

        for (int heightOffset = 0; heightOffset < this.voiceChat.getSettings().getDeviceHandler().getDevices().size(); ++heightOffset) {
            array[heightOffset] = (this.voiceChat.getSettings().getDeviceHandler().getDevices().get(heightOffset)).getName();
        }

        boolean var3 = true;
        this.dropDown = new GuiDropDownMenu(0, this.width / 2 - 152, this.height / 2 - 55, 150, 20, this.voiceChat.getSettings().getInputDevice() != null ? this.voiceChat.getSettings().getInputDevice().getName() : "None", array);
        this.microphoneMode = new GuiButton(5, this.width / 2 - 152, this.height / 2 + 25 - 55, 150, 20, I18n.format("menu.speakMode") + ": " + (this.voiceChat.getSettings().getSpeakMode() == 0 ? I18n.format("menu.speakModePushToTalk") : I18n.format("menu.speakModeToggleToTalk")));
        this.UIPosition = new GuiButton(4, this.width / 2 + 2, this.height / 2 + 25 - 55, 150, 20, I18n.format("menu.uiOptions"));
        this.voiceVolume = new GuiBoostSlider(910, this.width / 2 + 2, this.height / 2 - 25 - 55, "", I18n.format("menu.worldVolume") + ": " + (this.voiceChat.getSettings().getWorldVolume() == 0.0F ? I18n.format("options.off") : (int) (this.voiceChat.getSettings().getWorldVolume() * 100.0F) + "%"), 0.0F);
        this.voiceVolume.sliderValue = this.voiceChat.getSettings().getWorldVolume();
        this.boostSlider = new GuiBoostSlider(900, this.width / 2 + 2, this.height / 2 - 55, "", I18n.format("menu.boost") + ": " + ((int) (this.voiceChat.getSettings().getInputBoost() * 5.0F) <= 0 ? I18n.format("options.off") : "" + (int) (this.voiceChat.getSettings().getInputBoost() * 5.0F) + "db"), 0.0F);
        this.boostSlider.sliderValue = this.voiceChat.getSettings().getInputBoost();
        this.advancedOptions = new GuiCustomButton(899, this.width / 2 + 2, this.height / 2 + 49 - 55, 150, 20, I18n.format("menu.advancedOptions"));
        buttonList.add(new GuiButton(1, this.width / 2 - 151, this.height - 34, 75, 20, I18n.format("menu.gman.supportGliby")));
        buttonList.add(new GuiButton(2, this.width / 2 - 152, this.height / 2 - 25 - 55, 150, 20, !this.tester.recording ? I18n.format("menu.microphoneTest") : I18n.format("menu.microphoneStopTest")));
        buttonList.add(new GuiButton(3, this.width / 2 - 75, this.height - 34, 150, 20, I18n.format("menu.returnToGame")));
        buttonList.add(this.advancedOptions);
        buttonList.add(new GuiCustomButton(898, this.width / 2 - 152, this.height / 2 + 49 - 55, 150, 20, I18n.format("menu.openOptionsWizard")));
        buttonList.add(this.UIPosition);
        buttonList.add(this.microphoneMode);
        buttonList.add(this.boostSlider);
        buttonList.add(this.voiceVolume);
        buttonList.add(this.mutePlayer = new GuiCustomButton(897, this.width / 2 - 152, this.height / 2 + 73 - 55, 304, 20, I18n.format("menu.mutePlayers")));
        buttonList.add(this.dropDown);
        if (this.voiceChat.getSettings().getDeviceHandler().isEmpty()) {
            Object button = this.buttonList.get(0);
            if (button instanceof GuiButton) ((GuiButton) button).enabled = false;
            button = buttonList.get(3);
            if (button instanceof GuiButton) ((GuiButton) button).enabled = false;
            //this.buttonList.get(0).enabled = false;
            //this.buttonList.get(3).enabled = false;
            this.boostSlider.enabled = false;
            this.mutePlayer.enabled = false;
            this.microphoneMode.enabled = false;
            this.mutePlayer.enabled = false;
        }

        super.initGui();
        this.warningMessages = new ArrayList<String>();
        if (this.voiceChat.getSettings().getDeviceHandler().isEmpty()) {
            this.warningMessages.add(ChatFormatting.DARK_RED + "No input devices found, add input device and restart Minecraft.");
        }

        if (this.voiceChat.getModInfo().updateNeeded()) {
            this.warningMessages.add(this.updateMessage = I18n.format("menu.downloadLatest") + "§b " + this.voiceChat.getModInfo().updateURL);
            this.warningMessages.add(ChatFormatting.RED + I18n.format("menu.modOutdated"));
        }

        if (this.mc.isSingleplayer() && !this.mc.getIntegratedServer().getPublic()) {
            this.warningMessages.add(ChatFormatting.RED + I18n.format("menu.warningSingleplayer"));
        }

        if (!this.voiceChat.getClientNetwork().isConnected() && !this.mc.isSingleplayer()) {
            this.warningMessages.add(ChatFormatting.RED + I18n.format("voicechat.cannotSend"));
        }
    }

    @Override
    public void keyTyped(char c, int key) {
        if (key == 1) {
            this.voiceChat.getSettings().getConfiguration().save();
            this.mc.displayGuiScreen(null);
            this.mc.setIngameFocus();
        }

    }

    @Override
    public void mouseClicked(int x, int y, int b) {
        if (b == 0) {
            if (!this.voiceChat.getModInfo().updateNeeded()) {
            }

            for (int e = 0; e < this.warningMessages.size(); ++e) {
                String s = this.warningMessages.get(e);
                if (s.equals(this.updateMessage)) {
                    int warnY = e * this.fontRendererObj.FONT_HEIGHT + this.height / 2 + 66 - this.fontRendererObj.FONT_HEIGHT * this.warningMessages.size() / 2;
                    int length = this.fontRendererObj.getStringWidth(s);
                    if (this.inBounds(x, y, this.width / 2 - length / 2, warnY, length, this.fontRendererObj.FONT_HEIGHT)) {
                        this.openURL(this.voiceChat.modInfo.updateURL);
                    }
                }
            }

            if (this.dropDown.getMouseOverInteger() != -1 && this.dropDown.dropDownMenu && !this.voiceChat.getSettings().getDeviceHandler().isEmpty()) {
                Device var9 = this.voiceChat.getSettings().getDeviceHandler().getDevices().get(this.dropDown.getMouseOverInteger());
                if (var9 == null) {
                    return;
                }

                this.voiceChat.getSettings().setInputDevice(var9);
                this.dropDown.setDisplayString(var9.getName());
            }
        }
        super.mouseClicked(x, y, b);
    }

    @Override
    public void onGuiClosed() {
        if (this.tester.recording) {
            this.tester.stop();
        }

    }

    private void openURL(String url) {
        try {
            Class<?> clazz = Class.forName("java.awt.Desktop");
            Object object = clazz.getMethod("getDesktop", new Class[0]).invoke(null);
            clazz.getMethod("browse", URI.class).invoke(object, new URI(url));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Override
    public void updateScreen() {
        this.voiceChat.getSettings().setWorldVolume(this.voiceVolume.sliderValue);
        this.voiceChat.getSettings().setInputBoost(this.boostSlider.sliderValue);
        this.voiceVolume.setDisplayString(I18n.format("menu.worldVolume") + ": " + (this.voiceChat.getSettings().getWorldVolume() == 0.0F ? I18n.format("options.off") : (int) (this.voiceChat.getSettings().getWorldVolume() * 100.0F) + "%"));
        this.boostSlider.setDisplayString(I18n.format("menu.boost") + ": " + ((int) (this.voiceChat.getSettings().getInputBoost() * 5.0F) <= 0 ? I18n.format("options.off") : (int) (this.voiceChat.getSettings().getInputBoost() * 5.0F) + "db"));
        this.advancedOptions.allowed = !this.dropDown.dropDownMenu;
        this.mutePlayer.allowed = !this.dropDown.dropDownMenu;
    }
}
