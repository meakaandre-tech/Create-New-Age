package org.antarcticgardens.cna.fabric.compat.computercraft;

import com.simibubi.create.compat.computercraft.implementation.ComputerBehaviour;
import dan200.computercraft.api.peripheral.PeripheralLookup;
import org.antarcticgardens.cna.compat.computercraft.CNAComputerCraftProxy;

public class FabricComputerCraftProxy extends CNAComputerCraftProxy {

    @Override
    public void registerWithDependency() {
        computerFactory = FabricComputerBehaviour::new;
        ComputerBehaviour.registerItemDetailProviders();

        PeripheralLookup.get().registerFallback((level, pos, state, be, face) -> FabricComputerBehaviour.peripheralProvider(level, pos));
    }
}
