package net.CarsonKing.codingmod.ModItems;

import net.CarsonKing.codingmod.block.ModBlocks;
import net.CarsonKing.codingmod.codingmod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

//Creates a custom creative mode tab
public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab>CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, codingmod.MOD_ID);


    //Creates the creative mode tabs for items
    public static final RegistryObject<CreativeModeTab> TEST_ITEMS_TAB = CREATIVE_MODE_TABS.register("test_items_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.TERMINAL.get()))
                    .title(Component.translatable("creativetab.codingmod.testitem_items"))
                    // Add the following items into the creative tab.
                    .displayItems((itemDisplayParameters, output) -> {
                        // blocks
                        output.accept(ModBlocks.TERMINAL.get());
                        output.accept(ModBlocks.TEST_BLOCK.get());

                        // all the reward items
                        output.accept(ModItems.PROBLEM_1_COMPLETE.get());
                        output.accept(ModItems.PROBLEM_2_COMPLETE.get());
                        output.accept(ModItems.PROBLEM_3_COMPLETE.get());
                        output.accept(ModItems.PROBLEM_4_COMPLETE.get());
                        output.accept(ModItems.PROBLEM_5_COMPLETE.get());
                        output.accept(ModItems.PROBLEM_6_COMPLETE.get());
                        output.accept(ModItems.PROBLEM_7_COMPLETE.get());
                        output.accept(ModItems.PROBLEM_8_COMPLETE.get());
                        output.accept(ModItems.PROBLEM_9_COMPLETE.get());
                        output.accept(ModItems.PROBLEM_10_COMPLETE.get());
                        output.accept(ModItems.PROBLEM_11_COMPLETE.get());
                    }
                    ).build());



    //Adds the creative mode tab to the register bus
    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
