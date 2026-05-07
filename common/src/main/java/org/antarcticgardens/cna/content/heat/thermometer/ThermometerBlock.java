package org.antarcticgardens.cna.content.heat.thermometer;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
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
import org.jspecify.annotations.Nullable;

public class ThermometerBlock extends Block implements IBE<ThermometerBlockEntity>, IWrenchable {

    public ThermometerBlock(Properties properties) {
        super(properties);
    }
    // TODO: Colision shape

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING).add(BlockStateProperties.UP).add(BlockStateProperties.DOWN);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getClickedFace().getOpposite();
        BlockPos pos = context.getClickedPos();
        boolean facingUp = direction == Direction.UP;
        boolean facingDown = direction == Direction.DOWN;

        if (Direction.Plane.VERTICAL.test(direction)) {
            direction = context.getHorizontalDirection();
        }

        if (context.getPlayer() != null && context.getPlayer().isCrouching()) {
            direction = direction.getOpposite();
        } else if (!(context.getLevel().getBlockState(pos.relative(direction)).getBlock() instanceof HeatPipeBlock) && !facingUp && !facingDown) {
            for (Direction d : Direction.values()) {
                if (context.getLevel().getBlockState(pos.relative(d)).getBlock() instanceof HeatPipeBlock) {
                    direction = d;
                    break;
                }
            }
        }

        return this.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, direction)
                .setValue(BlockStateProperties.UP, facingUp)
                .setValue(BlockStateProperties.DOWN, facingDown);

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
