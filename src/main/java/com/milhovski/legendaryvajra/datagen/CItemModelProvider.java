package com.milhovski.legendaryvajra.datagen;

import com.milhovski.legendaryvajra.LegendaryVajra;
import com.milhovski.legendaryvajra.init.CItems;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class CItemModelProvider extends ItemModelProvider {

    public CItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, LegendaryVajra.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        handheldItem(CItems.VAJRA.get());
        handheldItem(CItems.NEOVAJRA.get());
    }

}
