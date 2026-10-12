package org.antarcticgardens.cna.content.electricity.generation.brushes;

import com.zurrtum.create.content.kinetics.base.DirectionalKineticBlock;
import com.zurrtum.create.content.kinetics.base.KineticBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.antarcticgardens.cna.config.CNAConfig;
import org.antarcticgardens.cna.content.electricity.generation.coil.GeneratorCoilBlock;
import org.antarcticgardens.cna.content.electricity.generation.coil.GeneratorCoilBlockEntity;
import org.antarcticgardens.cna.energy.EnergyHelper;
import org.antarcticgardens.cna.energy.SimpleEnergyStorage;

/** Collects the energy of the generator coils in a line on both sides and pushes it into neighbouring blocks. */
public class CarbonBrushesBlockEntity extends KineticBlockEntity {
    private final SimpleEnergyStorage storage;

    private int lastOutput = 0;
    private int syncOut = 0;

    public CarbonBrushesBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        storage = new SimpleEnergyStorage(0).setSupportsInsertion(false);
        setLazyTickRate(20);
    }

    @Override
    protected void write(ValueOutput compound, boolean clientPacket) {
        compound.putInt("lastOutput", lastOutput);
        super.write(compound, clientPacket);
    }

    @Override
    protected void read(ValueInput compound, boolean clientPacket) {
        lastOutput = compound.getIntOr("lastOutput", 0);
        super.read(compound, clientPacket);
    }

    public int getLastOutput() {
        return lastOutput;
    }

    public SimpleEnergyStorage getEnergyStorage() {
        return storage;
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide())
            return;
        Direction facing = getBlockState().getValue(DirectionalKineticBlock.FACING);

        storage.setCapacity(lastOutput * 20L);

        int coilsLeft = CNAConfig.getServer().maxCoils.get();
        lastOutput = 0;
        coilsLeft = processCoil(worldPosition, facing, coilsLeft);
        processCoil(worldPosition, facing.getOpposite(), coilsLeft);

        try (Transaction t = Transaction.openOuter()) {
            EnergyHelper.insertToSurrounding(storage, getBlockPos(), getLevel(), storage.getStoredEnergy(), t);
            t.commit();
        }
    }

    @Override
    public void lazyTick() {
        if (level == null || level.isClientSide())
            return;
        if (syncOut > 0) {
            syncOut = 0;
            setChanged();
            sendData();
        }
    }

    private int processCoil(BlockPos pos, Direction dir, int left) {
        if (left <= 0)
            return 0;

        pos = pos.relative(dir);

        if (level.getBlockEntity(pos) instanceof GeneratorCoilBlockEntity coil
                && coil.getBlockState().getValue(GeneratorCoilBlock.AXIS).test(dir)) {
            int energy = coil.takeGeneratedEnergy();
            lastOutput += energy;
            syncOut += energy;
            storage.internalInsert(energy, false);
            return processCoil(pos, dir, left - 1);
        }
        return left;
    }
}
