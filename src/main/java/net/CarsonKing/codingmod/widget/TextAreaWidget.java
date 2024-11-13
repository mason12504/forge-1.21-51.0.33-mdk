package net.CarsonKing.codingmod.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput; // Ensure this import is correct
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class TextAreaWidget extends AbstractWidget {
    private final Font font;
    private String text = "";
    private int cursorPosition = 0;
    private boolean isFocused = false;
    private int scrollOffset = 0; // Vertical scroll offset in pixels

    public TextAreaWidget(Font font, int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
        this.font = font;
    }

    public void setFocused(boolean focused) {
        this.isFocused = focused;
    }

    public boolean isFocused() {
        return this.isFocused;
    }

    public void setValue(String value) {
        this.text = value;
        this.cursorPosition = value.length();
        ensureCursorVisible();
    }

    public String getValue() {
        return this.text;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        // Draw background
        guiGraphics.fill(getX(), getY(), getX() + width, getY() + height, 0xFF202020);

        // Enable scissor to clip rendering to the widget's area
        enableScissor(getX(), getY(), getX() + width, getY() + height);

        // Draw text
        String[] lines = text.split("\n", -1);
        int lineHeight = font.lineHeight;
        int startY = getY() + 4 - scrollOffset;

        for (int i = 0; i < lines.length; i++) {
            int lineY = startY + (i * lineHeight);
            if (lineY + lineHeight > getY() && lineY < getY() + height) {
                guiGraphics.drawString(font, lines[i], getX() + 4, lineY, 0xFFFFFF, false);
            }
        }

        // Draw cursor
        if (isFocused) {
            int cursorLine = getCursorLine();
            int cursorColumn = getCursorColumn();
            String cursorLineText = cursorLine < lines.length ? lines[cursorLine] : "";
            int cursorX = getX() + 4 + font.width(cursorLineText.substring(0, Math.min(cursorColumn, cursorLineText.length())));
            int cursorY = startY + cursorLine * lineHeight;

            if (cursorY + lineHeight > getY() && cursorY < getY() + height) {
                guiGraphics.fill(cursorX, cursorY, cursorX + 1, cursorY + lineHeight, 0xFFFFFFFF);
            }
        }

        // Disable scissor
        disableScissor();
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (!isFocused) {
            return false;
        }
        insertText(Character.toString(chr));
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!isFocused) {
            return false;
        }
        switch (keyCode) {
            case GLFW.GLFW_KEY_BACKSPACE -> {
                deleteText(-1);
                return true;
            }
            case GLFW.GLFW_KEY_DELETE -> {
                deleteText(1);
                return true;
            }
            case GLFW.GLFW_KEY_LEFT -> {
                moveCursor(-1);
                return true;
            }
            case GLFW.GLFW_KEY_RIGHT -> {
                moveCursor(1);
                return true;
            }
            case GLFW.GLFW_KEY_UP -> {
                moveCursorLine(-1);
                return true;
            }
            case GLFW.GLFW_KEY_DOWN -> {
                moveCursorLine(1);
                return true;
            }
            case GLFW.GLFW_KEY_PAGE_UP -> {
                scrollOffset = Math.max(0, scrollOffset - height);
                return true;
            }
            case GLFW.GLFW_KEY_PAGE_DOWN -> {
                scrollOffset = Math.min(getMaxScroll(), scrollOffset + height);
                return true;
            }
            case GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER -> {
                insertText("\n");
                return true;
            }
            case GLFW.GLFW_KEY_TAB -> {
                insertText("    "); // Insert four spaces for indentation
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (isMouseOver(mouseX, mouseY)) {
            // Adjust scrollOffset based on vertical scroll amount (scrollY)
            scrollOffset -= scrollY * font.lineHeight;
            scrollOffset = Math.max(0, Math.min(scrollOffset, getMaxScroll()));
            return true;
        }
        return false;
    }



    private void insertText(String input) {
        this.text = this.text.substring(0, cursorPosition) + input + this.text.substring(cursorPosition);
        cursorPosition += input.length();
        ensureCursorVisible();
    }

    private void deleteText(int direction) {
        if (direction == -1 && cursorPosition > 0) {
            this.text = this.text.substring(0, cursorPosition - 1) + this.text.substring(cursorPosition);
            cursorPosition--;
            ensureCursorVisible();
        } else if (direction == 1 && cursorPosition < text.length()) {
            this.text = this.text.substring(0, cursorPosition) + this.text.substring(cursorPosition + 1);
            ensureCursorVisible();
        }
    }

    private void moveCursor(int offset) {
        cursorPosition = Math.max(0, Math.min(cursorPosition + offset, text.length()));
        ensureCursorVisible();
    }

    private void moveCursorLine(int lineOffset) {
        int[] lineOffsets = getLineOffsets();
        int currentLine = getCursorLine();
        int column = getCursorColumn();
        int targetLine = currentLine + lineOffset;

        if (targetLine >= 0 && targetLine < lineOffsets.length - 1) {
            int lineStart = lineOffsets[targetLine];
            int lineEnd = lineOffsets[targetLine + 1] - 1; // Exclude newline character
            int lineLength = lineEnd - lineStart;

            cursorPosition = lineStart + Math.min(column, lineLength);
            cursorPosition = Math.min(cursorPosition, text.length());
            ensureCursorVisible();
        }
    }

    private void ensureCursorVisible() {
        int lineHeight = font.lineHeight;
        int cursorLine = getCursorLine();
        int cursorPixelY = lineHeight * cursorLine;
        int widgetStartY = scrollOffset;
        int widgetEndY = scrollOffset + height - 8; // Subtract padding

        if (cursorPixelY < widgetStartY) {
            scrollOffset = cursorPixelY;
        } else if (cursorPixelY + lineHeight > widgetEndY) {
            scrollOffset = cursorPixelY + lineHeight - height + 8;
        }
        scrollOffset = Math.max(0, Math.min(scrollOffset, getMaxScroll()));
    }

    private int getMaxScroll() {
        int totalHeight = font.lineHeight * text.split("\n", -1).length;
        return Math.max(0, totalHeight - height + 8); // Add padding
    }

    private int getCursorLine() {
        int line = 0;
        int pos = 0;
        while ((pos = text.indexOf('\n', pos)) != -1 && pos < cursorPosition) {
            line++;
            pos++;
        }
        return line;
    }

    private int getCursorColumn() {
        int lineStart = text.lastIndexOf('\n', cursorPosition - 1);
        return cursorPosition - lineStart - 1;
    }

    private int[] getLineOffsets() {
        String[] lines = text.split("\n", -1);
        int[] offsets = new int[lines.length + 1];
        int pos = 0;
        offsets[0] = 0;
        for (int i = 0; i < lines.length; i++) {
            pos += lines[i].length() + 1; // +1 for the newline character
            offsets[i + 1] = pos;
        }
        return offsets;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOver(mouseX, mouseY)) {
            this.isFocused = true;
            calculateCursorFromMouse(mouseX, mouseY);
            return true;
        } else {
            this.isFocused = false;
            return false;
        }
    }

    private void calculateCursorFromMouse(double mouseX, double mouseY) {
        String[] lines = text.split("\n", -1);
        int lineHeight = font.lineHeight;
        int relativeY = (int) mouseY - getY() - 4 + scrollOffset;
        int lineIndex = relativeY / lineHeight;
        lineIndex = Math.max(0, Math.min(lineIndex, lines.length - 1));

        String lineText = lines[lineIndex];
        int relativeX = (int) mouseX - getX() - 4;
        int cursorColumn = font.plainSubstrByWidth(lineText, relativeX).length();

        int[] lineOffsets = getLineOffsets();
        cursorPosition = lineOffsets[lineIndex] + cursorColumn;
        cursorPosition = Math.min(cursorPosition, text.length());
        ensureCursorVisible();
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= getX() && mouseX < getX() + width &&
                mouseY >= getY() && mouseY < getY() + height;
    }

    // Correct implementation of updateWidgetNarration
    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        // No narration needed for this widget
    }

    private void enableScissor(int x1, int y1, int x2, int y2) {
        double scale = Minecraft.getInstance().getWindow().getGuiScale();
        RenderSystem.enableScissor(
                (int) (x1 * scale),
                (int) (Minecraft.getInstance().getWindow().getHeight() - y2 * scale),
                (int) ((x2 - x1) * scale),
                (int) ((y2 - y1) * scale)
        );
    }

    private void disableScissor() {
        RenderSystem.disableScissor();
    }
}
