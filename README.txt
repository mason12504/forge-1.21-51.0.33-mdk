INSTALLATION GUIDE
------------------------
A video walkthrough is also available in the files (User install walkthrough pt1.mp4), but here are the steps if you prefer a more concise format:
Download Minecraft Java Edition 1.21 here: https://www.minecraft.net/en-us/download
Note: The mod is only verified to run on 1.21, and may not work with 1.21.3 as it changes some things. 
Login with your Microsoft account, if you have yet to purchase it do that
Download forge Installer here 1.21-51.0.33 here: https://files.minecraftforge.net/net/minecraftforge/forge/index_1.21.html
Download Java here: https://www.java.com/download/ie_manual.jsp
Once both are downloaded go to your Minecraft launcher and select the forge profile and run it
On the Main Menu click on the mods option then click "Open Mods Folder"
Drag the mod .jar file into that folder
The Mod is now Installed!


USERS
------------------------
Once you're in the game play as normal until you find a village
When a village is found look for new structure, it should have more glass than others and a terminal block on the inside
Open the chest in the house to find the lesson instructions, once you do that your 1st problem should unlock in the advancements tab 
Open the terminal and click on learning mode to start solving problems (The problem you're currently on will save if you leave the terminal screen)
Once you solve a problem you'll get a reward, find its corresponding villager to exchange your reward for in game items
Repeat for all 11 problems!
If you choose to you can also use the terminal as a normal java compiler if you don't choose TEST CASES or LEARNING MODE when opening the terminal
(Video walkthrough part 2 covers these user steps as well) 

GRADERS
------------------------
Grader Specific Installation:
Minecraft Java Edition (Will provide one of our logins in separate email if game is not owned) : https://www.minecraft.net/en-us/download
Source Code:https://github.com/mason12504/forge-1.21-51.0.33-mdk
Java Runtimes (Adoptium): https://adoptium.net/temurin/releases/?package=jdk&os=windows
Java IDE (IntelliJ Community Edition): https://www.jetbrains.com/idea/download/?section=windows
Forge Installer(Needs to be run once): https://files.minecraftforge.net/net/minecraftforge/forge/index_1.21.html
Java: https://www.java.com/download/ie_manual.jsp

Out of Game:
Open IntelliJ and open the forge-1.21-51.0.33-mdk in IntelliJ to see all source code
Go to Src -> Main -> Java -> Net -> CarsonKing -> codingmod to see all main source code
-Block folder contains all code around custom blocks themselves
-ModItems Folder contains the item form of all blocks/items (things you can hold in your inventory)
-Screen -> TerminalScreen Holds the terminal Screen class which includes most of the code for the Terminal
-Widget-> TextAreaWidget Holds all the code for everything text related
-codingmod class holds all the main calls and registers for the mod
-Main -> Resources -> has all of our custom mod image data
-Main ->Resources -> Data -> Minecraft -> Structure -> codingmod -> houses -> terminal_houses_desert:
has the code for the generated coding structure, along with the instructional book.
-Main -> Resources -> Data -> Advancements: has all advancements code data

Village generation code is handled with a datapack implementation, in Src->Main->resources->data->minecraft->structure->village, 
the terminal_house_desert and terminal_house nbt files contain the village structure itself, 
and the various json files in worldgen->template_pool->village are edited versions of the vanilla jsons to include the modded structure in standard village generation

Villager trade code is in Src -> Main -> Java -> Net -> CarsonKing -> codingmod, 
with event-> ModEvents creating the trade events, and villager -> ModVillagers and ModVillagerTrades creating the villager occupation and a new trade type for non-randomly enchanted tools. 

All of our test cases, problems(with answers), and lesson plan are located in the GitHub

In Game:
Open and launch Minecraft on the forge profile
Once launched click on mods, then mods folder and drag our jar file into it
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
Look for a villager who has a white lab coat and a blue hat,  interact with him and you can click on certain trades they offer for the items you gain.
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

Terminal
Run: Runs the code
Clear: Clears all current code
TEST CASES: Runs Automated tests
LEARNING MODE: Enables learning mode
Click on the output screen and use the arrow keys to move left and right, scroll to go up and down

------------------------


DEVS
------------------------
INSTALLATION
Minecraft Java Edition: https://www.minecraft.net/en-us/download
Source Code:https://github.com/mason12504/forge-1.21-51.0.33-mdk
Java Runtimes (Adoptium): https://adoptium.net/temurin/releases/?package=jdk&os=windows
Java IDE (IntelliJ Community Edition): https://www.jetbrains.com/idea/download/?section=windows
Forge Installer(Needs to be run once): https://files.minecraftforge.net/net/minecraftforge/forge/index_1.21.html
Java: https://www.java.com/download/ie_manual.jsp


Key Components:
The terminal screen class controls the terminals ability to compile code, so any changes to code compilations should be done there, along with the automated testing and the learning mode
If you wish to edit the text interaction aspect of the terminal go to the TextAreaWidget class
All of our test cases, problems(with answers), and lesson plan are located in the GitHub if updates are needed for them

Village generation code is handled with a datapack implementation, in Src->Main->resources->data->minecraft->structure->village, 
the terminal_house_desert and terminal_house nbt files contain the village structure itself, 
and the various json files in worldgen->template_pool->village are edited versions of the vanilla jsons to include the modded structure in standard village generation

Villager trade code is in Src -> Main -> Java -> Net -> CarsonKing -> codingmod, 
with event-> ModEvents creating the trade events, and villager -> ModVillagers and ModVillagerTrades creating the villager occupation and a new trade type for non-randomly enchanted tools. 


CODE LOCATIONS
Open IntelliJ and open the forge-1.21-51.0.33-mdk in IntelliJ to see all source code
Go to Src -> Main -> Java -> Net -> CarsonKing -> codingmod to see all main source code
-Block folder contains all code around custom blocks themselves
-ModItems Folder contains the item form of all blocks/items (things you can hold in your inventory)
-Screen -> TerminalScreen Holds the terminal Screen class which includes most of the code for the Terminal
-Widget-> TextAreaWidget Holds all the code for everything text related
-codingmod class holds all the main calls and registers for the mod
-Main -> Resources -> has all of our custom mod image data
-Main ->Resources -> Data -> Minecraft -> Structure -> codingmod -> houses -> terminal_houses_desert:
has the code for the generated coding structure, along with the instructional book.
-Main -> Resources -> Data -> Advancements: has all advancements code data










Note: Arrays and ArrayLists are the only imports beyond base java that are added, defining your own classes does not work,
and the starting method must always be "public void playerMain() {" which replaces the standard main method,
other than that sky is the limit. Also when leaving the terminal or resizing the terminal with code inside it will erase that code.

Modding Tutorials by KaupenJoe were used for development 
https://github.com/Tutorials-By-Kaupenjoe/Forge-Tutorial-1.21.X 
 
