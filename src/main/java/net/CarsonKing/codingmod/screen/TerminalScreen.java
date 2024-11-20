package net.CarsonKing.codingmod.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.CarsonKing.codingmod.widget.TextAreaWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TerminalScreen extends Screen {
    private TextAreaWidget codeArea;
    private String outputText = "";
    private int outputScrollOffset = 0;
    private int outputHorizontalScrollOffset = 0; // New variable for horizontal scroll
    private final int imageWidth = 256;
    private final int imageHeight = 256;

    public TerminalScreen() {
        super(Component.translatable("screen.codingmod.terminal"));
    }

    @Override
    protected void init() {
        super.init();
        int centerX = (width - imageWidth) / 2;
        int centerY = (height - imageHeight) / 2;

        Font font = Minecraft.getInstance().font;
        codeArea = new TextAreaWidget(font, centerX + 10, centerY + 10, imageWidth - 20, imageHeight - 130);
        codeArea.setFocused(true);
        addRenderableWidget(codeArea);

        addRenderableWidget(
                Button.builder(Component.translatable("button.codingmod.run"), button -> {
                            String code = codeArea.getValue();
                            processCode(code);
                        })
                        .pos(centerX + 10, centerY + imageHeight - 110)
                        .size(60, 20)
                        .build()
        );

        addRenderableWidget(
                Button.builder(Component.translatable("button.codingmod.clear"), button -> codeArea.setValue(""))
                        .pos(centerX + 80, centerY + imageHeight - 110)
                        .size(60, 20)
                        .build()
        );

        setFocused(codeArea);
    }

    private void processCode(String code) {
        try {
            String className = "UserScript";
            boolean success = compileJavaCode(className, code);
            if (success) {
                String result = executeJavaCodeInSeparateProcess(className);
                displayOutput(result);
            } else {
                displayOutput("Compilation failed.");
            }
        } catch (Exception e) {
            displayOutput("Error: " + e.getMessage());
        }
    }

    private boolean compileJavaCode(String className, String code) throws IOException {
        String sourceCode = "package net.CarsonKing.codingmod.scripts;\n" +
                "import java.util.Scanner;\n" +
                "import java.util.ArrayList;\n" +
                "import java.util.Arrays;\n" +
                "public class " + className + " {\n" +
                code + "\n" +
                "    public static void main(String[] args) {\n" +
                "        new " + className + "().playerMain();\n" +
                "    }\n" +
                "}\n";

        Path sourcePath = Paths.get("scripts/" + className + ".java");
        Files.createDirectories(sourcePath.getParent());
        Files.write(sourcePath, sourceCode.getBytes());

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            displayOutput("No Java compiler available.");
            return false;
        }

        ByteArrayOutputStream errOut = new ByteArrayOutputStream();
        int result = compiler.run(null, null, new PrintStream(errOut), "-d", "scripts/", sourcePath.toString());

        if (result != 0) {
            displayOutput("Compilation failed:\n" + errOut.toString());
        }

        return result == 0;
    }

    private String executeJavaCodeInSeparateProcess(String className) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("java", "-cp", "scripts/", "net.CarsonKing.codingmod.scripts." + className);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        int exitCode = process.waitFor();

        return output.toString();
    }

    private void displayOutput(String output) {
        outputText = output;
        outputScrollOffset = getMaxOutputScroll();
        outputHorizontalScrollOffset = 0; // Reset horizontal scroll when new output is displayed
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        int centerX = (width - imageWidth) / 2;
        int centerY = (height - imageHeight) / 2;

        int outputX = centerX + 10;
        int outputY = centerY + imageHeight - 90;
        int outputWidth = imageWidth - 20;
        int outputHeight = 80;

        guiGraphics.fill(outputX - 2, outputY - 2, outputX + outputWidth + 2, outputY + outputHeight + 2, 0xFF000000);

        enableScissor(outputX, outputY, outputX + outputWidth, outputY + outputHeight);

        guiGraphics.drawString(font, "Output:", outputX, outputY - 15, 0xFFFFFF, false);

        String[] outputLines = outputText.split("\n");
        int lineHeight = font.lineHeight;
        int startY = outputY - outputScrollOffset;

        for (String line : outputLines) {
            int lineY = startY + (lineHeight);
            if (lineY + lineHeight > outputY && lineY < outputY + outputHeight) {
                // Apply horizontal scroll offset
                guiGraphics.drawString(font, line, outputX - outputHorizontalScrollOffset, lineY, 0xFFFFFF, false);
            }
            startY += lineHeight;
        }

        disableScissor();
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (codeArea.isFocused() && codeArea.charTyped(chr, modifiers)) {
            return true;
        }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (codeArea.isFocused() && codeArea.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }

        // Handle left and right arrow keys for output scrolling
        if (keyCode == GLFW.GLFW_KEY_LEFT) {
            outputHorizontalScrollOffset = Math.max(0, outputHorizontalScrollOffset - 10); // Scroll left by 10 pixels
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_RIGHT) {
            outputHorizontalScrollOffset = Math.min(getMaxOutputHorizontalScroll(), outputHorizontalScrollOffset + 10); // Scroll right by 10 pixels
            return true;
        }

        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (isMouseOverOutput(mouseX, mouseY)) {
            // Vertical scrolling
            outputScrollOffset -= scrollY * font.lineHeight;
            outputScrollOffset = Math.max(0, Math.min(outputScrollOffset, getMaxOutputScroll()));

            return true;
        } else if (codeArea.isMouseOver(mouseX, mouseY)) {
            return codeArea.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    private boolean isMouseOverOutput(double mouseX, double mouseY) {
        int outputX = (width - imageWidth) / 2 + 10;
        int outputY = (height - imageHeight) / 2 + imageHeight - 90;
        int outputWidth = imageWidth - 20;
        int outputHeight = 80;

        return mouseX >= outputX && mouseX < outputX + outputWidth &&
                mouseY >= outputY && mouseY < outputY + outputHeight;
    }

    private int getMaxOutputScroll() {
        int totalHeight = font.lineHeight * outputText.split("\n").length;
        int outputHeight = 80;
        return Math.max(0, totalHeight - outputHeight);
    }

    private int getMaxOutputHorizontalScroll() {
        int maxWidth = 0;
        for (String line : outputText.split("\n")) {
            int lineWidth = font.width(line);
            if (lineWidth > maxWidth) {
                maxWidth = lineWidth;
            }
        }
        return Math.max(0, maxWidth - (imageWidth - 20));
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
