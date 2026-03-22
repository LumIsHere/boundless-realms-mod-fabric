package com.boundless_realms.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NetherFurnaceBlock extends FurnaceBlock {
    public NetherFurnaceBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new NetherFurnaceBlockEntity(pos, state);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient()) {
            return ActionResult.SUCCESS;
        } else {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof NetherFurnaceBlockEntity) {
                player.openHandledScreen((NetherFurnaceBlockEntity)blockEntity);
                player.incrementStat(Stats.INTERACT_WITH_FURNACE);
            }
            return ActionResult.CONSUME;
        }
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.NETHER_FURNACE_ENTITY, (world1, pos, state1, blockEntity) -> {
            if (world1.getTime() % 20 == 0) System.out.println("Nether Furnace is Ticking!");

            if (world1 instanceof ServerWorld serverWorld) {
                AbstractFurnaceBlockEntity.tick(serverWorld, pos, state1, blockEntity);
            }
        });
    }
}
