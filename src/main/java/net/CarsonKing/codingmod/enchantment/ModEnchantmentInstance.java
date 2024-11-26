package net.CarsonKing.codingmod.enchantment;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

// Basically vanilla EnchantmentInstance but with ResourceKeys instead of Holders
// Stores the type of Enchantment (ie Sharpness, Efficiency) and level (I, II, III, etc) as int
public class ModEnchantmentInstance {
    public final ResourceKey<Enchantment> Enchantment;
    public final int level;

    // initializer
    public ModEnchantmentInstance(ResourceKey<Enchantment> Enchantment, int level){
        this.Enchantment = Enchantment;
        this.level = level;
    }
}
