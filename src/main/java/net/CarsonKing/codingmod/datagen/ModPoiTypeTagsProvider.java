package net.CarsonKing.codingmod.datagen;

import net.CarsonKing.codingmod.codingmod;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.PoiTypeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.PoiTypeTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

// gives a PoiTypeTag (Point of interest tag) to the coder profession
// point of interest block is the terminal
public class ModPoiTypeTagsProvider extends PoiTypeTagsProvider {
    public ModPoiTypeTagsProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, pProvider, codingmod.MOD_ID, existingFileHelper);
    }

    // Basically this just adds the coder jobsite POI code from ModVillagers somehow
    @Override
    protected void addTags(HolderLookup.Provider pProvider){
        tag(PoiTypeTags.ACQUIRABLE_JOB_SITE)
                .addOptional(ResourceLocation.fromNamespaceAndPath(codingmod.MOD_ID, "coder_poi"));
}
}
