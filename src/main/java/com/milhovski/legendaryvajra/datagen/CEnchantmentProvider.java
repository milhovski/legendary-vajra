package com.milhovski.legendaryvajra.datagen;

import com.milhovski.legendaryvajra.LegendaryVajra;
import com.milhovski.legendaryvajra.common.tag.CTags;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class CEnchantmentProvider extends TagsProvider<Enchantment> {

    protected CEnchantmentProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider,
                                   @Nullable ExistingFileHelper existingFileHelper) {
        super(output, Registries.ENCHANTMENT, provider, LegendaryVajra.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider pProvider) {
        this.tag(CTags.Enchantments.VAJRA_SILK).add(Enchantments.SILK_TOUCH);
    }

}
