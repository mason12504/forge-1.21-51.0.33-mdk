package net.CarsonKing.codingmod.screen;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import com.mojang.blaze3d.systems.RenderSystem;
import net.CarsonKing.codingmod.ModItems.ModItems;
import net.CarsonKing.codingmod.network.AwardItemC2SPacket;
import net.CarsonKing.codingmod.network.ModMessages;
import net.CarsonKing.codingmod.widget.TextAreaWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.commands.Commands;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import org.lwjgl.glfw.GLFW;
import net.minecraft.nbt.CompoundTag;
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

// Import necessary classes for item handling
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;

public class TerminalScreen extends Screen {
    private TextAreaWidget codeArea;
    private String outputText = "";
    private int outputScrollOffset = 0;
    private int outputHorizontalScrollOffset = 0; // Variable for horizontal scroll
    private final int imageWidth = 256;
    private final int imageHeight = 256;
    private long nextProblemTime = 0; // Variable to determine when the next problem starts

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

    // Inner class to represent a coding problem
    private static class CodingProblem {
        String description;
        String codeSnippet;
        String expectedOutput;

        CodingProblem(String description, String codeSnippet, String expectedOutput) {
            this.description = description;
            this.codeSnippet = codeSnippet;
            this.expectedOutput = expectedOutput;
        }
    }

    // List of predefined test cases
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

