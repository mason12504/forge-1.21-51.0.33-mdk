package net.CarsonKing.codingmod.ModItems;

import net.CarsonKing.codingmod.codingmod;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
// items class containing all the items added by the mod
public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, codingmod.MOD_ID);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    public static final RegistryObject<Item> TESTITEM = ITEMS.register("testitem",
            () -> new Item(new Item.Properties()));
    // mason - adding an item here just to see how it works
    // related code for this item can be accessed by checking usages,
    // and resources/assets.codingmod/models/item/masontest.json, (png and ingame appearance)
    // and its name is in resources/assets.codingmod//lang/en_us.json (ingame name of item)
    public static final RegistryObject<Item> TESTITEMMASON = ITEMS.register("masontest",
            () -> new Item(new Item.Properties()));
}
