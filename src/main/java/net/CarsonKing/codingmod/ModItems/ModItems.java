package net.CarsonKing.codingmod.ModItems;

import net.CarsonKing.codingmod.codingmod;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
// Items class containing all the items added by the mod
public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, codingmod.MOD_ID);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    // Functional code in assets/items just like all other items.
    // Reward tokens for solving problems
    public static final RegistryObject<Item> PROBLEM_1_COMPLETE = ITEMS.register("problem_1_complete",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PROBLEM_2_COMPLETE = ITEMS.register("problem_2_complete",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PROBLEM_3_COMPLETE = ITEMS.register("problem_3_complete",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PROBLEM_4_COMPLETE = ITEMS.register("problem_4_complete",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PROBLEM_5_COMPLETE = ITEMS.register("problem_5_complete",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PROBLEM_6_COMPLETE = ITEMS.register("problem_6_complete",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PROBLEM_7_COMPLETE = ITEMS.register("problem_7_complete",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PROBLEM_8_COMPLETE = ITEMS.register("problem_8_complete",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PROBLEM_9_COMPLETE = ITEMS.register("problem_9_complete",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PROBLEM_10_COMPLETE = ITEMS.register("problem_10_complete",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PROBLEM_11_COMPLETE = ITEMS.register("problem_11_complete",
            () -> new Item(new Item.Properties()));


}
