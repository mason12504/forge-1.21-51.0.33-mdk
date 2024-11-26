package net.CarsonKing.codingmod.villager;

import net.CarsonKing.codingmod.enchantment.ModEnchantmentInstance;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import java.util.ArrayList;

// adds a new type of trade to the villager trading class
public class ModVillagerTrades extends VillagerTrades {

    // set up variables, this class is highly based on the vanilla EnchantBookForEmeralds class
    public static class SpecialEnchantment implements VillagerTrades.ItemListing{

        private final Item ItemToEnchant;
        private final ArrayList<ModEnchantmentInstance> GivenEnchantments;  // list of enchantments to apply
        private final Item CostItem;    // the item the player gives to the villager for this trade

        // method to call and pass variables
        public SpecialEnchantment(Item GivenItem, Item reward, ArrayList<ModEnchantmentInstance> GivenEnchantments) {
             this.GivenEnchantments = GivenEnchantments;
             this.ItemToEnchant = GivenItem;
             this.CostItem = reward;
        }
        // just makes sense to override as this is already in the villager code
        @Override
        public MerchantOffer getOffer(Entity pTrader, RandomSource pRandom) {

            // need this to set up a holder of type enchantment for the .enchant command
            // I am not sure exactly why it must be done this way but this is essentially how vanilla does it.
            RegistryAccess registryAccess = pTrader.level().registryAccess();

            // Set up the item to be enchanted
            ItemStack itemstack = new ItemStack(ItemToEnchant);

            // for each enchantment given, put it on the item
            for(int i = 0; i < GivenEnchantments.size(); i++){

                // set up enchantments
                Holder<Enchantment> enchant = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(GivenEnchantments.get(i).Enchantment);
                int level = GivenEnchantments.get(i).level;

                // enchant the item
                itemstack.enchant(enchant, level);

            }
            // set up the final trade offer that the villager will display
            return new MerchantOffer(new ItemCost(CostItem, 1),
                    itemstack, 100, 40, 0.2F);
        }
    }

}
