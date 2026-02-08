package org.antarcticgardens.cna.forge.compat.computercraft;

import com.simibubi.create.compat.computercraft.AbstractComputerBehaviour;
import com.simibubi.create.compat.computercraft.implementation.peripherals.SyncedPeripheral;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import org.antarcticgardens.cna.compat.computercraft.peripherals.CarbonBrushesBlockEntityPeripheral;
import org.antarcticgardens.cna.compat.computercraft.peripherals.EnergiserBlockEntityPeripheral;
import org.antarcticgardens.cna.compat.computercraft.peripherals.MotorBlockEntityPeripheral;
import org.antarcticgardens.cna.content.electricity.generation.brushes.CarbonBrushesBlockEntity;
import org.antarcticgardens.cna.content.energising.EnergiserBlockEntity;
import org.antarcticgardens.cna.content.motor.MotorBlockEntity;

import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;

public class ForgeComputerBehaviour extends AbstractComputerBehaviour {
    protected static final Capability<IPeripheral> PERIPHERAL_CAPABILITY = CapabilityManager
            .get(new CapabilityToken<>() {
            });

    LazyOptional<SyncedPeripheral<?>> peripheral;
    NonNullSupplier<SyncedPeripheral<?>> peripheralSupplier;

    public ForgeComputerBehaviour(SmartBlockEntity be) {
        super(be);
        this.peripheralSupplier = getPeripheralSupplier(be);
    }

    public static NonNullSupplier<SyncedPeripheral<?>> getPeripheralSupplier(SmartBlockEntity be) {
        if (be instanceof EnergiserBlockEntity energiserBlockEntity)
            return () -> new EnergiserBlockEntityPeripheral(energiserBlockEntity);
        if (be instanceof MotorBlockEntity motorBlockEntity)
            return () -> new MotorBlockEntityPeripheral(motorBlockEntity);
        if (be instanceof CarbonBrushesBlockEntity carbonBrushesBlockEntity)
            return () -> new CarbonBrushesBlockEntityPeripheral(carbonBrushesBlockEntity);

        throw new IllegalArgumentException(
                "No peripheral registered for " + BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(be.getType()));
    }

    @Override
    public <T> boolean isPeripheralCap(Capability<T> cap) {
        return cap == PERIPHERAL_CAPABILITY;
    }

    @Override
    public <T> LazyOptional<T> getPeripheralCapability() {
        if (peripheral == null || !peripheral.isPresent())
            peripheral = LazyOptional.of(peripheralSupplier);
        return peripheral.cast();
    }
}
