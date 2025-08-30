package com.milhovski.legendaryvajra.common.event;

import com.milhovski.legendaryvajra.LegendaryVajra;
import com.milhovski.legendaryvajra.common.item.Vajra;
import com.milhovski.legendaryvajra.init.CDataComponents;
import com.milhovski.legendaryvajra.common.tag.CTags;
import com.milhovski.legendaryvajra.utils.RadiusMap;

import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

@EventBusSubscriber(modid = LegendaryVajra.MOD_ID)
public class CEvents {

    private static final int ENERGY_PER_BLOCK = 1000;

    @SubscribeEvent
    public static void onVajraUsage(PlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getEntity();
        Level level = event.getLevel();
        if (level.isClientSide()) return;
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (!(stack.getItem() instanceof Vajra)) {
            stack = player.getItemInHand(InteractionHand.OFF_HAND);
            if (!(stack.getItem() instanceof Vajra)) return;
        }

        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        if (state.is(Blocks.BEDROCK) || state.isAir() || !state.is(CTags.Blocks.VAJRA_MINEABLE)) return;

        boolean isSneaking = player.isCrouching();
        int radius = isSneaking ? 0 : RadiusMap.getVajraRadius().getOrDefault(stack.getItem(), 0);

        List<BlockPos> blocksToBreak = Vajra.getBlocksToBeDestroyed(stack, pos, serverPlayer);
        boolean creative = player.getAbilities().instabuild;

        for (BlockPos targetPos : blocksToBreak) {
            BlockState targetState = level.getBlockState(targetPos);
            if (targetState.is(Blocks.BEDROCK) || targetState.isAir() || !targetState.is(CTags.Blocks.VAJRA_MINEABLE))
                continue;

            if (!creative) {
                int energy = stack.getOrDefault(CDataComponents.ENERGY.get(), 0);
                if (energy < ENERGY_PER_BLOCK) {
                    player.displayClientMessage(Component.literal("Not enough energy"), true);
                    break;
                }
                stack.set(CDataComponents.ENERGY.get(), energy - ENERGY_PER_BLOCK);
            }

            ItemStack tool = Vajra.toolForDrops(stack, level.registryAccess());
            Block.dropResources(targetState, level, targetPos, level.getBlockEntity(targetPos), player, tool);
            level.destroyBlock(targetPos, false, player);
        }

        event.setCanceled(true);
    }

}
