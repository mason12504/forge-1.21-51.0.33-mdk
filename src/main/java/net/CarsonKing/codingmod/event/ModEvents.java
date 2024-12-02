package net.CarsonKing.codingmod.event;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.CarsonKing.codingmod.ModItems.ModItems;
import net.CarsonKing.codingmod.block.ModBlocks;
import net.CarsonKing.codingmod.codingmod;
import net.CarsonKing.codingmod.enchantment.ModEnchantmentInstance;
import net.CarsonKing.codingmod.villager.ModVillagerTrades;
import net.CarsonKing.codingmod.villager.ModVillagers;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


import java.util.ArrayList;
import java.util.List;

// Contains all ten of the villager trades for reward items.
@Mod.EventBusSubscriber(modid=codingmod.MOD_ID)
public class ModEvents {

    @SubscribeEvent
    public static void addCustomTrades(VillagerTradesEvent event){

        // add coder trades to the coder profession
        if(event.getType() == ModVillagers.CODER_PROF.get()){
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();

            // trades.get(Level of villager) creates a new trade with given Itemcost and Itemstack
            // I put max uses at 100 and pXp at 40 for all, as rewards shouldn't be significantly limited, and one trade is enough to level up the villager for the most part to get new reward tiers
            trades.get(1).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemCost(ModItems.PROBLEM_1_COMPLETE.get(), 1),
                    new ItemStack(Items.IRON_PICKAXE, 1),
                    100, 40, 0.02f));

            trades.get(1).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemCost(ModItems.PROBLEM_2_COMPLETE.get(), 1),
                    new ItemStack(Items.COAL_BLOCK, 12),
                    100, 40, 0.02f));

            trades.get(2).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemCost(ModItems.PROBLEM_3_COMPLETE.get(), 1),
                    new ItemStack(Items.IRON_BLOCK, 12),
                    100, 40, 0.02f));

            // Enchanted items use a different method, passing a list of desired enchantments as well as the item itself
            trades.get(2).add(new ModVillagerTrades.SpecialEnchantment(
                    Items.SHEARS, ModItems.PROBLEM_4_COMPLETE.get(),
                    new ArrayList<ModEnchantmentInstance>(){
                        {
                            add(new ModEnchantmentInstance(Enchantments.UNBREAKING, 3));
                            add(new ModEnchantmentInstance(Enchantments.MENDING, 1));
                        }
                    }));

            trades.get(3).add(new ModVillagerTrades.SpecialEnchantment(
                    Items.BOW, ModItems.PROBLEM_5_COMPLETE.get(),
                    new ArrayList<ModEnchantmentInstance>(){
                        {
                            add(new ModEnchantmentInstance(Enchantments.POWER, 4));
                            add(new ModEnchantmentInstance(Enchantments.INFINITY, 1));
                        }
                    }));

            trades.get(3).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemCost(ModItems.PROBLEM_6_COMPLETE.get(), 1),
                    new ItemStack(Items.SLIME_BLOCK, 24),
                    100, 8, 0.02f));

            trades.get(4).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemCost(ModItems.PROBLEM_7_COMPLETE.get(), 1),
                    new ItemStack(Items.GOLDEN_APPLE, 16),
                    100, 40, 0.02f));

            trades.get(4).add(new ModVillagerTrades.SpecialEnchantment(
                    Items.DIAMOND_SWORD, ModItems.PROBLEM_8_COMPLETE.get(),
                    new ArrayList<ModEnchantmentInstance>(){
                        {
                            add(new ModEnchantmentInstance(Enchantments.SHARPNESS, 5));
                            add(new ModEnchantmentInstance(Enchantments.LOOTING, 3));
                            add(new ModEnchantmentInstance(Enchantments.UNBREAKING, 3));
                            add(new ModEnchantmentInstance(Enchantments.SWEEPING_EDGE, 3));
                            add(new ModEnchantmentInstance(Enchantments.MENDING, 1));
                        }
                    }));

            trades.get(5).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemCost(ModItems.PROBLEM_9_COMPLETE.get(), 1),
                    new ItemStack(Items.TOTEM_OF_UNDYING, 1),
                    100, 40, 0.02f));

            trades.get(5).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemCost(ModItems.PROBLEM_10_COMPLETE.get(), 1),
                    new ItemStack(ModBlocks.TERMINAL.get(), 1),
                    100, 40, 0.02f));


        }
    }
}
