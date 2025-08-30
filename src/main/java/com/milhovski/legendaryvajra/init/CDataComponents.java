package com.milhovski.legendaryvajra.init;

import com.milhovski.legendaryvajra.LegendaryVajra;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CDataComponents {

    public static final DeferredRegister<DataComponentType<?>> COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, LegendaryVajra.MOD_ID);

    public static final Supplier<DataComponentType<Integer>> ENERGY = COMPONENTS.register(
            "energy",
            () -> DataComponentType.<Integer>builder()
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT)
                    .build()
    );

    public static final Supplier<DataComponentType<Boolean>> SILK_MODE = COMPONENTS.register(
            "silk_mode",
            () -> DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build()
    );

    public static final Supplier<DataComponentType<Integer>> RADIUS_MODE = COMPONENTS.register(
            "radius_mode",
            () -> DataComponentType.<Integer>builder()
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT)
                    .build()
    );

    public static void init(IEventBus bus) {
        COMPONENTS.register(bus);
    }

}
