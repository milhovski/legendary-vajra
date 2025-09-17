package com.milhovski.legendaryvajra.common.item;

import com.milhovski.legendaryvajra.common.tier.EToolMaterials;
import com.milhovski.legendaryvajra.init.CDataComponents;
import com.milhovski.legendaryvajra.common.tag.CTags;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
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
        super(tier, CTags.Blocks.VAJRA_MINEABLE,
                properties.attributes(DiggerItem.createAttributes(
                    tier, EToolMaterials.VAJRA.getAttackDamageBonus(), 1.6f)));
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        return super.getName(stack).copy().withStyle(Style.EMPTY.withColor(0xFFB3C6));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable TooltipContext context,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        int mode = stack.getOrDefault(CDataComponents.RADIUS_MODE.get(), 0);
        int width = (mode == 0 ? 1 : 3);
        int energy = stack.getOrDefault(CDataComponents.ENERGY.get(), 0);
        boolean silk = stack.getOrDefault(CDataComponents.SILK_MODE.get(), false);

        tooltip.add(Component.literal(energy + " / " + MAX_ENERGY + " ⚡")
                .withStyle(ChatFormatting.AQUA));

        tooltip.add(Component.literal("Silk Touch: ").withStyle(ChatFormatting.DARK_PURPLE)
                .append(Component.literal(silk ? "On" : "Off")
                        .withStyle(silk ? ChatFormatting.GREEN : ChatFormatting.RED)));

        tooltip.add(Component.literal("Area: ").withStyle(ChatFormatting.DARK_PURPLE)
                .append(Component.literal(width + "x" + width)
                        .withStyle(ChatFormatting.GREEN)));
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

        if (range == 0) {
            positions.add(initialBlockPos);
            return positions;
        }

        BlockHitResult traceResult = level.clip(new ClipContext(
                entity.getEyePosition(1f),
                entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(6f)),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                entity
        ));

        Direction side;

        if (traceResult.getType() == HitResult.Type.BLOCK && traceResult.getBlockPos().equals(initialBlockPos)) {
            side = traceResult.getDirection();
        } else {
            Vec3 toCenter = Vec3.atCenterOf(initialBlockPos).subtract(entity.getEyePosition(1f));
            side = Direction.getNearest(toCenter.x, toCenter.y, toCenter.z);
        }

        for (int dx = -range; dx <= range; dx++) {
            for (int dy = -range; dy <= range; dy++) {
                switch (side) {
                    case UP, DOWN -> {
                        positions.add(initialBlockPos.offset(dx, 0, dy));
                    }
                    case NORTH, SOUTH -> {
                        positions.add(initialBlockPos.offset(dx, dy, 0));
                    }
                    case EAST, WEST -> {
                        positions.add(initialBlockPos.offset(0, dy, dx));
                    }
                }
            }
        }

        return positions;
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state) {
        if (!state.is(CTags.Blocks.VAJRA_MINEABLE)) return super.getDestroySpeed(stack, state);
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