    // List of coding problems (11 problems)
    private final List<CodingProblem> codingProblems = Arrays.asList(
            // Problem 1
            new CodingProblem(
                    "1. Write a program that prints “Hello World!”.",
                    "public void playerMain() {\n" +
                            "    System.out.println(\"Hello World!\");\n" +
                            "}",
                    "Hello World!\n"
            ),
            // Problem 2
            new CodingProblem(
                    "2. Write a program that prints the circumference of a circle with diameter 5. Reminder: Circumference = 3.14 * 5",
                    "public void playerMain() {\n" +
                            "    double radius = 5;\n" +
                            "    double circumference = 3.14 * radius;\n" +
                            "    System.out.println(circumference);\n" +
                            "}",
                    "15.700000000000001\n"
            ),
            // Problem 3
            new CodingProblem(
                    "3. Create an integer variable with a value of 5. Print that value, and then increment the variable by 1. You can do this however you want, but when you have done so, print the variable again. Your program should print two total values when it is run.",
                    "public void playerMain() {\n" +
                            "    int number = 5;\n" +
                            "    System.out.println(number);\n" +
                            "    number++;\n" +
                            "    System.out.println(number);\n" +
                            "}",
                    "5\n6\n"
            ),
            // Problem 4
            new CodingProblem(
                    "4. Create a string array containing only one entry: “My first string”. Once you have created this array, go ahead and print it.",
                    "public void playerMain() {\n" +
                            "    String[] strings = {\"My first string\"};\n" +
                            "    System.out.println(strings[0]);\n" +
                            "}",
                    "My first string\n"
            ),
            // Problem 5
            new CodingProblem(
                    "5. Create an empty string ArrayList, and then print its size. Once you’ve done that, use .add to place “Steve” inside the ArrayList, and then print his name!",
                    "import java.util.ArrayList;\n" +
                            "public void playerMain() {\n" +
                            "    ArrayList<String> names = new ArrayList<>();\n" +
                            "    System.out.println(names.size());\n" +
                            "    names.add(\"Steve\");\n" +
                            "    System.out.println(names.get(0));\n" +
                            "}",
                    "0\nSteve\n"
            ),
            // Problem 6
            new CodingProblem(
                    "6. Write a program that does the following: Begin by creating an integer variable with a value of 5. Add 7 to it, subtract 9, multiply it by 10, and then divide it by 6. Finally, calculate the remainder of the variable when dividing it by 5.",
                    "public void playerMain() {\n" +
                            "    int a = 5;\n" +
                            "    a += 7;\n" +
                            "    a -= 9;\n" +
                            "    a *= 10;\n" +
                            "    a /= 6;\n" +
                            "    int remainder = a % 5;\n" +
                            "    System.out.println(remainder);\n" +
                            "}",
                    "0\n"
            ),
            // Problem 7
            new CodingProblem(
                    "7. Write a program that generates a true value and a false value, and then prints them out.",
                    "public void playerMain() {\n" +
                            "    boolean valTrue = true;\n" +
                            "    boolean valFalse = false;\n" +
                            "    System.out.println(valTrue);\n" +
                            "    System.out.println(valFalse);\n" +
                            "}",
                    "true\nfalse\n"
            ),
            // Problem 8
            new CodingProblem(
                    "8. Write a program where you have three integer values, each holding the values 2, 5, and 5 respectively. Print the values of these three variables. Then, create an AND statement checking to see if 2==5 and 5==5. Then, create an OR statement checking the same conditions. Print the true or false values of these AND or statements.",
                    "public void playerMain() {\n" +
                            "    int a = 2;\n" +
                            "    int b = 5;\n" +
                            "    int c = 5;\n" +
                            "    System.out.println(a);\n" +
                            "    System.out.println(b);\n" +
                            "    System.out.println(c);\n" +
                            "    boolean andResult = (a == 5) && (b == 5);\n" +
                            "    boolean orResult = (a == 5) || (b == 5);\n" +
                            "    System.out.println(andResult);\n" +
                            "    System.out.println(orResult);\n" +
                            "}",
                    "2\n5\n5\nfalse\ntrue\n"
            ),
            // Problem 9
            new CodingProblem(
                    "9. Write a program that has the following: An integer variable, a, which has a value of 7. An if statement, which checks to see if a < 5. An else if statement, which checks to see if a < 10. An else statement, which checks to see if a >= 10.",
                    "public void playerMain() {\n" +
                            "    int a = 7;\n" +
                            "    if(a < 5) {\n" +
                            "        System.out.println(\"a is less than 5\");\n" +
                            "    } else if(a < 10) {\n" +
                            "        System.out.println(\"a is less than 10\");\n" +
                            "    } else {\n" +
                            "        System.out.println(\"a is 10 or greater\");\n" +
                            "    }\n" +
                            "}",
                    "a is less than 10\n"
            ),
            // Problem 10
            new CodingProblem(
                    "10.  Write a program using for loops to iterate through the following array and print each value: \n" +
                            "   String[] colors = {“red”, “green”, “blue”}\n" +
                            " ",
                    "public void playerMain() {\n" +
                            "    String[] colors = {“red”, “green”, “blue”};\n" +
                            "    for (int i = 0; i < colors.length; i++) { \n" +
                            "         System.out.println(colors[i]); \n" +
                            "    }\n" +
                            "}",
                    "red\ngreen\nblue\n"
            ),
            // Problem 11
            new CodingProblem(
                    "11. Write a program using while loops that count from 5 to 10. During each loop, print the number prior to incrementing it.",
                    "public void playerMain() {\n" +
                            "    int number = 5;\n" +
                            "    while(number <= 10) {\n" +
                            "        System.out.println(number);\n" +
                            "        number++;\n" +
                            "    }\n" +
                            "}",
                    "5\n6\n7\n8\n9\n10\n"
            ),
            // Problem 12
            new CodingProblem(
                    "12. Write a program containing your own exponential calculator function. Once you’ve created it, call your function and output the result of 2^10.",
                    "public void playerMain() {\n" +
                            "    int result = exponent(2, 10);\n" +
                            "    System.out.println(result);\n" +
                            "}\n\n" +
                            "public int exponent(int base, int power) {\n" +
                            "    int result = 1;\n" +
                            "    for(int i = 0; i < power; i++) {\n" +
                            "        result *= base;\n" +
                            "    }\n" +
                            "    return result;\n" +
                            "}",
                    "1024\n"
            )
    );

    // State variables for Learning Mode
    private boolean isLearningModeActive = false;
    private int currentProblemIndex = 0;

    public TerminalScreen() {
        super(Component.translatable("screen.codingmod.terminal"));
    }

