package org.antarcticgardens.cna.energy;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.antarcticgardens.cna.CreateNewAge;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

/**
 * Energy storage, in FE. Stands in for Antarctic Gardens' ESL, which has no 26.3 build: the same shape,
 * backed by Fabric's transfer transactions and block API lookup. Only New Age's own blocks provide it.
 */
public interface EnergyStorage {
    BlockApiLookup<EnergyStorage, Direction> SIDED =
            BlockApiLookup.get(Identifier.fromNamespaceAndPath(CreateNewAge.MOD_ID, "energy_storage"), EnergyStorage.class, Direction.class);

    long insert(long maxAmount, TransactionContext transaction);

    long extract(long maxAmount, TransactionContext transaction);

    long getStoredEnergy();

    long getCapacity();

    default boolean supportsInsertion() {
        return true;
    }

    default boolean supportsExtraction() {
        return true;
    }

    @Nullable
    static EnergyStorage findForBlock(Level level, BlockPos pos, @Nullable Direction side) {
        return SIDED.find(level, pos, side);
    }

    /** Items with their own energy storage come from other energy mods; none are in the pack. */
    @Nullable
    static EnergyStorage findForItem(ItemStack stack) {
        return null;
    }

    static <T extends BlockEntity> void registerForBlockEntity(BiFunction<? super T, Direction, EnergyStorage> provider,
                                                               BlockEntityType<T> type) {
        SIDED.registerForBlockEntity(provider, type);
    }
}
