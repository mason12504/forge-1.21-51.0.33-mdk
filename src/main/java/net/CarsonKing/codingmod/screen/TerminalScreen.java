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

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TerminalScreen extends Screen {
    private TextAreaWidget codeArea;
    private String outputText = "";
    private int outputScrollOffset = 0;
    private int outputHorizontalScrollOffset = 0; // Variable for horizontal scroll
    private final int imageWidth = 256;
    private final int imageHeight = 256;

    // ExecutorService to handle asynchronous test execution
    private final ExecutorService testExecutor = Executors.newSingleThreadExecutor();

    // Inner class to represent a test case
    private static class TestCase {
        String description;
        String code;
        String expectedOutput;

        TestCase(String description, String code, String expectedOutput) {
            this.description = description;
            this.code = code;
            this.expectedOutput = expectedOutput;
        }
    }

    // List of predefined test cases
    private final List<TestCase> testCases = Arrays.asList(
            // Test 1: Successful Compilation and Execution
            new TestCase(
                    "Test 1: Successful Compilation and Execution",
                    "public void playerMain() {\n" +
                            "    System.out.println(\"Hello, World!\");\n" +
                            "}",
                    "Hello, World!\n"
            ),

            // Test 2: Compilation Error (Missing Semicolon)
            new TestCase(
                    "Test 2: Compilation Error (Missing Semicolon)",
                    "public void playerMain() {\n" +
                            "    System.out.println(\"Missing semicolon\")\n" + // Missing semicolon
                            "}",
                    "Compilation failed:\n" +
                            "Error on line 3: ';' expected\n"
            ),

            // Test 3: Runtime Error (NullPointerException)
            new TestCase(
                    "Test 3: Runtime Error (NullPointerException)",
                    "public void playerMain() {\n" +
                            "    String s = null;\n" +
                            "    System.out.println(s.length());\n" + // This will cause NullPointerException
                            "}",
                    "java.lang.NullPointerException"
            ),

            // Test 4: Testing Loops
            new TestCase(
                    "Test 4: Testing Loops",
                    "public void playerMain() {\n" +
                            "    for (int i = 0; i < 7; i++) {\n" +
                            "        System.out.println(i);\n" +
                            "    }\n" +
                            "}",
                    "0\n1\n2\n3\n4\n5\n6\n"
            ),

            // Test 5: Testing Arithmetic Operations and Numeric Variable Assignment
            new TestCase(
                    "Test 5: Testing Arithmetic Operations and Numeric Variable Assignment",
                    "public void playerMain() {\n" +
                            "    int testVariable1 = 10;\n" +
                            "    float testVariable2 = 15.5f;\n" + // Added 'f' suffix for float literal
                            "    System.out.println(testVariable1 + testVariable2);\n" +
                            "    System.out.println(testVariable1 - testVariable2);\n" +
                            "    System.out.println(testVariable1 * testVariable2);\n" +
                            "    System.out.println(testVariable1 / testVariable2);\n" +
                            "    System.out.println(testVariable1 % testVariable2);\n" +
                            "}",
                    "25.5\n-5.5\n155.0\n0.6451613\n10.0\n"
            ),

            // Test 6: Testing Arrays
            new TestCase(
                    "Test 6: Testing Arrays",
                    "public void playerMain() {\n" +
                            "    int[] numbers = {10, 20, 30, 40, 50};\n" +
                            "    for (int number : numbers) {\n" +
                            "        System.out.println(number);\n" +
                            "    }\n" +
                            "}",
                    "10\n20\n30\n40\n50\n"
            ),

            // Test 7: Testing a While Loop
            new TestCase(
                    "Test 7: Testing a While Loop",
                    "public void playerMain() {\n" +
                            "    int count = 5;\n" +
                            "    while (count > 0) {\n" +
                            "        System.out.println(count);\n" +
                            "        count--;\n" +
                            "    }\n" +
                            "}",
                    "5\n4\n3\n2\n1\n"
            ),

            // Test 8: Creating a Method and Calling It from playerMain
            new TestCase(
                    "Test 8: Creating a Method and Calling It from playerMain",
                    "public void playerMain() {\n" +
                            "    greet(\"Minecraft User\");\n" +
                            "}\n\n" +
                            "public void greet(String name) {\n" +
                            "    System.out.println(\"Hello, \" + name + \"!\");\n" +
                            "}",
                    "Hello, Minecraft User!\n"
            )
    );

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

        // "Run" Button
        addRenderableWidget(
                Button.builder(Component.translatable("button.codingmod.run"), button -> {
                            String code = codeArea.getValue();
                            processCode(code);
                        })
                        .pos(centerX + 10, centerY + imageHeight - 110)
                        .size(60, 20)
                        .build()
        );

        // "Clear" Button
        addRenderableWidget(
                Button.builder(Component.translatable("button.codingmod.clear"), button -> codeArea.setValue(""))
                        .pos(centerX + 80, centerY + imageHeight - 110)
                        .size(60, 20)
                        .build()
        );

        // "Test" Button
        addRenderableWidget(
                Button.builder(Component.translatable("TEST CASES"), button -> {
                            runTests();
                        })
                        .pos(centerX + 150, centerY + imageHeight - 110)
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
            }
            // No else block needed as `compileJavaCode` already displays errors
        } catch (Exception e) {
            displayOutput("Error: " + e.getMessage());
        }
    }

    private void runTests() {
        testExecutor.submit(() -> {
            StringBuilder testResults = new StringBuilder("Running Tests...\n\n");

            for (int i = 0; i < testCases.size(); i++) {
                TestCase testCase = testCases.get(i);
                int testNumber = i + 1;

                // Update the codeArea with the test case's code on the main thread
                Minecraft.getInstance().execute(() -> codeArea.setValue(testCase.code));

                // Wait for the GUI to update and for the user to see the code
                try {
                    Thread.sleep(1000); // 1-second delay
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    testResults.append("❌ Test interrupted.\n\n");
                    continue;
                }

                testResults.append("Test ").append(testNumber).append(": ").append(testCase.description).append("\n\n");

                try {
                    String className = "UserScript";
                    boolean success = compileJavaCode(className, testCase.code);
                    String actualOutput = "";

                    if (success) {
                        actualOutput = executeJavaCodeInSeparateProcess(className);
                    } else {
                        actualOutput = outputText; // Capture compilation errors
                    }

                    // Determine Pass/Fail Status
                    boolean isPass = false;
                    if (testCase.description.contains("Compilation Error")) {
                        // For compilation error tests, check if actual output contains expected error message
                        isPass = actualOutput.contains(testCase.expectedOutput.trim());
                    } else if (testCase.description.contains("Runtime Error")) {
                        // For runtime error tests, check if actual output contains expected exception
                        isPass = actualOutput.contains(testCase.expectedOutput.trim());
                    } else {
                        // For other tests, compare actual output with expected output
                        isPass = actualOutput.equals(testCase.expectedOutput);
                    }

                    // Append Expected and Actual Outputs
                    testResults.append("**Expected Output:**\n");
                    testResults.append(testCase.expectedOutput).append("\n");
                    testResults.append("**Actual Output:**\n");
                    testResults.append(actualOutput).append("\n");

                    // Append Pass/Fail Status
                    if (isPass) {
                        testResults.append("**Status:** ✅ Passed\n");
                    } else {
                        testResults.append("**Status:** ❌ Failed\n");
                    }

                } catch (Exception e) {
                    testResults.append("❌ Failed\n");
                    testResults.append("**Exception occurred:** ").append(e.getMessage()).append("\n");
                }

                testResults.append("\n---\n\n"); // Separator between test cases
            }

            // Append summary
            int passed = countOccurrences(testResults.toString(), "✅ Passed");
            int failed = countOccurrences(testResults.toString(), "❌ Failed");
            testResults.append("**Test Summary:**\n");
            testResults.append("✅ Passed: ").append(passed).append("\n");
            testResults.append("❌ Failed: ").append(failed).append("\n");

            // Display the test results on the main thread
            Minecraft.getInstance().execute(() -> displayOutput(testResults.toString()));
        });
    }

    // Utility method to count occurrences of a substring
    private int countOccurrences(String str, String subStr) {
        int count = 0;
        int idx = 0;
        while ((idx = str.indexOf(subStr, idx)) != -1) {
            count++;
            idx += subStr.length();
        }
        return count;
    }

    private boolean compileJavaCode(String className, String code) throws IOException {
        String packageDeclaration = "package net.CarsonKing.codingmod.scripts;\n";
        String sourceCode = packageDeclaration +
                "import java.util.Scanner;\n" +
                "import java.util.ArrayList;\n" +
                "import java.util.Arrays;\n" +
                "public class " + className + " {\n" +
                code + "\n" + // User's code is inserted here
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

        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(
                Arrays.asList(sourcePath.toFile())
        );

        JavaCompiler.CompilationTask task = compiler.getTask(
                null, // default Writer for additional output
                fileManager,
                diagnostics,
                Arrays.asList("-d", "scripts/"), // compilation options
                null,
                compilationUnits
        );

        boolean success = task.call();
        fileManager.close();

        if (!success) {
            StringBuilder errorMessage = new StringBuilder("Compilation failed:\n");
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                if (diagnostic.getKind() == Diagnostic.Kind.ERROR) {
                    long lineNumber = diagnostic.getLineNumber();
                    String message = diagnostic.getMessage(null);
                    errorMessage.append("Error on line ")
                            .append(lineNumber - 4)
                            .append(": ")
                            .append(message)
                            .append("\n");
                }
            }
            displayOutput(errorMessage.toString());
        }

        return success;
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

        // If there was an error during execution, it would be part of the output
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
            int lineY = startY + lineHeight;
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

    @Override
    public void onClose() {
        super.onClose();
        // Shutdown the test executor to free resources
        testExecutor.shutdownNow();
    }
}
