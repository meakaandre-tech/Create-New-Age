package org.antarcticgardens.cna.fabric.compat.computercraft;

import com.simibubi.create.compat.computercraft.AbstractComputerBehaviour;
import com.simibubi.create.compat.computercraft.implementation.ComputerBehaviour;
import com.simibubi.create.compat.computercraft.implementation.peripherals.SyncedPeripheral;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.Level;
import org.antarcticgardens.cna.CreateNewAge;
import org.antarcticgardens.cna.compat.computercraft.peripherals.CarbonBrushesBlockEntityPeripheral;
import org.antarcticgardens.cna.compat.computercraft.peripherals.EnergiserBlockEntityPeripheral;
import org.antarcticgardens.cna.compat.computercraft.peripherals.MotorBlockEntityPeripheral;
import org.antarcticgardens.cna.content.electricity.generation.brushes.CarbonBrushesBlockEntity;
import org.antarcticgardens.cna.content.energising.EnergiserBlockEntity;
import org.antarcticgardens.cna.content.motor.MotorBlockEntity;
import org.jspecify.annotations.Nullable;

public class FabricComputerBehaviour extends AbstractComputerBehaviour {

    @Nullable
    public static IPeripheral peripheralProvider(Level level, BlockPos blockPos) {
        AbstractComputerBehaviour behavior = BlockEntityBehaviour.get(level, blockPos, AbstractComputerBehaviour.TYPE);
        if (behavior instanceof FabricComputerBehaviour real)
            return real.getPeripheral();
        return null;
    }

    SyncedPeripheral<?> peripheral;

    public FabricComputerBehaviour(SmartBlockEntity te) {
        super(te);
        this.peripheral = getPeripheralFor(te);
    }

    public static SyncedPeripheral<?> getPeripheralFor(SmartBlockEntity be) {
        if (be instanceof EnergiserBlockEntity energiserBlockEntity)
            return new EnergiserBlockEntityPeripheral(energiserBlockEntity);
        if (be instanceof MotorBlockEntity motorBlockEntity)
            return new MotorBlockEntityPeripheral(motorBlockEntity);
        if (be instanceof CarbonBrushesBlockEntity carbonBrushesBlockEntity)
            return new CarbonBrushesBlockEntityPeripheral(carbonBrushesBlockEntity);

        throw new IllegalArgumentException(
                "No peripheral registered for " + BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(be.getType()));
    }

    @Override
    @Nullable
    public <T> T getPeripheral() {
        //noinspection unchecked
        return (T) peripheral;
    }
}
