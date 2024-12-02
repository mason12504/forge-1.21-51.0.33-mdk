package net.CarsonKing.codingmod;
import net.CarsonKing.codingmod.network.ModMessages;
import com.mojang.logging.LogUtils;
import net.CarsonKing.codingmod.ModItems.ModCreativeModeTabs;
import net.CarsonKing.codingmod.ModItems.ModItems;
import net.CarsonKing.codingmod.block.ModBlocks;
import net.CarsonKing.codingmod.villager.ModVillagers;
import net.CarsonKing.codingmod.villager.ModVillagerTrades; // I believe this is necessesary as it has the enchanted item trade code, but maybe not.
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(net.CarsonKing.codingmod.codingmod.MOD_ID)
public class codingmod
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "codingmod";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();




    public codingmod()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for mod loading
        modEventBus.addListener(this::commonSetup);
        ModMessages.init();

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        //Registers for block items, creative mod tab
        ModCreativeModeTabs.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);

        // Register the custom villager code
        ModVillagers.register(modEventBus);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
    }



    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {

    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {

        }
    }
}
