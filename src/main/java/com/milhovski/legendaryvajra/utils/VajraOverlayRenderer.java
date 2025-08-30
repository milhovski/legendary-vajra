package com.milhovski.legendaryvajra.utils;

import com.milhovski.legendaryvajra.LegendaryVajra;
import com.milhovski.legendaryvajra.common.item.Vajra;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.neoforged.fml.common.EventBusSubscriber;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
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

        AABB area = AreaBoxCalculator.get3x3Box(mc.level, origin, side);
        if (area == null) return;

        Vec3 cameraPos = event.getCamera().getPosition();
        AABB renderBox = area.move(-cameraPos.x, -cameraPos.y, -cameraPos.z).inflate(0.002);

        Matrix4f pose = new Matrix4f();
        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();
        VertexConsumer builder = buffer.getBuffer(RenderType.lines());

        OverlayRenderer.drawBox(pose, builder, renderBox, 1f, 1f, 1f, 1f);

        buffer.endBatch();
    }

}
