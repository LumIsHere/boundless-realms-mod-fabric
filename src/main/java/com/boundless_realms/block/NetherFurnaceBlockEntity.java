package com.boundless_realms.block;

import com.boundless_realms.recipe.ModRecipeTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.FurnaceScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class NetherFurnaceBlockEntity extends AbstractFurnaceBlockEntity {
    public NetherFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.NETHER_FURNACE_ENTITY, pos, state, ModRecipeTypes.NETHER_SMELTING);
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new FurnaceScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    @Override
    protected Text getContainerName() {
        return Text.translatable("container.nether_furnace");
    }
}
