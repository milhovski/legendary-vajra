package com.milhovski.legendaryvajra.utils;

import com.milhovski.legendaryvajra.LegendaryVajra;
import com.milhovski.legendaryvajra.common.item.Vajra;
import com.milhovski.legendaryvajra.common.tag.CTags;
import net.minecraft.client.Minecraft;
import net.neoforged.fml.common.EventBusSubscriber;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

@EventBusSubscriber(modid = LegendaryVajra.MOD_ID)
public class VajraOverlayRenderer {

    private static final Minecraft mc = Minecraft.getInstance();

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;
        if (mc.level == null || mc.player == null) return;

        ItemStack heldItem = mc.player.getMainHandItem();
        if (!(heldItem.getItem() instanceof Vajra)) return;

        if (!(mc.hitResult instanceof BlockHitResult blockHit) || blockHit.getType() != HitResult.Type.BLOCK) return;

        BlockPos origin = blockHit.getBlockPos();
        Direction side = blockHit.getDirection();
        int range = mc.player.isShiftKeyDown()
                ? 0
                : RadiusMap.getVajraRadius().getOrDefault(heldItem.getItem(), 0);

        if (!mc.level.getBlockState(origin).is(CTags.Blocks.VAJRA_MINEABLE)) return;

        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;

        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                BlockPos pos = switch (side.getAxis()) {
                    case Y -> origin.offset(x, 0, y);
                    case X -> origin.offset(0, y, x);
                    case Z -> origin.offset(x, y, 0);
                };

                minX = Math.min(minX, pos.getX());
                minY = Math.min(minY, pos.getY());
                minZ = Math.min(minZ, pos.getZ());
                maxX = Math.max(maxX, pos.getX() + 1);
                maxY = Math.max(maxY, pos.getY() + 1);
                maxZ = Math.max(maxZ, pos.getZ() + 1);
            }
        }

        Vec3 cameraPos = event.getCamera().getPosition();
        AABB box = new AABB(minX, minY, minZ, maxX, maxY, maxZ)
                .move(-cameraPos.x, -cameraPos.y, -cameraPos.z)
                .inflate(0.002);

        Matrix4f identity = new Matrix4f();
        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();
        VertexConsumer builder = buffer.getBuffer(RenderType.lines());

        OverlayRenderer.drawBox(identity, builder, box, 1f, 1f, 1f, 1f);
        buffer.endBatch();
    }

}
