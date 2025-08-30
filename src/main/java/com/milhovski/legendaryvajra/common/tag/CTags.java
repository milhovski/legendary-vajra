package com.milhovski.legendaryvajra.common.tag;

import com.milhovski.legendaryvajra.LegendaryVajra;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;

public class CTags {

    public static class Blocks {
        public static final TagKey<Block> VAJRA_MINEABLE =
                BlockTags.create(ResourceLocation.fromNamespaceAndPath(LegendaryVajra.MOD_ID, "vajra_mineable"));
    }

    public static class Enchantments {
        public static final TagKey<Enchantment> VAJRA_SILK =
                TagKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(LegendaryVajra.MOD_ID, "vajra_silk"));
    }

}
