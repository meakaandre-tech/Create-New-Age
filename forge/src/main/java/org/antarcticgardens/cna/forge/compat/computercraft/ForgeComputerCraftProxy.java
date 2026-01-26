package org.antarcticgardens.cna.forge.compat.computercraft;

import com.simibubi.create.compat.computercraft.implementation.ComputerBehaviour;
import org.antarcticgardens.cna.compat.computercraft.CNAComputerCraftProxy;

public class ForgeComputerCraftProxy extends CNAComputerCraftProxy {
    @Override
    public void registerWithDependency() {
        computerFactory = ForgeComputerBehaviour::new;
        ComputerBehaviour.registerItemDetailProviders();
    }
}
