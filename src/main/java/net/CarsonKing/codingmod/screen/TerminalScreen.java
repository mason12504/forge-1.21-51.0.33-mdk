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
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TerminalScreen extends Screen {
    private TextAreaWidget codeArea;
    private String outputText = "";
    private int outputScrollOffset = 0;
    private int outputHorizontalScrollOffset = 0;
    private final int imageWidth = 256;
    private final int imageHeight = 256;
    private ConcurrentLinkedQueue<String> inputQueue = new ConcurrentLinkedQueue<>();
    private TextAreaWidget inputArea;
    private Process currentProcess;
    private Thread inputThread;
    private Thread outputThread;



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

        // Input area dimensions
        int inputX = centerX + 10;
        int inputY = centerY + imageHeight - 30;
        int inputWidth = imageWidth - 20;
        int inputHeight = 20;

        // Initialize the input area
        inputArea = new TextAreaWidget(font, inputX, inputY, inputWidth, inputHeight);
        addRenderableWidget(inputArea);
    }

    private void processCode(String code) {
        try {
            // Clear previous output and input
            outputText = "";
            outputScrollOffset = 0;
            outputHorizontalScrollOffset = 0;
            inputQueue.clear();

            String className = "UserScript";
            boolean success = compileJavaCode(className, code);
            if (success) {
                String result = executeJavaCodeInSeparateProcess(className);
                // No need to call displayOutput here
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
                "        try {\n" +
                "            new " + className + "().playerMain();\n" +
                "        } catch (Exception e) {\n" +
                "            e.printStackTrace();\n" +
                "        }\n" +
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
        // Terminate any existing process and threads
        terminateProcess();

        ProcessBuilder pb = new ProcessBuilder("java", "-cp", "scripts/", "net.CarsonKing.codingmod.scripts." + className);
        pb.redirectErrorStream(true);
        currentProcess = pb.start();

        // Create threads to handle input and output
        inputThread = new Thread(() -> handleProcessInput(currentProcess));
        outputThread = new Thread(() -> handleProcessOutput(currentProcess));

        inputThread.start();
        outputThread.start();

        int exitCode;
        if (currentProcess.waitFor(30, TimeUnit.SECONDS)) {
            // Process finished within timeout
            exitCode = currentProcess.exitValue();
        } else {
            // Process timed out
            terminateProcess();
            displayOutput("Process timed out.");
            exitCode = -1;
        }

        // Wait for threads to finish
        inputThread.join();
        outputThread.join();

        displayOutput("Process exited with code " + exitCode);

        // Reset currentProcess and threads
        currentProcess = null;
        inputThread = null;
        outputThread = null;

        return outputText;
    }

    private void terminateProcess() {
        if (currentProcess != null && currentProcess.isAlive()) {
            currentProcess.destroy();
        }
        if (inputThread != null && inputThread.isAlive()) {
            inputThread.interrupt();
        }
        if (outputThread != null && outputThread.isAlive()) {
            outputThread.interrupt();
        }
    }


    private void handleProcessInput(Process process) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
            while (process.isAlive() && !Thread.currentThread().isInterrupted()) {
                if (!inputQueue.isEmpty()) {
                    String input = inputQueue.poll();
                    writer.write(input);
                    writer.newLine();
                    writer.flush();
                } else {
                    Thread.sleep(100); // Adjust as needed
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            displayOutput("Error in handleProcessInput: " + e.getMessage());
        } catch (InterruptedException e) {
            // Thread was interrupted, exit gracefully
        }
    }

    private void handleProcessOutput(Process process) {
        try (InputStream is = process.getInputStream()) {
            int ch;
            while ((ch = is.read()) != -1 && !Thread.currentThread().isInterrupted()) {
                displayOutput(Character.toString((char) ch));
            }
        } catch (IOException e) {
            e.printStackTrace();
            displayOutput("Error in handleProcessOutput: " + e.getMessage());
        }
    }



    private synchronized void displayOutput(String outputText) {
        this.outputText += outputText;
        outputScrollOffset = getMaxOutputScroll();
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

        for (int i = 0; i < outputLines.length; i++) {
            int lineY = startY + (i * lineHeight);
            if (lineY + lineHeight > outputY && lineY < outputY + outputHeight) {
                guiGraphics.drawString(font, outputLines[i], outputX, lineY, 0xFFFFFF, false);
            }
        }

        disableScissor();

        // Draw input area label
        guiGraphics.drawString(font, "Input:", inputArea.getX(), inputArea.getY() - 15, 0xFFFFFF, false);
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

        if (inputArea.isFocused() && keyCode == GLFW.GLFW_KEY_ENTER) {
            String userInput = inputArea.getValue();
            inputArea.setValue(""); // Clear input area
            inputQueue.add(userInput);
            displayOutput("> " + userInput); // Echo input to output area
            return true;
        }

        if (codeArea.isFocused() && codeArea.keyPressed(keyCode, scanCode, modifiers)) {
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