    // Saves Progress for the problem the player is on
    private void saveProgress() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.player.getPersistentData().putInt("CurrentProblemIndex", currentProblemIndex);
        }
    }

    // Loads Progress for the problem the player is on
    private void loadProgress() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            currentProblemIndex = mc.player.getPersistentData().getInt("CurrentProblemIndex");
        }
    }

    @Override
    protected void init() {
        super.init();
        int centerX = (width - imageWidth) / 2;
        int centerY = (height - imageHeight) / 2;

        // Output Screen Dimensions
        int outputX = centerX + 10;
        int outputY = centerY + imageHeight - 90;
        int outputWidth = imageWidth - 20; // 236 pixels

        // Button Widths
        int runButtonWidth = 50;
        int clearButtonWidth = 50;
        int testCasesButtonWidth = 68;
        int learningModeButtonWidth = 68;

        // Verify total widths sum up to outputWidth
        int totalButtonWidth = runButtonWidth + clearButtonWidth + testCasesButtonWidth + learningModeButtonWidth; // Should be 236

        // Button Positions
        int buttonY = centerY + imageHeight - 110; // Same as before

        int button1X = outputX;
        int button2X = button1X + runButtonWidth;
        int button3X = button2X + clearButtonWidth;
        int button4X = button3X + testCasesButtonWidth;

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
                        .pos(button1X, buttonY)
                        .size(runButtonWidth, 20)
                        .build()
        );

        // "Clear" Button
        addRenderableWidget(
                Button.builder(Component.translatable("button.codingmod.clear"), button -> codeArea.setValue(""))
                        .pos(button2X, buttonY)
                        .size(clearButtonWidth, 20)
                        .build()
        );

        // "TEST CASES" Button
        addRenderableWidget(
                Button.builder(Component.literal("TEST CASES"), button -> {
                            runTests();
                        })
                        .pos(button3X, buttonY)
                        .size(testCasesButtonWidth, 20)
                        .build()
        );

        // "LEARNING MODE" Button
        addRenderableWidget(
                Button.builder(Component.literal("LEARNING MODE"), button -> {
                            enterLearningMode();
                        })
                        .pos(button4X, buttonY)
                        .size(learningModeButtonWidth, 20)
                        .build()
        );

        setFocused(codeArea);
    }

    private void enterLearningMode() {
        if (isLearningModeActive) {
            // Already in Learning Mode
            displayOutput("Already in Learning Mode.\n");
            return;
        }

        isLearningModeActive = true;

        // Load the saved progress
        loadProgress();

        displayCurrentProblem();
    }

    private void displayCurrentProblem() {
        if (currentProblemIndex >= codingProblems.size()) {
            displayOutput("🎉 Congratulations! You have completed all the coding problems.\n");

            // Reset progress and save
            currentProblemIndex = 0;
            saveProgress();

            isLearningModeActive = false;
            return;
        }

        CodingProblem currentProblem = codingProblems.get(currentProblemIndex);
        String problemDescription = "📘 Problem " + (currentProblemIndex + 1) + " of " + codingProblems.size() + ":\n" +
                currentProblem.description + "\n\n" +
                "📝 Please write your code in the terminal and click 'Run' to submit.\n";

        displayOutput(problemDescription);
    }

    private void processCode(String code) {
        try {
            String className = "UserScript";
            boolean success = compileJavaCode(className, code);
            if (success) {
                String result = executeJavaCodeInSeparateProcess(className);
                if (isLearningModeActive) {
                    checkLearningModeOutput(result);
                } else {
                    displayOutput(result);
                }
            }
            // No else block needed as `compileJavaCode` already displays errors
        } catch (Exception e) {
            displayOutput("⚠️ Error: " + e.getMessage() + "\n");
        }
    }

    private void checkLearningModeOutput(String actualOutput) {
        CodingProblem currentProblem = codingProblems.get(currentProblemIndex);
        String expectedOutput = currentProblem.expectedOutput;

        // Trim to avoid issues with trailing spaces or newlines
        boolean isCorrect = actualOutput.trim().equals(expectedOutput.trim());

        // Prepare the result message
        StringBuilder resultMessage = new StringBuilder();

        if (isCorrect) {
            resultMessage.append("✅ Correct Output!\n");
            // Award the player the specific item
            awardItemToPlayer();
            resultMessage.append("🎁 You've been awarded: ")
                    .append(rewardItems[currentProblemIndex].getDescription().getString())
                    .append("!\n\n");
            // Set the time to move to the next problem in 3 seconds
            nextProblemTime = System.currentTimeMillis() + 3000;
            // Do not immediately increment the problem index or display the next problem
        } else {
            resultMessage.append("❌ Incorrect Output.\n");
            resultMessage.append("**Expected Output:**\n");
            resultMessage.append(expectedOutput).append("\n");
            resultMessage.append("**Your Output:**\n");
            resultMessage.append(actualOutput).append("\n");
            resultMessage.append("🔄 Please try again.\n\n");
        }

        displayOutput(resultMessage.toString());
    }

    // rewardItems array
    private static final Item[] rewardItems = {
            ModItems.PROBLEM_1_COMPLETE.get(),
            ModItems.PROBLEM_2_COMPLETE.get(),
            ModItems.PROBLEM_3_COMPLETE.get(),
            ModItems.PROBLEM_4_COMPLETE.get(),
            ModItems.PROBLEM_5_COMPLETE.get(),
            ModItems.PROBLEM_6_COMPLETE.get(),
            ModItems.PROBLEM_7_COMPLETE.get(),
            ModItems.PROBLEM_8_COMPLETE.get(),
            ModItems.PROBLEM_9_COMPLETE.get(),
            ModItems.PROBLEM_10_COMPLETE.get(),
            Items.DIAMOND,
            Items.NETHER_STAR
    };

    private void awardItemToPlayer() {
        if (currentProblemIndex < rewardItems.length) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                String itemID = getItemID(currentProblemIndex);
                if (!itemID.isEmpty()) {
                    String command = "give " + mc.player.getGameProfile().getName() + " " + itemID + " 1";
                    mc.player.connection.sendCommand(command);
                }
            }
        }
    }

    private String getItemID(int index) {
        switch (index) {
            case 0:
                return "codingmod:problem_1_complete";
            case 1:
                return "codingmod:problem_2_complete";
            case 2:
                return "codingmod:problem_3_complete";
            case 3:
                return "codingmod:problem_4_complete";
            case 4:
                return "codingmod:problem_5_complete";
            case 5:
                return "codingmod:problem_6_complete";
            case 6:
                return "codingmod:problem_7_complete";
            case 7:
                return "codingmod:problem_8_complete";
            case 8:
                return "codingmod:problem_9_complete";
            case 9:
                return "codingmod:problem_10_complete";
            case 10:
                return "minecraft:diamond";
            case 11:
                return "minecraft:nether_star";
            default:
                return "";
        }
    }

    private void runTests() {
        testExecutor.submit(() -> {
            StringBuilder testResults = new StringBuilder("🔍 Running Tests...\n\n");

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

                testResults.append("📄 Test ").append(testNumber).append(": ").append(testCase.description).append("\n\n");

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
                        isPass = actualOutput.trim().equals(testCase.expectedOutput.trim());
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
            testResults.append("📊 **Test Summary:**\n");
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
            displayOutput("⚠️ No Java compiler available.");
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
            StringBuilder errorMessage = new StringBuilder("⚠️ Compilation failed:\n");
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                if (diagnostic.getKind() == Diagnostic.Kind.ERROR) {
                    long lineNumber = diagnostic.getLineNumber();
                    String message = diagnostic.getMessage(null);
                    errorMessage.append("Error on line ")
                            .append(lineNumber - 3)
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
    public void tick() {
        super.tick();
        if (nextProblemTime > 0 && System.currentTimeMillis() >= nextProblemTime) {
            nextProblemTime = 0; // Reset the timer
            currentProblemIndex++;
            saveProgress(); // Save progress after moving to the next problem
            displayCurrentProblem();
        }
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

        // Save the current progress
        saveProgress();

        // Shutdown the test executor to free resources
        testExecutor.shutdownNow();
    }
}
