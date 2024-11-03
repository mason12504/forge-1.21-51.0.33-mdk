package net.CarsonKing.codingmod.widget;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.Font;
import org.lwjgl.glfw.GLFW;

public class TextAreaWidget extends EditBox {
    public TextAreaWidget(Font font, int x, int y, int width, int height) {
        super(font, x, y, width, height, Component.literal(""));
        this.setMaxLength(10000);
        this.setFocused(true);
        this.setVisible(true);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        // Allow line breaks
        if (codePoint == '\n' || codePoint == '\r') {
            this.insertText("\n");
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Handle the Enter key to insert a new line
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            this.insertText("\n");
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
