package com.milhovski.legendaryvajra.common.tier;

import com.milhovski.legendaryvajra.Config;

import net.minecraft.world.item.Tier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public enum EToolMaterials implements Tier {

    VAJRA(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, Config.DurabilityVajra, 9.0f, 10.0F, 27, () -> Ingredient.EMPTY),
    NEOVAJRA(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, Config.DurabilityVajra, 9.0f, 20.0F, 27, () -> Ingredient.EMPTY);

    private final TagKey<Block> incorrect;
    private float speed;
    private final float damage;
    private final int enchantmentValue;
    private final Supplier<Ingredient> ingredient;

    EToolMaterials(TagKey<Block> incorrect, int uses, float speed, float damage, int enchantmentValue, Supplier<Ingredient> ingredient) {
        this.incorrect = incorrect;
        this.speed = speed;
        this.damage = damage;
        this.enchantmentValue = enchantmentValue;
        this.ingredient = ingredient;
    }

    @Override
    public int getUses() {
        return -1;
    }

    @Override
    public float getSpeed() {
        return this.speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    @Override
    public float getAttackDamageBonus() {
        return this.damage;
    }

    @Override
    public @NotNull TagKey<Block> getIncorrectBlocksForDrops() {
        return this.incorrect;
    }

    @Override
    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }

    @Override
    public @NotNull Ingredient getRepairIngredient() {
        return this.ingredient.get();
    }

}
