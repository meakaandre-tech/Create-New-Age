package org.antarcticgardens.cna;

import com.simibubi.create.compat.recipeViewerCommon.SequencedAssemblySubCategoryType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.antarcticgardens.cna.content.electricity.generation.brushes.CarbonBrushesItem;
import org.antarcticgardens.cna.content.energising.EnergisingBlockItem;
import org.antarcticgardens.cna.content.heat.stirling.StirlingEngineItem;
import org.antarcticgardens.cna.content.motor.MotorBlockItem;
import org.antarcticgardens.cna.content.nuclear.reactor.fuelacceptor.ReactorFuelAcceptorBlockEntity;
import org.antarcticgardens.cna.fabric.FabricRegistrar;
import org.antarcticgardens.cna.fabric.compat.emi.EmiEnergisingSubcategory;
import org.antarcticgardens.cna.fabric.compat.jei.FabricJeiEnergisingSubcategory;
import org.antarcticgardens.cna.fabric.compat.rei.ReiEnergiserSubcategory;
import org.antarcticgardens.cna.fabric.content.electricity.generation.brushes.FabricCarbonBrushesItem;
import org.antarcticgardens.cna.fabric.content.energising.FabricEnergisingBlockItem;
import org.antarcticgardens.cna.fabric.content.heat.stirling.FabricStirlingEngineItem;
import org.antarcticgardens.cna.fabric.content.motor.FabricMotorBlockItem;
import org.antarcticgardens.cna.fabric.content.reator.fuelacceptor.FabricReactorFuelAcceptorBlockEntity;
import org.antarcticgardens.cna.platform.PlatformRegistrar;

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

    @Override
    public EnergisingBlockItem platformEnergisingBlockItem(Block block, Item.Properties builder) {
        return new FabricEnergisingBlockItem(block, builder);
    }

    @Override
    public StirlingEngineItem platformStirlingEngineItem(Block block, Item.Properties builder) {
        return new FabricStirlingEngineItem(block, builder);
    }

    @Override
    public CarbonBrushesItem platformCarbonBrushesItem(Block block, Item.Properties builder) {
        return new FabricCarbonBrushesItem(block, builder);
    }

    @Override
    public MotorBlockItem platformMotorBlockItem(Block block, Item.Properties builder) {
        return new FabricMotorBlockItem(block, builder);
    }
}
