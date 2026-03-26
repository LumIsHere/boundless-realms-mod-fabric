package com.boundless_realms.item;

import net.minecraft.world.item.Item;

public class GraphicsCardItem extends Item {
    private final int hashrate;

    public GraphicsCardItem(Properties settings, int hashrate) {
        super(settings);
        this.hashrate = hashrate;
    }

    public int getHashrate() {
        return hashrate;
    }
}
