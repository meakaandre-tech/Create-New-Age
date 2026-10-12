package org.antarcticgardens.cna.energy;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

/**
 * A plain energy buffer with optional insert and extract limits, as ESL's SimpleEnergyStorage.
 */
public class SimpleEnergyStorage extends SnapshotParticipant<Long> implements EnergyStorage {
    private long capacity;
    private long stored;
    private long maxInsert = Long.MAX_VALUE;
    private long maxExtract = Long.MAX_VALUE;
    private boolean supportsInsertion = true;
    private boolean supportsExtraction = true;
    private Runnable finalCommitCallback = () -> {};

    public SimpleEnergyStorage(long capacity) {
        if (capacity < 0)
            throw new IllegalArgumentException("SimpleEnergyStorage capacity can't be negative");
        this.capacity = capacity;
    }

    public SimpleEnergyStorage onFinalCommit(Runnable callback) {
        finalCommitCallback = callback;
        return this;
    }

    public SimpleEnergyStorage setCapacity(long capacity) {
        this.capacity = capacity;
        stored = Math.min(stored, capacity);
        return this;
    }

    public SimpleEnergyStorage setMaxInsert(long maxInsert) {
        this.maxInsert = maxInsert;
        return this;
    }

    public SimpleEnergyStorage setMaxExtract(long maxExtract) {
        this.maxExtract = maxExtract;
        return this;
    }

    public SimpleEnergyStorage setSupportsInsertion(boolean supportsInsertion) {
        this.supportsInsertion = supportsInsertion;
        return this;
    }

    public SimpleEnergyStorage setSupportsExtraction(boolean supportsExtraction) {
        this.supportsExtraction = supportsExtraction;
        return this;
    }

    public SimpleEnergyStorage setStoredEnergy(long amount) {
        stored = Math.max(0, Math.min(amount, capacity));
        return this;
    }

    @Override
    public long insert(long maxAmount, TransactionContext transaction) {
        if (!supportsInsertion || maxAmount <= 0)
            return 0;
        long inserted = Math.min(Math.min(maxAmount, maxInsert), capacity - stored);
        if (inserted > 0) {
            updateSnapshots(transaction);
            stored += inserted;
        }
        return inserted;
    }

    @Override
    public long extract(long maxAmount, TransactionContext transaction) {
        if (!supportsExtraction || maxAmount <= 0)
            return 0;
        long extracted = Math.min(Math.min(maxAmount, maxExtract), stored);
        if (extracted > 0) {
            updateSnapshots(transaction);
            stored -= extracted;
        }
        return extracted;
    }

    /** Changes the stored energy outside a transaction, ignoring the insert limit. */
    public long internalInsert(long amount, boolean simulate) {
        long inserted = Math.max(0, Math.min(amount, capacity - stored));
        if (!simulate)
            stored += inserted;
        return inserted;
    }

    /** Changes the stored energy outside a transaction, ignoring the extract limit. */
    public long internalExtract(long amount, boolean simulate) {
        long extracted = Math.max(0, Math.min(amount, stored));
        if (!simulate)
            stored -= extracted;
        return extracted;
    }

    @Override
    public long getStoredEnergy() {
        return stored;
    }

    @Override
    public long getCapacity() {
        return capacity;
    }

    @Override
    public boolean supportsInsertion() {
        return supportsInsertion;
    }

    @Override
    public boolean supportsExtraction() {
        return supportsExtraction;
    }

    @Override
    public Long createSnapshot() {
        return stored;
    }

    @Override
    public void restoreSnapshot(Long snapshot) {
        stored = snapshot;
    }

    @Override
    protected void onFinalCommit() {
        finalCommitCallback.run();
    }
}
