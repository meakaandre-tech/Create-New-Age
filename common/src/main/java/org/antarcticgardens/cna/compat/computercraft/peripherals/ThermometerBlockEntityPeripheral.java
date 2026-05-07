package org.antarcticgardens.cna.compat.computercraft.peripherals;

import com.simibubi.create.compat.computercraft.implementation.peripherals.SyncedPeripheral;
import dan200.computercraft.api.lua.LuaFunction;
import org.antarcticgardens.cna.content.heat.thermometer.ThermometerBlockEntity;

public class ThermometerBlockEntityPeripheral extends SyncedPeripheral<ThermometerBlockEntity> {
    public ThermometerBlockEntityPeripheral(ThermometerBlockEntity blockEntity) {
        super(blockEntity);
    }

    @LuaFunction(mainThread = true)
    public final int getHeat() {
        return (int) blockEntity.getHeat();
    }

    @Override
    public String getType() {
        return "CNA_thermometer";
    }
}
