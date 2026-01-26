package org.antarcticgardens.cna.compat.computercraft;

import com.simibubi.create.compat.Mods;
import com.simibubi.create.compat.computercraft.AbstractComputerBehaviour;
import com.simibubi.create.compat.computercraft.FallbackComputerBehaviour;
import com.simibubi.create.compat.computercraft.implementation.ComputerBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import org.antarcticgardens.cna.CreateNewAge;

import java.util.function.Function;

public abstract class CNAComputerCraftProxy {
    public static void register() {
        fallbackFactory = FallbackComputerBehaviour::new;
        Mods.COMPUTERCRAFT.executeIfInstalled(() -> CreateNewAge.getInstance().getPlatform().getCNAComputerProxy()::registerWithDependency);
    }

    public abstract void registerWithDependency();

    protected static Function<SmartBlockEntity, ? extends AbstractComputerBehaviour> fallbackFactory;
    protected static Function<SmartBlockEntity, ? extends AbstractComputerBehaviour> computerFactory;

    public static AbstractComputerBehaviour behaviour(SmartBlockEntity sbe) {
        if (computerFactory == null)
            return fallbackFactory.apply(sbe);
        return computerFactory.apply(sbe);
    }
}
