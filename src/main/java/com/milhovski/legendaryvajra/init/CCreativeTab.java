package com.milhovski.legendaryvajra.init;

import com.milhovski.legendaryvajra.LegendaryVajra;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CCreativeTab {

    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, LegendaryVajra.MOD_ID);

    public static final Supplier<CreativeModeTab> LEGENDARYVAJRA_TAB = TABS.register("legendaryvajra_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.legendaryvajra"))
            .icon(() -> new ItemStack(CItems.VAJRA.get()))
            .displayItems((CreativeModeTab.ItemDisplayParameters params, CreativeModeTab.Output output) -> {
                CItems.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
            }).build()
    );

    public static void init(IEventBus bus) { TABS.register(bus); }

}
