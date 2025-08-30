package com.milhovski.legendaryvajra.datagen;

import com.milhovski.legendaryvajra.LegendaryVajra;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = LegendaryVajra.MOD_ID)
public class CDatagen {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        BlockTagsProvider blockTagsProvider = new CBlockTagProvider(packOutput, lookupProvider, existingFileHelper);
        generator.addProvider(event.includeServer(), blockTagsProvider);
        generator.addProvider(event.includeServer(), new CRecipeProvider(packOutput, lookupProvider));
        generator.addProvider(event.includeClient(), new CItemModelProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeServer(), new CEnchantmentProvider(packOutput, lookupProvider, existingFileHelper));
    }

}
