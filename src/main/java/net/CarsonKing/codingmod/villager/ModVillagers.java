package net.CarsonKing.codingmod.villager;

import com.google.common.collect.ImmutableSet;
import com.google.errorprone.annotations.Immutable;
import net.CarsonKing.codingmod.block.ModBlocks;
import net.CarsonKing.codingmod.codingmod;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModVillagers {
    public static final DeferredRegister<PoiType> POI_TYPES =
            DeferredRegister.create(ForgeRegistries.POI_TYPES, codingmod.MOD_ID);
    public static final DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS =
            DeferredRegister.create(ForgeRegistries.VILLAGER_PROFESSIONS, codingmod.MOD_ID);

    // point of interest type for the Coder villager profession is the terminal block.
    public static final RegistryObject<PoiType> CODER_POI = POI_TYPES.register("coder_poi",
            () -> new PoiType(ImmutableSet.copyOf(ModBlocks.TERMINAL.get().getStateDefinition().getPossibleStates()),
                    1, 1));

    // create the coder profession
    public static final RegistryObject<VillagerProfession> CODER_PROF =
            VILLAGER_PROFESSIONS.register("coder", ()-> new VillagerProfession("coder",
                    holder -> holder.get() == CODER_POI.get(), holder -> holder.get() == CODER_POI.get(),
                    ImmutableSet.of(), ImmutableSet.of(), SoundEvents.ANVIL_FALL));
                    // ANVIL_FALL PROBABLY IS TEMPORARY

    // register the profession and point of interest blocks to respective classes
    public static void register(IEventBus eventBus){
        POI_TYPES.register(eventBus);
        VILLAGER_PROFESSIONS.register(eventBus);
    }
}
