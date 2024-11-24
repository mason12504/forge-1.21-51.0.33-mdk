INSTALLATION GUIDE
------------------------
Download Minecraft Java Edition here: https://www.minecraft.net/en-us/download
Login with your Microsoft account, if you have yet to purchase it do that
Download forge Installer here 1.21-51.0.33 here: https://files.minecraftforge.net/net/minecraftforge/forge/index_1.21.html
Download Java here: https://www.java.com/download/ie_manual.jsp
Once both are downloaded go to your Minecraft launcher and select the forge profile and run it
On the Main Menu click on the mods option then click "Open Mods Folder"
Drag the mod .jar file into that folder
The Mod is now Installed!


USERS
------------------------
Once your in the game play as normal until you find a village
When a village is found look for new structure, should have more glass then others and a terminal block on the inside
Open the chest in the house to find the lesson instructions, once you do that your 1st problem should unlock in the advancements tab 
Open the terminal and click on learning mode to start solving problems (The problem your currently on will save if you leave the terminal screen)
Once you solve a problem you'll get a reward, find its corresponding villager to exchange your reward for in game items
Repeat for all 11 problems!
If you choose to you can also use the terminal as a normal java compiler if you don't choose TEST CASES or LEARNING MODE when opening the terminal

GRADERS
------------------------
Grader Specific Installation:
Minecraft Java Edition (Will provide one of our logins in separate email if game is not owned) : https://www.minecraft.net/en-us/download
Source Code:https://github.com/mason12504/forge-1.21-51.0.33-mdk
Java Runtimes (Adoptium): https://adoptium.net/temurin/releases/?package=jdk&os=windows
Java IDE (Intelji Community Edition): https://www.jetbrains.com/idea/download/?section=windows
Forge Installer(Needs to be run once): https://files.minecraftforge.net/net/minecraftforge/forge/index_1.21.html
Java: https://www.java.com/download/ie_manual.jsp

Out of Game:
Open Intelji and open the forge-1.21-51.0.33-mdk in Intelji to see all source code
Go to Src -> Main -> Java -> Net -> CarsonKing -> codingmod to see all main source code
-Block folder contains all code around custom blocks themselves
-ModItems Folder contains the item form of all blocks/items (things you can hold in your inventory)
-Screen -> TerminalScreen Holds the terminal Screen class which includes most of the code for the Terminal
-Widget-> TextAreaWidget Holds all the code for everything text related
-codingmod class holds all the main calls and registers for the mod
-Main -> Recources -> has all of our custom mod image data
INSERT BOOK CODE LOCATION
INSERT ADVACMENTS CODE LOCATION
INSERT VILLAGE GENERATION CODE LOCATION
All of our test cases, problems(with answers), and lesson plan are located in the GitHub in the folder Deliverables

In Game:
Hit the play button at the top of the Intelji screen to open Minecraft
Press Single Player
Press Create new world (Make sure Game mode is set to creative and cheats are on)
Once in game type the command (by pressing t to open the chat box) /locate structure minecraft:village_desert
While still in the text box click on the green coordinates there should be a ~ in the middle representing height, make it 130 then press enter
Once there look for a house with blue blocks and lots of glass, that's where the terminal is
If you interact with the chest there should be a book with the lessons, and you should get an advancement as well, unlocking the 1st problem (check advancements by hitting escape, then advancements)
Interact with the Terminal block (Should look like a small black computer) to open it
To run our automated test cases press TEST CASES to run those
To just run normal code start entering code without pressing TEST CASES or LEARNING MODE (Please see NOTES at the bottom before you begin coding)
To run through the problems press LEARNING MODE
Each problem completion will reward you with a custom item, and a advancement indicating your moving onto the next problem (if you leave the terminal at any point in learning mode it'll save the problem your currently on)
Look for a villager who (INSERT VILLAGER DESCRIPTION HERE) interact with him and you can click on certain trades they offer for the items you gain.
There are only 11 problems, when that's done that's the majority of our mod, you can either mess around with the base terminal or just hit escape and quit game.


Basic Controls
Movement: WASD
Break: Left Click
Interact: Right Click
Jump: Space Bar
Fly: Double Press Space Bar
Stop Flying: Double Press Space Bar
Sprint: Control Or Double Click W
Chat Box/Command Line: T

------------------------


DEVS
------------------------



Note: Arrays and ArrayLists are the only imports beyond base java that are added, defining your own classes does not work,
and the starting method must always be "public void playerMain() {" which replaces the standard main method,
other then that sky is the limit. Also when leaving the terminal or resizing the terminal with code inside it will erase that code.
 
