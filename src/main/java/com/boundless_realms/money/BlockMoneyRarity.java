package com.boundless_realms.money;

import net.minecraft.util.RandomSource;

public enum BlockMoneyRarity {
    COMMON(1, 3, 0.20F),
    UNCOMMON(4, 7, 0.32F),
    RARE(8, 12, 0.44F),
    EPIC(13, 17, 0.56F),
    LEGENDARY(18, 21, 0.68F),
    MYTHIC(22, 25, 0.80F);

    private static final float CROSS_RARITY_CHANCE = 0.05F;
    private static final BlockMoneyRarity[] VALUES = values();

    private final int minimumAmount;
    private final int maximumAmount;
    private final float dropChance;

    BlockMoneyRarity(int minimumAmount, int maximumAmount, float dropChance) {
        this.minimumAmount = minimumAmount;
        this.maximumAmount = maximumAmount;
        this.dropChance = dropChance;
    }

    public boolean shouldDropMoney(RandomSource random) {
        return random.nextFloat() < dropChance;
    }

    public int rollAmount(RandomSource random) {
        BlockMoneyRarity effectiveRarity = this;
        if (random.nextFloat() < CROSS_RARITY_CHANCE) {
            int offset = 1 + random.nextInt(VALUES.length - 1);
            effectiveRarity = VALUES[(ordinal() + offset) % VALUES.length];
        }

        return effectiveRarity.rollWithinRange(random);
    }

    private int rollWithinRange(RandomSource random) {
        int range = maximumAmount - minimumAmount + 1;
        int variation = (random.nextInt(range) + random.nextInt(range)) / 2;
        return minimumAmount + variation;
    }
}
