package com.milhovski.legendaryvajra.common.item;

import com.milhovski.legendaryvajra.common.tier.EToolMaterials;
import com.milhovski.legendaryvajra.init.CDataComponents;
import com.milhovski.legendaryvajra.common.tag.CTags;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.energy.IEnergyStorage;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Vajra extends DiggerItem {

    private static final int ENERGY_PER_BLOCK = 1000;
    private static final int MAX_ENERGY = 3_000_000;

    public Vajra(Tier tier, Properties properties,
                 ItemCapability<IEnergyStorage, Void> itemCapability) {
        super(tier, CTags.Blocks.VAJRA_MINEABLE,
                properties.attributes(DiggerItem.createAttributes(
                    tier, EToolMaterials.VAJRA.getAttackDamageBonus(), 1.6f)));
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        return super.getName(stack).copy().withStyle(Style.EMPTY.withColor(0xFF0000));
    }

    @Override
    public boolean isCorrectToolForDrops(@NotNull ItemStack stack, @NotNull BlockState state) {
        return true;
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        return true;
    }

    @Override
    public void postHurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {}

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable TooltipContext context,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        int energy = stack.getOrDefault(CDataComponents.ENERGY.get(), 0);
        boolean silk = stack.getOrDefault(CDataComponents.SILK_MODE.get(), false);

        tooltip.add(Component.literal(energy + " / " + MAX_ENERGY + " ⚡")
                .withStyle(ChatFormatting.AQUA));

        tooltip.add(Component.literal("Silk Touch: ").withStyle(ChatFormatting.DARK_PURPLE)
                .append(Component.literal(silk ? "On" : "Off")
                        .withStyle(silk ? ChatFormatting.GREEN : ChatFormatting.RED)));
    }

    @Override
    public boolean isDamageable(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return super.isEnchantable(stack);
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return stack.getOrDefault(CDataComponents.SILK_MODE.get(), false);
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return stack.getOrDefault(CDataComponents.ENERGY.get(), 0) > 0;
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        int energy = Mth.clamp(stack.getOrDefault(CDataComponents.ENERGY.get(), 0), 0, MAX_ENERGY);
        return Math.round(13f * energy / MAX_ENERGY);
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        return 0x7600E6;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.isShiftKeyDown()) {
            boolean current = stack.getOrDefault(CDataComponents.SILK_MODE.get(), false);
            stack.set(CDataComponents.SILK_MODE.get(), !current);

            if (!level.isClientSide) {
                player.displayClientMessage(
                        Component.literal("Silk Touch: " + (!current ? "ON" : "OFF"))
                                .withStyle(ChatFormatting.GRAY),
                        true
                );
            }
        }

        player.getInventory().setChanged();
        return InteractionResultHolder.success(stack);
    }

    @Override
    public boolean isPrimaryItemFor(@NotNull ItemStack stack, @NotNull Holder<Enchantment> enchantment) {
        if (enchantment.is(CTags.Enchantments.VAJRA_SILK)) {
            return stack.getOrDefault(CDataComponents.SILK_MODE.get(), false);
        }
        return super.isPrimaryItemFor(stack, enchantment);
    }

    public static ItemStack toolForDrops(ItemStack stack, RegistryAccess access) {
        if (stack.getOrDefault(CDataComponents.SILK_MODE.get(), false)) {
            ItemStack copy = stack.copy();
            Holder<Enchantment> silk = access.registryOrThrow(Registries.ENCHANTMENT)
                    .getHolderOrThrow(Enchantments.SILK_TOUCH);
            copy.enchant(silk, 1);
            return copy;
        }
        return stack;
    }

    public static List<BlockPos> getBlocksToBeDestroyed(ItemStack stack, BlockPos initialBlockPos, LivingEntity entity) {
        List<BlockPos> positions = new ArrayList<>();
        positions.add(initialBlockPos);
        return positions;
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state) {
        if (state.is(Blocks.BEDROCK) || state.is(Blocks.BARRIER)) return super.getDestroySpeed(stack, state);
        int energy = stack.getOrDefault(CDataComponents.ENERGY.get(), 0);
        return energy >= ENERGY_PER_BLOCK ? 100000F : 3.0F;
    }

    @Override
    public boolean mineBlock(@NotNull ItemStack stack, @NotNull Level level,
                             @NotNull BlockState state, @NotNull BlockPos pos,
                             @NotNull LivingEntity entity) {
        if (level.isClientSide) return true;
        if (!(entity instanceof Player player)) return true;

        int energy = stack.getOrDefault(CDataComponents.ENERGY.get(), 0);
        if (energy < ENERGY_PER_BLOCK) return true;

        boolean creative = player.getAbilities().instabuild;
        ItemStack tool = toolForDrops(stack, level.registryAccess());

        if (!creative) {
            stack.set(CDataComponents.ENERGY.get(), energy - ENERGY_PER_BLOCK);
        }
        Block.dropResources(state, level, pos, level.getBlockEntity(pos), player, tool);
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);

        List<BlockPos> blocksToBreak = getBlocksToBeDestroyed(stack, pos, entity);
        for (BlockPos targetPos : blocksToBreak) {
            if (targetPos.equals(pos)) continue;

            BlockState targetState = level.getBlockState(targetPos);
            if (targetState.isAir() || targetState.is(Blocks.BEDROCK) || !targetState.is(CTags.Blocks.VAJRA_MINEABLE))
                continue;

            if (!creative) {
                int e = stack.getOrDefault(CDataComponents.ENERGY.get(), 0);
                if (e < ENERGY_PER_BLOCK) break;
                stack.set(CDataComponents.ENERGY.get(), e - ENERGY_PER_BLOCK);
            }

            Block.dropResources(targetState, level, targetPos, level.getBlockEntity(targetPos), player, tool);
            level.setBlock(targetPos, Blocks.AIR.defaultBlockState(), 3);
        }

        return false;
    }

}
