package net.CarsonKing.codingmod.screen;

import net.CarsonKing.codingmod.widget.TextAreaWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class TerminalScreen extends Screen {
    private TextAreaWidget codeArea;
    private String outputText = "";
    private final int imageWidth = 256;
    private final int imageHeight = 166;

    public TerminalScreen() {
        super(Component.translatable("screen.codingmod.terminal"));
    }

    @Override
    protected void init() {
        super.init();
        int centerX = (this.width - this.imageWidth) / 2;
        int centerY = (this.height - this.imageHeight) / 2;

        // Initialize the code area for input
        Font font = Minecraft.getInstance().font;
        codeArea = new TextAreaWidget(font, centerX + 10, centerY + 10, this.imageWidth - 20, this.imageHeight - 70);
        codeArea.setMaxLength(5000);
        codeArea.setFocused(true);
        this.addRenderableWidget(codeArea);

        // Add a Run button using builder
        this.addRenderableWidget(
                Button.builder(Component.translatable("button.codingmod.run"), button -> {
                            String code = codeArea.getValue();
                            processCode(code);
                        })
                        .pos(centerX + 10, centerY + this.imageHeight - 50)
                        .size(60, 20)
                        .build()
        );

        // Add a Clear button using builder
        this.addRenderableWidget(
                Button.builder(Component.translatable("button.codingmod.clear"), button -> {
                            codeArea.setValue("");
                        })
                        .pos(centerX + 80, centerY + this.imageHeight - 50)
                        .size(60, 20)
                        .build()
        );
    }

    private void processCode(String code) {
        try {
            // Compile the code
            String className = "UserScript";
            String fullClassName = "net.CarsonKing.codingmod.scripts." + className;
            boolean success = compileJavaCode(className, code);
            if (success) {
                // Execute the code in a separate process
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
        // Prepare source code
        String fullClassName = "net.CarsonKing.codingmod.scripts." + className;
        String sourceCode = "package net.CarsonKing.codingmod.scripts;\n" +
                "public class " + className + " implements Runnable {\n" +
                "    @Override\n" +
                "    public void run() {\n" +
                code + "\n" +
                "    }\n" +
                "}\n";

        // Save source in .java file
        Path sourcePath = Paths.get("scripts/" + className + ".java");
        Files.createDirectories(sourcePath.getParent());
        Files.write(sourcePath, sourceCode.getBytes());

        // Compile source file
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            displayOutput("No Java compiler available.");
            return false;
        }
        int result = compiler.run(null, null, null, sourcePath.toString());
        return result == 0;
    }

    private String executeJavaCodeInSeparateProcess(String className) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("java", "-cp", "scripts/", "net.CarsonKing.codingmod.scripts." + className);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        // Read the output
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        int exitCode = process.waitFor();
        output.append("Process exited with code ").append(exitCode);

        return output.toString();
    }

    private void displayOutput(String output) {
        this.outputText = output;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
        // this.renderBackground(guiGraphics);

        int centerX = (this.width - this.imageWidth) / 2;
        int centerY = (this.height - this.imageHeight) / 2;

        // Render the code area and buttons
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        // Render output text
        int outputY = centerY + this.imageHeight - 100;
        guiGraphics.drawString(this.font, "Output:", centerX + 10, outputY, 0xFFFFFF, false);
        guiGraphics.drawString(this.font, outputText, centerX + 10, outputY + 15, 0xFFFFFF, false);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Allow closing the GUI with the Escape key
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}



