package org.antarcticgardens.cna.registry;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Small stand-in for Registrate's BlockEntityEntry. Entries start empty and are filled in when the block
 * entity types register, so blocks can hold them without a static initialisation cycle.
 */
public final class BlockEntityEntry<T extends BlockEntity> {
    private BlockEntityType<T> type;

    public void set(BlockEntityType<T> type) {
        this.type = type;
    }

    public BlockEntityType<T> get() {
        return type;
    }

    public T create(BlockPos pos, BlockState state) {
        return type.create(pos, state);
    }

    public boolean is(BlockEntity blockEntity) {
        return blockEntity != null && blockEntity.getType() == type;
    }
}
