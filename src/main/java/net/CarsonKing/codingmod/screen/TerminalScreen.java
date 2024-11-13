package net.CarsonKing.codingmod.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.CarsonKing.codingmod.widget.TextAreaWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.AbstractWidget;
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
    private final int imageWidth = 256;
    private final int imageHeight = 256; // Increased height for more room

    public TerminalScreen() {
        super(Component.translatable("screen.codingmod.terminal"));
    }

    @Override
    protected void init() {
        super.init();
        int centerX = (width - imageWidth) / 2;
        int centerY = (height - imageHeight) / 2;

        // Initialize the code area with increased height
        Font font = Minecraft.getInstance().font;
        codeArea = new TextAreaWidget(font, centerX + 10, centerY + 10, imageWidth - 20, imageHeight - 130);
        codeArea.setFocused(true);
        addRenderableWidget(codeArea);

        // Add Run button
        addRenderableWidget(
                Button.builder(Component.translatable("button.codingmod.run"), button -> {
                            String code = codeArea.getValue();
                            processCode(code);
                        })
                        .pos(centerX + 10, centerY + imageHeight - 110)
                        .size(60, 20)
                        .build()
        );

        // Add Clear button
        addRenderableWidget(
                Button.builder(Component.translatable("button.codingmod.clear"), button -> codeArea.setValue(""))
                        .pos(centerX + 80, centerY + imageHeight - 110)
                        .size(60, 20)
                        .build()
        );

        // Set focus to codeArea
        setFocused(codeArea);
    }

    private void processCode(String code) {
        try {
            // Compile the code
            String className = "UserScript";
            boolean success = compileJavaCode(className, code);
            if (success) {
                // Execute the code
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
                "public class " + className + " implements Runnable {\n" +
                "    @Override\n" +
                "    public void run() {\n" +
                code + "\n" +
                "    }\n" +
                "    public static void main(String[] args) {\n" +
                "        new " + className + "().run();\n" +
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
        // Auto-scroll to the bottom
        outputScrollOffset = getMaxOutputScroll();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTicks);

        // Render the code area and buttons
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        int centerX = (width - imageWidth) / 2;
        int centerY = (height - imageHeight) / 2;

        // Output area dimensions
        int outputX = centerX + 10;
        int outputY = centerY + imageHeight - 90;
        int outputWidth = imageWidth - 20;
        int outputHeight = 80;

        // Draw output background
        guiGraphics.fill(outputX - 2, outputY - 2, outputX + outputWidth + 2, outputY + outputHeight + 2, 0xFF000000);

        // Enable scissor to clip rendering to the output area
        enableScissor(outputX, outputY, outputX + outputWidth, outputY + outputHeight);

        // Draw output text
        guiGraphics.drawString(font, "Output:", outputX, outputY - 15, 0xFFFFFF, false);

        String[] outputLines = outputText.split("\n");
        int lineHeight = font.lineHeight;
        int startY = outputY - outputScrollOffset;

        for (int i = 0; i < outputLines.length; i++) {
            int lineY = startY + (i * lineHeight);
            if (lineY + lineHeight > outputY && lineY < outputY + outputHeight) {
                guiGraphics.drawString(font, outputLines[i], outputX, lineY, 0xFFFFFF, false);
            }
        }

        // Disable scissor
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
        // Close the GUI with Escape key
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (isMouseOverOutput(mouseX, mouseY)) {
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
        int outputHeight = 80; // Same as in render method
        return Math.max(0, totalHeight - outputHeight);
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






