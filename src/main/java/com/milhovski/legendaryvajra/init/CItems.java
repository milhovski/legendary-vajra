package com.milhovski.legendaryvajra.init;

import com.milhovski.legendaryvajra.LegendaryVajra;
import com.milhovski.legendaryvajra.common.item.NeoVajra;
import com.milhovski.legendaryvajra.common.item.Vajra;
import com.milhovski.legendaryvajra.common.tier.EToolMaterials;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(LegendaryVajra.MOD_ID);

    public static final DeferredItem<Item> VAJRA = ITEMS.register("vajra",
            () -> new Vajra(EToolMaterials.VAJRA, new Item.Properties()
                    .stacksTo(1).rarity(Rarity.RARE),
                    Capabilities.EnergyStorage.ITEM));

    public static final DeferredItem<Item> NEOVAJRA = ITEMS.register("neovajra",
            () -> new NeoVajra(EToolMaterials.NEOVAJRA, new Item.Properties()
                    .stacksTo(1).rarity(Rarity.EPIC),
                    Capabilities.EnergyStorage.ITEM));

    public static void init(IEventBus bus) { ITEMS.register(bus); }

}
