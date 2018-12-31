package net.gliby.voicechat.client.keybindings;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.settings.KeyBinding;

@SideOnly(Side.CLIENT)
public abstract class KeyEvent {

    public KeyBinding forgeKeyBinding;
    public EnumBinding keyBind;
    public int keyID = -1;
    public boolean repeating;


    public KeyEvent(EnumBinding keyBind, int keyID, boolean repeating) {
        this.keyBind = keyBind;
        this.keyID = keyID;
        this.repeating = repeating;
    }

    public abstract void keyDown(KeyBinding var1, boolean var2, boolean var3);

    public abstract void keyUp(KeyBinding var1, boolean var2);
}
