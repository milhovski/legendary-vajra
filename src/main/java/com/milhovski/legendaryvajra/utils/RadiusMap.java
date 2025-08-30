package com.milhovski.legendaryvajra.utils;

import com.milhovski.legendaryvajra.Config;
import com.milhovski.legendaryvajra.init.CItems;
import net.minecraft.world.item.Item;

import java.util.Map;

public class RadiusMap {

    public static Map<Item, Integer> getVajraRadius() {
        return Map.of(CItems.VAJRA.get(), Config.radiusVajra);
    }

}
