package org.antarcticgardens.cna.forge.content.nuclear.reactor.fuelacceptor;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.antarcticgardens.cna.content.nuclear.reactor.fuelacceptor.ReactorFuelAcceptorBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ForgeReactorFuelAcceptorBlockEntity extends ReactorFuelAcceptorBlockEntity {
    private final LazyOptional<IItemHandler> capability;

    public ForgeReactorFuelAcceptorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        capability = LazyOptional.of(FuelAcceptorInventoryHandler::new);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        capability.invalidate();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (isItemHandlerCap(cap))
            return capability.cast();
        return super.getCapability(cap, side);
    }

    private class FuelAcceptorInventoryHandler extends InvWrapper {

        public FuelAcceptorInventoryHandler() {
            super(container);
        }
    }
}
