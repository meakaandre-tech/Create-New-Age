package org.antarcticgardens.cna.content.heat.creative;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.WrenchableDirectionalBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.antarcticgardens.cna.CNABlockEntityTypes;

public class CreativeHeatGeneratorBlock extends WrenchableDirectionalBlock implements IBE<CreativeHeatGeneratorBlockEntity>, IWrenchable {

    public CreativeHeatGeneratorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getNearestLookingDirection();
        if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown())
            direction = direction.getOpposite();
        return defaultBlockState().setValue(FACING, direction.getOpposite());
    }

    @Override
    public Class<CreativeHeatGeneratorBlockEntity> getBlockEntityClass() {
        return CreativeHeatGeneratorBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CreativeHeatGeneratorBlockEntity> getBlockEntityType() {
        return CNABlockEntityTypes.CREATIVE_HEAT_GENERATOR.get();
    }
}
