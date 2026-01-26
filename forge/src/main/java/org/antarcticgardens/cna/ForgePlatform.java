package org.antarcticgardens.cna;

import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.AssemblyOperatorBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.antarcticgardens.cna.compat.computercraft.CNAComputerCraftProxy;
import org.antarcticgardens.cna.content.nuclear.reactor.fuelacceptor.ReactorFuelAcceptorBlockEntity;
import org.antarcticgardens.cna.forge.ForgeRegistrar;
import org.antarcticgardens.cna.forge.compat.computercraft.ForgeComputerCraftProxy;
import org.antarcticgardens.cna.forge.compat.jei.ForgeJeiEnergisingSubcategory;
import org.antarcticgardens.cna.forge.content.electricity.generation.brushes.ForgeCarbonBrushesItem;
import org.antarcticgardens.cna.forge.content.energising.ForgeEnergisingBlockItem;
import org.antarcticgardens.cna.forge.content.heat.stirling.ForgeStirlingEngineItem;
import org.antarcticgardens.cna.forge.content.motor.ForgeMotorBlockItem;
import org.antarcticgardens.cna.forge.content.nuclear.reactor.fuelacceptor.ForgeReactorFuelAcceptorBlockEntity;
import org.antarcticgardens.cna.platform.PlatformRegistrar;

import java.util.function.Supplier;

public class ForgePlatform extends Platform {
    private final ForgeRegistrar registrationHelper = new ForgeRegistrar();
    
    @Override
    public PlatformRegistrar getRegistrar() {
        return registrationHelper;
    }

    @Override
    public void commonSetup(Runnable commonSetup) {
        FMLJavaModLoadingContext.get().getModEventBus().addListener((FMLCommonSetupEvent e) -> e.enqueueWork(commonSetup));
    }

    @Override
    public Object getEnergisingRecipeSubCategory() {
        return (Supplier<Supplier<SequencedAssemblySubCategory>>) () -> ForgeJeiEnergisingSubcategory::new;
    }

    @Override
    public CNAComputerCraftProxy getCNAComputerProxy() {
        return new ForgeComputerCraftProxy();
    }

    @Override
    public ReactorFuelAcceptorBlockEntity platformReactorFuelAcceptorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        return new ForgeReactorFuelAcceptorBlockEntity(type, pos, blockState);
    }

    @Override
    public AssemblyOperatorBlockItem platformEnergisingBlockItem(Block block, Item.Properties builder) {
        return new ForgeEnergisingBlockItem(block, builder);
    }

    @Override
    public BlockItem platformStirlingEngineItem(Block block, Item.Properties builder) {
        return new ForgeStirlingEngineItem(block, builder);
    }

    @Override
    public BlockItem platformCarbonBrushesItem(Block block, Item.Properties builder) {
        return new ForgeCarbonBrushesItem(block, builder);
    }

    @Override
    public BlockItem platformMotorBlockItem(Block block, Item.Properties builder) {
        return new ForgeMotorBlockItem(block, builder);
    }


}
