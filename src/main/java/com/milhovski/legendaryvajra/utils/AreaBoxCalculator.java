package com.milhovski.legendaryvajra.utils;

import com.milhovski.legendaryvajra.common.tag.CTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import org.jetbrains.annotations.Nullable;

public class AreaBoxCalculator {

    @Nullable
    public static AABB get3x3Box(Level level, BlockPos origin, Direction side) {
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;

        boolean hasValidBlock = false;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos pos = switch (side.getAxis()) {
                    case Y -> origin.offset(dx, 0, dz);
                    case X -> origin.offset(0, dz, dx);
                    case Z -> origin.offset(dx, dz, 0);
                };

                BlockState state = level.getBlockState(pos);

                if (!state.isAir() && state.is(CTags.Blocks.VAJRA_MINEABLE)) {
                    hasValidBlock = true;

                    minX = Math.min(minX, pos.getX());
                    minY = Math.min(minY, pos.getY());
                    minZ = Math.min(minZ, pos.getZ());
                    maxX = Math.max(maxX, pos.getX() + 1);
                    maxY = Math.max(maxY, pos.getY() + 1);
                    maxZ = Math.max(maxZ, pos.getZ() + 1);
                }
            }
        }

        if (!hasValidBlock) return null;

        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }

}
