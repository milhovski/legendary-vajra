package com.milhovski.legendaryvajra.datagen;

import com.milhovski.legendaryvajra.init.CItems;

import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class CRecipeProvider extends RecipeProvider implements IConditionBuilder {

    public CRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, CItems.VAJRA.get())
                .pattern("AAA")
                .pattern("ABD")
                .pattern("ADC")
                .define('A', Items.NETHER_STAR)
                .define('B', Items.NETHERITE_PICKAXE)
                .define('D', Items.NETHERITE_INGOT)
                .define('C', Items.NETHERITE_BLOCK)
                .unlockedBy("has_star", InventoryChangeTrigger.TriggerInstance.hasItems(Items.NETHER_STAR))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, CItems.NEOVAJRA.get())
                .pattern("CCC")
                .pattern("CBC")
                .pattern("CCC")
                .define('B', CItems.VAJRA)
                .define('C', Items.NETHERITE_BLOCK)
                .unlockedBy("has_star", InventoryChangeTrigger.TriggerInstance.hasItems(Items.NETHER_STAR))
                .save(recipeOutput);
    }

}
