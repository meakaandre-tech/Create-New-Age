package org.antarcticgardens.cna.energy;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public final class EnergyHelper {
    private EnergyHelper() {
    }

    /** Moves up to {@code maxAmount} from one storage to another and returns how much moved. */
    public static long moveEnergy(EnergyStorage from, EnergyStorage to, long maxAmount, TransactionContext transaction) {
        if (from == null || to == null || maxAmount <= 0)
            return 0;
        long moved;
        try (var test = transaction.openNested()) {
            long available = from.extract(maxAmount, test);
            moved = to.insert(available, test);
            test.abort();
        }
        if (moved <= 0)
            return 0;
        from.extract(moved, transaction);
        to.insert(moved, transaction);
        return moved;
    }

    /** Pushes energy into the storages on all six sides and returns how much went out. */
    public static long insertToSurrounding(EnergyStorage from, BlockPos pos, Level level, long maxAmount, TransactionContext transaction) {
        long moved = 0;
        for (Direction direction : Direction.values()) {
            if (moved >= maxAmount)
                break;
            EnergyStorage target = EnergyStorage.findForBlock(level, pos.relative(direction), direction.getOpposite());
            if (target == null || target == from)
                continue;
            moved += moveEnergy(from, target, maxAmount - moved, transaction);
        }
        return moved;
    }
}
