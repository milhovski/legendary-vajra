package com.milhovski.legendaryvajra.common.item;

import com.milhovski.legendaryvajra.common.tier.EToolMaterials;
import com.milhovski.legendaryvajra.init.CDataComponents;
import com.milhovski.legendaryvajra.common.tag.CTags;
import com.milhovski.legendaryvajra.utils.RadiusMap;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.energy.IEnergyStorage;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Vajra extends DiggerItem {

    private static final int ENERGY_PER_BLOCK = 1000;
    private static final int MAX_ENERGY = 7_000_000;

    public Vajra(Tier tier, Properties properties,
                 ItemCapability<IEnergyStorage, Void> itemCapability) {
        super(tier, CTags.Blocks.VAJRA_MINEABLE, properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable TooltipContext context,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        int mode = stack.getOrDefault(CDataComponents.RADIUS_MODE.get(), 0);
        int width = (mode == 0 ? 1 : 3);
        int energy = stack.getOrDefault(CDataComponents.ENERGY.get(), 0);
        boolean silk = stack.getOrDefault(CDataComponents.SILK_MODE.get(), false);

        tooltip.add(Component.literal("Energy: ").withStyle(ChatFormatting.GOLD)
                .append(Component.literal(energy + "/" + MAX_ENERGY).withStyle(ChatFormatting.GRAY)));

        tooltip.add(Component.literal("Mode: ").withStyle(ChatFormatting.GOLD)
                .append(Component.literal(silk ? "Silk Touch" : "Normal").withStyle(ChatFormatting.GRAY)));

        tooltip.add(Component.literal("Area: ").withStyle(ChatFormatting.GOLD)
                .append(Component.literal(width + "x" + width).withStyle(ChatFormatting.GRAY)));
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
        return true;
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
            return InteractionResultHolder.success(stack);
        } else {
            int currentRadius = stack.getOrDefault(CDataComponents.RADIUS_MODE.get(), 0);
            int nextRadius = (currentRadius == 0 ? 1 : 0);
            stack.set(CDataComponents.RADIUS_MODE.get(), nextRadius);

            if (!level.isClientSide) {
                int width = nextRadius == 0 ? 1 : 3;
                player.displayClientMessage(
                        Component.literal("Area: " + width + "x" + width)
                                .withStyle(ChatFormatting.GRAY),
                        true
                );
            }
            return InteractionResultHolder.success(stack);
        }
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
        Level level = entity.level();

        int mode = stack.getOrDefault(CDataComponents.RADIUS_MODE.get(), 0);
        int range = (mode == 0 ? 0 : 1);

        BlockHitResult traceResult = level.clip(new ClipContext(
                entity.getEyePosition(1f),
                entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(6f)),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                entity
        ));

        if (traceResult.getType() == HitResult.Type.MISS) return positions;

        Direction.Axis axis = traceResult.getDirection().getAxis();
        for (int dx = -range; dx <= range; dx++) {
            for (int dy = -range; dy <= range; dy++) {
                if (axis == Direction.Axis.Y) {
                    positions.add(initialBlockPos.offset(dx, 0, dy));
                } else if (axis == Direction.Axis.X) {
                    positions.add(initialBlockPos.offset(0, dy, dx));
                } else {
                    positions.add(initialBlockPos.offset(dx, dy, 0));
                }
            }
        }

        return positions;
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state) {
        if (!state.is(CTags.Blocks.VAJRA_MINEABLE)) return super.getDestroySpeed(stack, state);
        int energy = stack.getOrDefault(CDataComponents.ENERGY.get(), 0);
        return energy >= ENERGY_PER_BLOCK ? 100000F : Math.max(1.0F, EToolMaterials.VAJRA.getSpeed());
    }

    @Override
    public boolean mineBlock(@NotNull ItemStack stack, @NotNull Level level,
                             @NotNull BlockState state, @NotNull BlockPos pos,
                             @NotNull LivingEntity entity) {
        if (level.isClientSide) return true;

        int energy = stack.getOrDefault(CDataComponents.ENERGY.get(), 0);
        if (energy < ENERGY_PER_BLOCK) {
            ((EToolMaterials) getTier()).setSpeed(0);
            return false;
        }

        stack.set(CDataComponents.ENERGY.get(), energy - ENERGY_PER_BLOCK);
        ((EToolMaterials) getTier()).setSpeed(100000);

        ItemStack tool = toolForDrops(stack, level.registryAccess());
        Block.dropResources(state, level, pos, level.getBlockEntity(pos), entity, stack);
        level.removeBlock(pos, false);

        return true;
    }

}
