package org.antarcticgardens.cna.fabric.content.reator.fuelacceptor;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.antarcticgardens.cna.content.nuclear.reactor.fuelacceptor.ReactorFuelAcceptorBlockEntity;

import javax.annotation.Nullable;

public class FabricReactorFuelAcceptorBlockEntity extends ReactorFuelAcceptorBlockEntity implements SidedStorageBlockEntity {
    private FuelAcceptorItemHandler itemHandler;

    public FabricReactorFuelAcceptorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        itemHandler = new FuelAcceptorItemHandler(this);
    }

    @Nullable
    @Override
    public Storage<ItemVariant> getItemStorage(@Nullable Direction face) {
        return itemHandler;
    }

    private class FuelAcceptorItemHandler extends SingleVariantStorage<ItemVariant> {
        private final ReactorFuelAcceptorBlockEntity blockEntity;

        FuelAcceptorItemHandler(ReactorFuelAcceptorBlockEntity blockEntity) {
            this.blockEntity = blockEntity;
        }

        @Override
        protected ItemVariant getBlankVariant() {
            return ItemVariant.blank();
        }

        @Override
        protected long getCapacity(ItemVariant itemVariant) {
            return (long) Math.min(64, variant.getItem().getMaxStackSize()) * blockEntity.container.getContainerSize();
        }
    }
}
