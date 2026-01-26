package org.antarcticgardens.cna;

import com.simibubi.create.content.processing.AssemblyOperatorBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.antarcticgardens.cna.compat.computercraft.CNAComputerCraftProxy;
import org.antarcticgardens.cna.content.nuclear.reactor.fuelacceptor.ReactorFuelAcceptorBlockEntity;
import org.antarcticgardens.cna.platform.PlatformRegistrar;

public abstract class Platform {
    public abstract PlatformRegistrar getRegistrar();
    public abstract void commonSetup(Runnable commonSetup);
    public abstract Object getEnergisingRecipeSubCategory();
    public abstract CNAComputerCraftProxy getCNAComputerProxy();
    public abstract ReactorFuelAcceptorBlockEntity platformReactorFuelAcceptorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState);
    public abstract AssemblyOperatorBlockItem platformEnergisingBlockItem(Block block, Item.Properties builder);
    public abstract BlockItem platformStirlingEngineItem(Block block, Item.Properties builder);
    public abstract BlockItem platformCarbonBrushesItem(Block block, Item.Properties builder);
    public abstract BlockItem platformMotorBlockItem(Block block, Item.Properties builder);
}
