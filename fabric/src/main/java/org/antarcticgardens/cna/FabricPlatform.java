package org.antarcticgardens.cna;

import com.simibubi.create.compat.computercraft.AbstractComputerBehaviour;
import com.simibubi.create.compat.recipeViewerCommon.SequencedAssemblySubCategoryType;
import com.simibubi.create.content.processing.AssemblyOperatorBlockItem;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.antarcticgardens.cna.content.nuclear.reactor.fuelacceptor.ReactorFuelAcceptorBlockEntity;
import org.antarcticgardens.cna.fabric.FabricRegistrar;
import org.antarcticgardens.cna.fabric.compat.computercraft.FabricComputerBehaviour;
import org.antarcticgardens.cna.fabric.compat.emi.EmiEnergisingSubcategory;
import org.antarcticgardens.cna.fabric.compat.jei.FabricJeiEnergisingSubcategory;
import org.antarcticgardens.cna.fabric.compat.rei.ReiEnergiserSubcategory;
import org.antarcticgardens.cna.fabric.content.electricity.generation.brushes.FabricCarbonBrushesItem;
import org.antarcticgardens.cna.fabric.content.energising.FabricEnergisingBlockItem;
import org.antarcticgardens.cna.fabric.content.heat.stirling.FabricStirlingEngineItem;
import org.antarcticgardens.cna.fabric.content.motor.FabricMotorBlockItem;
import org.antarcticgardens.cna.fabric.content.reator.fuelacceptor.FabricReactorFuelAcceptorBlockEntity;
import org.antarcticgardens.cna.platform.PlatformRegistrar;

import java.util.function.Function;

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
    public Function<SmartBlockEntity, ? extends AbstractComputerBehaviour> getCNAComputerBehaviour() {
        return FabricComputerBehaviour::new;
    }

    @Override
    public ReactorFuelAcceptorBlockEntity platformReactorFuelAcceptorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        return new FabricReactorFuelAcceptorBlockEntity(type, pos, blockState);
    }

    @Override
    public AssemblyOperatorBlockItem platformEnergisingBlockItem(Block block, Item.Properties builder) {
        return new FabricEnergisingBlockItem(block, builder);
    }

    @Override
    public BlockItem platformStirlingEngineItem(Block block, Item.Properties builder) {
        return new FabricStirlingEngineItem(block, builder);
    }

    @Override
    public BlockItem platformCarbonBrushesItem(Block block, Item.Properties builder) {
        return new FabricCarbonBrushesItem(block, builder);
    }

    @Override
    public BlockItem platformMotorBlockItem(Block block, Item.Properties builder) {
        return new FabricMotorBlockItem(block, builder);
    }
}
