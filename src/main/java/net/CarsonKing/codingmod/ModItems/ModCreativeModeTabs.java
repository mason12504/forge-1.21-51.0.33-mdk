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

//Creates a custim creative mode tab
public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab>CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, codingmod.MOD_ID);


    //Creates the creative mode tabs for items
    public static final RegistryObject<CreativeModeTab> TEST_ITEMS_TAB = CREATIVE_MODE_TABS.register("test_items_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.TESTITEM.get()))
                    .title(Component.translatable("creativetab.codingmod.testitem_items"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModItems.TESTITEM.get());
                    }
                    ).build());

    //Creates the cretative mode tab for blocks
    public static final RegistryObject<CreativeModeTab> TEST_BLOCKS_TAB = CREATIVE_MODE_TABS.register("test_blocks_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.TEST_BLOCK.get()))
                    .withTabsBefore(TEST_ITEMS_TAB.getId())
                    .title(Component.translatable("creativetab.codingmod.test_blocks"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModBlocks.TEST_BLOCK.get());
                        output.accept(ModBlocks.TERMINAL.get());
                    }
                    ).build());


    //Adds the creative mode tab to the register bus
    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
