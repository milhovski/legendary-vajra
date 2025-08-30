package com.milhovski.legendaryvajra;

import com.milhovski.legendaryvajra.common.energy.REnergyStorage;
import com.milhovski.legendaryvajra.init.CCreativeTab;
import com.milhovski.legendaryvajra.init.CDataComponents;
import com.milhovski.legendaryvajra.init.CItems;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@Mod(LegendaryVajra.MOD_ID)
public class LegendaryVajra {

    public static final String MOD_ID = "legendaryvajra";

    public LegendaryVajra(IEventBus bus, ModContainer container) {
        bus.addListener(this::registerCaps);
        CDataComponents.init(bus);
        CItems.init(bus);
        CCreativeTab.init(bus);
    }

    public void registerCaps(RegisterCapabilitiesEvent event) {
        event.registerItem(
                Capabilities.EnergyStorage.ITEM,
                (stack, ctx) -> new REnergyStorage(stack, 7_000_000, Integer.MAX_VALUE, 1000),
                CItems.VAJRA.get()
        );
    }

}
