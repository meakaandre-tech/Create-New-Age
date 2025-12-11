package org.antarcticgardens.cna;

import com.tterrag.registrate.builders.BlockEntityBuilder;
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
import org.antarcticgardens.cna.platform.PlatformRegistrar;

public abstract class Platform {
    public abstract PlatformRegistrar getRegistrar();
    public abstract void commonSetup(Runnable commonSetup);
    public abstract Object getEnergisingRecipeSubCategory();
    public abstract ReactorFuelAcceptorBlockEntity platformReactorFuelAcceptorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState);
    public abstract EnergisingBlockItem platformEnergisingBlockItem(Block block, Item.Properties builder);
    public abstract StirlingEngineItem platformStirlingEngineItem(Block block, Item.Properties builder);
    public abstract CarbonBrushesItem platformCarbonBrushesItem(Block block, Item.Properties builder);
    public abstract MotorBlockItem platformMotorBlockItem(Block block, Item.Properties builder);
}
