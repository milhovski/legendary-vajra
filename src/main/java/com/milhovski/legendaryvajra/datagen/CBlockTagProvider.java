package com.milhovski.legendaryvajra.datagen;

import com.milhovski.legendaryvajra.LegendaryVajra;
import com.milhovski.legendaryvajra.common.tag.CTags;

import mekanism.common.Mekanism;
import mekanism.common.tags.MekanismTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class CBlockTagProvider extends BlockTagsProvider {

    public CBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                             @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, LegendaryVajra.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(CTags.Blocks.VAJRA_MINEABLE)
                .addTag(BlockTags.MINEABLE_WITH_PICKAXE)
                .addTag(BlockTags.MINEABLE_WITH_SHOVEL)
                .addTag(BlockTags.MINEABLE_WITH_AXE)
                .addTag(BlockTags.MINEABLE_WITH_HOE)
                .add(Blocks.OBSIDIAN, Blocks.CRYING_OBSIDIAN)
                .add(Blocks.GLOWSTONE);
    }

}
