package net.CarsonKing.codingmod.event;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.CarsonKing.codingmod.ModItems.ModItems;
import net.CarsonKing.codingmod.codingmod;
import net.CarsonKing.codingmod.villager.ModVillagers;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid=codingmod.MOD_ID)
public class ModEvents {

    @SubscribeEvent
    public static void addCustomTrades(VillagerTradesEvent event){

        // add a trade to the farmer villager
        if(event.getType() == VillagerProfession.FARMER){
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
            // trades.get(int) where int is the level of the villager ingame.
            trades.get(1).add((pTrader, pRandom) -> new MerchantOffer(
                    // This differs from Kaupenjoe tutorial for 1.20.
                    // ItemStack --> ItemCost. Seems to work just fine
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(ModItems.TESTITEMMASON.get(), 1),
                    100, 8, 0.02f));
        }

        // coder trades
        if(event.getType() == ModVillagers.CODER_PROF.get()){
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
            trades.get(1).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(ModItems.TESTITEMMASON.get(), 1),
                    100, 8, 0.02f));

            trades.get(1).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemCost(ModItems.PROBLEM_1_COMPLETE.get(), 1),
                    new ItemStack(Items.IRON_PICKAXE, 1),
                    100, 8, 0.02f));

        }
    }
}
