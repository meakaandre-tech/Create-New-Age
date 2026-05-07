package org.antarcticgardens.cna.content.heat.thermometer;

import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.antarcticgardens.cna.CNABlockEntityTypes;
import org.antarcticgardens.cna.content.heat.pipe.HeatPipeBlock;
import org.antarcticgardens.cna.content.motor.MotorBlock;
import org.jspecify.annotations.Nullable;

public class ThermometerBlock extends Block implements IBE<ThermometerBlockEntity> {

    public ThermometerBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.FACING);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getClickedFace().getOpposite();
        BlockPos pos = context.getClickedPos();
        if (context.getPlayer() != null && context.getPlayer().isCrouching()) {
            direction = direction.getOpposite();
        } else if (!(context.getLevel().getBlockState(pos.relative(direction)).getBlock() instanceof HeatPipeBlock)) {
            for (Direction d : Direction.values()) {
                if (context.getLevel().getBlockState(pos.relative(d)).getBlock() instanceof HeatPipeBlock) {
                    direction = d;
                    break;
                }
            }
        }

        return this.defaultBlockState().setValue(BlockStateProperties.FACING, direction);

    }

    @Override
    public Class<ThermometerBlockEntity> getBlockEntityClass() {
        return ThermometerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ThermometerBlockEntity> getBlockEntityType() {
        return CNABlockEntityTypes.THERMOMETER.get();
    }
}
