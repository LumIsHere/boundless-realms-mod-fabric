package com.boundless_realms.item;

import net.minecraft.item.Item;

public class GraphicsCardItem extends Item {
    private final int hashrate;

    public GraphicsCardItem(Settings settings, int hashrate) {
        super(settings);
        this.hashrate = hashrate;
    }

    public int getHashrate() {
        return hashrate;
    }
}
