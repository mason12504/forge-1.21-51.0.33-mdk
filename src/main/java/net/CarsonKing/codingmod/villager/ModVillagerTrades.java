package net.CarsonKing.codingmod.villager;

import net.CarsonKing.codingmod.ModItems.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;

import java.util.Optional;

// adds a new type of trade to the villager trading class
public class ModVillagerTrades extends VillagerTrades {

    // set up variables, this class is highly based on the vanilla EnchantBookForEmeralds class
    public static class SpecialEnchantment implements VillagerTrades.ItemListing{
        private final ResourceKey<Enchantment> GivenEnchantment;        // enchantment type
        private final Item ItemToEnchant;
        private final int level;    // level of enchantment
        private final Item CostItem;

        // method to call and pass variables
        public SpecialEnchantment(Item GivenItem, Item reward, ResourceKey<Enchantment> GivenKey, int level) {
             this.GivenEnchantment = GivenKey;
             this.ItemToEnchant = GivenItem;
             this.level = level;
             this.CostItem = reward;
        }

        @Override
        public MerchantOffer getOffer(Entity pTrader, RandomSource pRandom) {

            Registry registryEnch = pTrader.level().registryAccess().registryOrThrow(Registries.ENCHANTMENT);

            int i;
            ItemStack itemstack;
            Holder<Enchantment> enchant1 = registryEnch.getHolderOrThrow(GivenEnchantment);
            Holder<Enchantment> mendingEnchant = registryEnch.getHolderOrThrow(Enchantments.MENDING);

            itemstack = new ItemStack(ItemToEnchant);
            itemstack.enchant(enchant1, level);
            itemstack.enchant(mendingEnchant, 1);



            return new MerchantOffer(new ItemCost(CostItem, 1),
                    itemstack, 12, 40, 0.2F);
        }
    }

}
