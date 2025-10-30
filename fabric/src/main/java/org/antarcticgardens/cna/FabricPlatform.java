package org.antarcticgardens.cna;

import com.simibubi.create.compat.recipeViewerCommon.SequencedAssemblySubCategoryType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.antarcticgardens.cna.content.nuclear.reactor.fuelacceptor.ReactorFuelAcceptorBlockEntity;
import org.antarcticgardens.cna.fabric.FabricRegistrar;
import org.antarcticgardens.cna.fabric.compat.emi.EmiEnergisingSubcategory;
import org.antarcticgardens.cna.fabric.compat.jei.FabricJeiEnergisingSubcategory;
import org.antarcticgardens.cna.fabric.compat.rei.ReiEnergiserSubcategory;
import org.antarcticgardens.cna.fabric.content.reator.fuelacceptor.FabricReactorFuelAcceptorBlockEntity;
import org.antarcticgardens.cna.platform.PlatformRegistrar;

import java.util.function.Consumer;

public class FabricPlatform extends Platform {
    private final FabricRegistrar registrar = new FabricRegistrar();

    @Override
    public PlatformRegistrar getRegistrar() {
        return registrar;
    }

    @Override
    public void commonSetup(Runnable commonSetup) {
        commonSetup.run();
    }

    @Override
    public Object getEnergisingRecipeSubCategory() {
        return new SequencedAssemblySubCategoryType(
                () -> FabricJeiEnergisingSubcategory::new,
                () -> ReiEnergiserSubcategory::new,
                () -> EmiEnergisingSubcategory::new
        );
    }

    @Override
    public ReactorFuelAcceptorBlockEntity platformReactorFuelAcceptorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        return new FabricReactorFuelAcceptorBlockEntity(type, pos, blockState);
    }
}
