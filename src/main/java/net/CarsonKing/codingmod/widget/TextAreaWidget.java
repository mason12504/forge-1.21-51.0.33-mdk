package net.CarsonKing.codingmod.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class TextAreaWidget extends AbstractWidget {
    private final Font font;
    private String text = "";
    private int cursorPosition = 0;
    private boolean isFocused = false;
    private int scrollOffset = 0; // Vertical scroll offset in pixels
    private int horizontalScrollOffset = 0; // Horizontal scroll offset in pixels

    public TextAreaWidget(Font font, int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
        this.font = font;
    }

    /**
     * Retrieves the current text content of the TextAreaWidget.
     *
     * @return The text currently in the widget.
     */
    public String getValue() {
        return this.text;
    }

    /**
     * Sets the text content of the TextAreaWidget.
     *
     * @param value The new text to display in the widget.
     */
    public void setValue(String value) {
        this.text = value != null ? value : "";
        this.cursorPosition = Math.min(this.cursorPosition, this.text.length());
        this.scrollOffset = 0;
        this.horizontalScrollOffset = 0;
        // Optionally, trigger a re-render or any other necessary updates here.
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        // Draw background
        guiGraphics.fill(getX(), getY(), getX() + width, getY() + height, 0xFF202020);

        // Enable scissor to clip rendering to the widget's area
        enableScissor(getX(), getY(), getX() + width, getY() + height);

        // Draw text with horizontal scrolling support
        String[] lines = text.split("\n", -1);
        int lineHeight = font.lineHeight;
        int startY = getY() + 4 - scrollOffset;

        for (int i = 0; i < lines.length; i++) {
            int lineY = startY + (i * lineHeight);
            if (lineY + lineHeight > getY() && lineY < getY() + height) {
                String lineText = lines[i];

                // Determine the starting character index based on horizontalScrollOffset (pixel-based)
                int startCharIndex = getCharacterIndexForPixel(lineText, horizontalScrollOffset);

                // Get the visible substring starting from startCharIndex within the available width
                String visibleText = font.plainSubstrByWidth(lineText.substring(startCharIndex), width - 8);

                guiGraphics.drawString(font, visibleText, getX() + 4, lineY, 0xFFFFFF, false);
            }
        }

        // Draw cursor
        if (isFocused) {
            int cursorLine = getCursorLine();
            int cursorColumn = getCursorColumn();
            String cursorLineText = cursorLine < lines.length ? lines[cursorLine] : "";

            // Find the starting character index based on horizontalScrollOffset (pixel-based)
            int startCharIndex = getCharacterIndexForPixel(cursorLineText, horizontalScrollOffset);

            // Calculate the width from startCharIndex to cursorColumn
            String substringForWidth = "";
            if (cursorColumn > startCharIndex && cursorColumn <= cursorLineText.length()) {
                substringForWidth = cursorLineText.substring(startCharIndex, cursorColumn);
            }

            int cursorVisibleWidth = font.width(substringForWidth);

            int cursorX = getX() + 4 + cursorVisibleWidth;
            int cursorY = startY + cursorLine * lineHeight;

            if (cursorY + lineHeight > getY() && cursorY < getY() + height &&
                    cursorX >= getX() + 4 && cursorX < getX() + width - 4) {
                guiGraphics.fill(cursorX, cursorY, cursorX + 1, cursorY + lineHeight, 0xFFFFFFFF);
            }

            // Ensure cursor is visible
            ensureCursorVisible(cursorVisibleWidth);
        }

        // Disable scissor after rendering
        disableScissor();
    }

    // Type only if terminal is focused
    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (!isFocused) {
            return false;
        }
        insertText(Character.toString(chr));
        return true;
    }

    // Terminal navigation keys
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
                scrollOffset = Math.min(getMaxVerticalScroll(), scrollOffset + height);
                return true;
            }
            case GLFW.GLFW_KEY_HOME -> {
                moveCursorToLineStart();
                return true;
            }
            case GLFW.GLFW_KEY_END -> {
                moveCursorToLineEnd();
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
            // Vertical scrolling
            scrollOffset -= scrollY * font.lineHeight;
            scrollOffset = Math.max(0, Math.min(scrollOffset, getMaxVerticalScroll()));

            // Horizontal scrolling
            horizontalScrollOffset += scrollX * font.width(" ");
            horizontalScrollOffset = Math.max(0, Math.min(horizontalScrollOffset, getMaxHorizontalScroll()));
            return true;
        }
        return false;
    }

    private void insertText(String input) {
        this.text = this.text.substring(0, cursorPosition) + input + this.text.substring(cursorPosition);
        cursorPosition += input.length();
        ensureCursorVisible(font.width(input));
    }

    private void deleteText(int direction) {
        if (direction == -1 && cursorPosition > 0) {
            this.text = this.text.substring(0, cursorPosition - 1) + this.text.substring(cursorPosition);
            cursorPosition--;
            ensureCursorVisible(-font.width(" "));
        } else if (direction == 1 && cursorPosition < text.length()) {
            this.text = this.text.substring(0, cursorPosition) + this.text.substring(cursorPosition + 1);
            ensureCursorVisible(font.width(" "));
        }
    }

    private void moveCursor(int offset) {
        cursorPosition = Math.max(0, Math.min(cursorPosition + offset, text.length()));
        ensureCursorVisible(0);
    }

    // Move cursor between lines
    private void moveCursorLine(int lineOffset) {
        int[] lineOffsets = getLineOffsets();
        int currentLine = getCursorLine();
        int column = getCursorColumn();
        int targetLine = currentLine + lineOffset;

        // determine line start and end
        if (targetLine >= 0 && targetLine < lineOffsets.length - 1) {
            int lineStart = lineOffsets[targetLine];
            int lineEnd = lineOffsets[targetLine + 1] - 1; // Exclude newline character
            int lineLength = lineEnd - lineStart;

            cursorPosition = lineStart + Math.min(column, lineLength);
            cursorPosition = Math.min(cursorPosition, text.length());
            ensureCursorVisible(0);
        }
    }

    private void moveCursorToLineStart() {
        int lineStart = text.lastIndexOf('\n', cursorPosition - 1);
        cursorPosition = lineStart + 1;
        ensureCursorVisible(0);
    }

    private void moveCursorToLineEnd() {
        int lineEnd = text.indexOf('\n', cursorPosition);
        if (lineEnd == -1) {
            cursorPosition = text.length();
        } else {
            cursorPosition = lineEnd;
        }
        ensureCursorVisible(0);
    }

    // Scroll the terminal with the cursor
    private void ensureCursorVisible(int cursorVisibleWidth) {
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

        // Horizontal scrolling
        String cursorLineText = getLineText(cursorLine);
        int cursorTextWidth = font.width(cursorLineText.substring(0, getCursorColumn()));
        int widgetStartX = horizontalScrollOffset;
        int widgetEndX = horizontalScrollOffset + width - 8; // Subtract padding

        if (cursorTextWidth < widgetStartX) {
            horizontalScrollOffset = cursorTextWidth - 4; // Add a small buffer
            if (horizontalScrollOffset < 0) {
                horizontalScrollOffset = 0;
            }
        } else if (cursorTextWidth > widgetEndX) {
            horizontalScrollOffset = cursorTextWidth - width + 12; // Add a small buffer
        }

        scrollOffset = Math.max(0, Math.min(scrollOffset, getMaxVerticalScroll()));
        horizontalScrollOffset = Math.max(0, Math.min(horizontalScrollOffset, getMaxHorizontalScroll()));
    }

    private int getMaxVerticalScroll() {
        int totalHeight = font.lineHeight * text.split("\n", -1).length;
        return Math.max(0, totalHeight - height + 8); // Add padding
    }

    private int getMaxHorizontalScroll() {
        int maxLineWidth = 0;
        String[] lines = text.split("\n", -1);
        for (String line : lines) {
            int lineWidth = font.width(line);
            if (lineWidth > maxLineWidth) {
                maxLineWidth = lineWidth;
            }
        }
        return Math.max(0, maxLineWidth - width + 8); // Add padding
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

    private String getLineText(int lineIndex) {
        String[] lines = text.split("\n", -1);
        if (lineIndex >= 0 && lineIndex < lines.length) {
            return lines[lineIndex];
        }
        return "";
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

    // Focus on terminal
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

    // Determine where to place the cursor based on where was clicked
    private void calculateCursorFromMouse(double mouseX, double mouseY) {
        String[] lines = text.split("\n", -1);
        int lineHeight = font.lineHeight;
        int relativeY = (int) mouseY - getY() - 4 + scrollOffset;
        int lineIndex = relativeY / lineHeight;
        lineIndex = Math.max(0, Math.min(lineIndex, lines.length - 1));

        String lineText = lines[lineIndex];
        int relativeX = (int) mouseX - getX() - 4 + horizontalScrollOffset;
        int cursorColumn = getCharacterIndexForPixel(lineText, relativeX);

        int[] lineOffsets = getLineOffsets();
        cursorPosition = lineOffsets[lineIndex] + Math.min(cursorColumn, lineText.length());
        cursorPosition = Math.min(cursorPosition, text.length());
        ensureCursorVisible(0); // Pass 0 since we're handling the adjustment within the method
    }

    /**
     * Determines the character index in the line where the text should start based on the pixel offset.
     *
     * @param lineText    The text of the current line.
     * @param pixelOffset The current horizontal scroll offset in pixels.
     * @return The character index to start rendering from.
     */
    private int getCharacterIndexForPixel(String lineText, int pixelOffset) {
        int width = 0;
        int index = 0;
        while (index < lineText.length()) {
            char c = lineText.charAt(index);
            int charWidth = font.width(Character.toString(c));
            if (width + charWidth > pixelOffset) {
                break;
            }
            width += charWidth;
            index++;
        }
        return index;
    }

    // Check if mouse is currently over the terminal.
    @Override
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
