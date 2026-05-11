package org.antarcticgardens.cna.content.heat.thermometer;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import org.antarcticgardens.cna.CNABlockEntityTypes;
import org.jspecify.annotations.Nullable;

public class ThermometerBlock extends FaceAttachedHorizontalDirectionalBlock implements IBE<ThermometerBlockEntity>, IWrenchable {
    public static final MapCodec<ThermometerBlock> CODEC = simpleCodec(ThermometerBlock::new);

    public ThermometerBlock(Properties properties) {
        super(properties);
    }

    // TODO: Colision shape

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, FACE);
        super.createBlockStateDefinition(builder);
    }

    @Override
    protected MapCodec<? extends FaceAttachedHorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        AttachFace face = switch (context.getClickedFace()) {
            case DOWN -> AttachFace.CEILING;
            case UP -> AttachFace.FLOOR;
            default -> AttachFace.WALL;
        };
        if (face == AttachFace.WALL) {
            facing = context.getClickedFace();
        }

        return this.defaultBlockState().setValue(FACING, facing).setValue(FACE, face);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return canAttach(level, pos, getConnectedDirection(state).getOpposite());
    }

    public static boolean canAttach(LevelReader reader, BlockPos pos, Direction direction) {
        BlockPos blockPos = pos.relative(direction);
        return !reader.getBlockState(blockPos).isAir();
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
