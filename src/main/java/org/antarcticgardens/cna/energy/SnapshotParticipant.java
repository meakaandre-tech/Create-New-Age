package org.antarcticgardens.cna.energy;

/**
 * Fabric's snapshot participant with public snapshot access, which New Age's network snapshots need
 * to save and restore the storages of every block on a network.
 */
public abstract class SnapshotParticipant<T> extends net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant<T> {
    @Override
    public abstract T createSnapshot();

    public abstract void restoreSnapshot(T snapshot);

    @Override
    protected final void readSnapshot(T snapshot) {
        restoreSnapshot(snapshot);
    }
}
