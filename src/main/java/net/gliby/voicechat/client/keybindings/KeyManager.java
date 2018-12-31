package net.gliby.voicechat.client.keybindings;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.gliby.voicechat.client.VoiceChatClient;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class KeyManager {

    private final VoiceChatClient voiceChat;
    @SideOnly(Side.CLIENT)
    private final List<KeyEvent> keyEvents = new ArrayList<KeyEvent>();
    protected boolean[] keyDown;


    public KeyManager(VoiceChatClient voiceChat) {
        this.voiceChat = voiceChat;
    }

    @SideOnly(Side.CLIENT)
    public List<? extends KeyEvent> getKeyEvents() {
        return this.keyEvents;
    }

    public String getKeyName(EnumBinding binding) {
        for (int i = 0; i < this.keyEvents.size(); ++i) {
            KeyEvent event = this.keyEvents.get(i);
            if (event.keyBind == binding) {
                return Keyboard.getKeyName(event.keyID);
            }
        }

        return null;
    }

    public void init() {
        this.keyEvents.add(new KeySpeakEvent(this.voiceChat, EnumBinding.SPEAK, 47, false));
        this.keyEvents.add(new KeyGuiOptionsEvent(this.voiceChat, EnumBinding.OPEN_GUI_OPTIONS, 52, false));
        this.registerKeyBindings();
        FMLCommonHandler.instance().bus().register(this);
    }

    @SubscribeEvent
    public void keyEvent(InputEvent.KeyInputEvent event) {
        for (int i = 0; i < this.keyEvents.size(); ++i) {
            KeyEvent keyEvent = this.keyEvents.get(i);
            KeyBinding keyBinding = this.keyEvents.get(i).forgeKeyBinding;
            int keyCode = keyBinding.getKeyCode();
            boolean state = keyCode < 0 ? Mouse.isButtonDown(keyCode + 100) : Keyboard.isKeyDown(keyCode);
            boolean tickEnd = true;
            if (state != this.keyDown[i] || state && keyEvent.repeating) {
                if (state) {
                    keyEvent.keyDown(keyBinding, true, state != this.keyDown[i]);
                } else {
                    keyEvent.keyUp(keyBinding, true);
                }

                this.keyDown[i] = state;
            }
        }

    }

    private KeyBinding[] registerKeyBindings() {
        KeyBinding[] keyBinding = new KeyBinding[this.keyEvents.size()];

        for (int i = 0; i < keyBinding.length; ++i) {
            KeyEvent keyEvent = this.keyEvents.get(i);
            keyBinding[i] = new KeyBinding(keyEvent.keyBind.name, keyEvent.keyID, "key.categories.multiplayer");
            this.keyDown = new boolean[keyBinding.length];
            keyEvent.forgeKeyBinding = keyBinding[i];
            ClientRegistry.registerKeyBinding(keyBinding[i]);
        }

        return keyBinding;
    }
}
