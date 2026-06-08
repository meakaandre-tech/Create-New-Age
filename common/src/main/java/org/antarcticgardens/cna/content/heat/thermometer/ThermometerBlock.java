package org.antarcticgardens.cna.content.heat.thermometer;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.antarcticgardens.cna.CNABlockEntityTypes;
import org.jspecify.annotations.Nullable;

public class ThermometerBlock extends FaceAttachedHorizontalDirectionalBlock implements IBE<ThermometerBlockEntity>, IWrenchable {
    public static final MapCodec<ThermometerBlock> CODEC = simpleCodec(ThermometerBlock::new);
    public static BooleanProperty POWERED = BlockStateProperties.POWERED;

    public static final VoxelShaper FLOOR_SHAPER = VoxelShaper.forHorizontal(Shapes.or(
            Shapes.box(3.0 /16, 0.0, 3.0 /16, 13.0 /16, 2.0 /16, 13.0 /16),
            Shapes.box(3.5 /16, 2.0 /16, 6.0 /16, 4.5 /16, 12.0 /16, 10.0 /16),
            Shapes.box(11.5 /16, 2.0 /16, 6.0 /16, 12.5 /16, 12.0 /16, 10.0 /16),
            Shapes.box(4.5 /16, 4.0 /16, 6.25 /16, 11.5 /16, 1.0, 9.25 /16),
            Shapes.box(5.5 /16, 5.0 /16, 9.25 /16, 10.5 /16, 15.0 /16, 11.25 /16)
    ), Direction.NORTH);
    public static final VoxelShaper CEILING_SHAPER = VoxelShaper.forHorizontal(Shapes.or(
            Shapes.box(3.0 /16, 14.0 /16, 3.0 /16, 13.0 /16, 1.0, 13.0 /16),
            Shapes.box(3.5 /16, 4.0 /16, 6.0 /16, 4.5 /16, 14.0 /16, 10.0 /16),
            Shapes.box(11.5 /16, 4.0 /16, 6.0 /16, 12.5 /16, 14.0 /16, 10.0 /16),
            Shapes.box(4.5 /16, 0.0, 6.25 /16, 11.5 /16, 12.0 /16, 9.25 /16),
            Shapes.box(5.5 /16, 1.0 /16, 9.25 /16, 10.5 /16, 11.0 /16, 11.25 /16)
    ), Direction.NORTH);
    public static final VoxelShaper WALL_SHAPER = VoxelShaper.forHorizontal(Shapes.or(
            Shapes.box(3.0 /16, 3.0 /16, 14.0 /16, 13.0 /16, 13.0 /16, 1.0),
            Shapes.box(3.5 /16, 6.0 /16, 4.0 /16, 4.5 /16, 10.0 /16, 14.0 /16),
            Shapes.box(11.5 /16, 6.0 /16, 4.0 /16, 12.5 /16, 10.0 /16, 14.0 /16),
            Shapes.box(4.5 /16, 2.0 /16, 4.25 /16, 11.5 /16, 14.0 /16, 7.25 /16),
            Shapes.box(5.5 /16, 3.0 /16, 7.25 /16, 10.5 /16, 13.0 /16, 9.25 /16)
    ), Direction.NORTH);

    public ThermometerBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, FACE, POWERED);
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

        return this.defaultBlockState().setValue(FACING, facing).setValue(FACE, face).setValue(POWERED, false);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return true;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShaper shaper = switch (state.getValue(BlockStateProperties.ATTACH_FACE)) {
            case FLOOR -> FLOOR_SHAPER;
            case CEILING -> CEILING_SHAPER;
            case WALL -> WALL_SHAPER;
        };
        return shaper.get(state.getValue(BlockStateProperties.HORIZONTAL_FACING));
    }

    @Override
    public Class<ThermometerBlockEntity> getBlockEntityClass() {
        return ThermometerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ThermometerBlockEntity> getBlockEntityType() {
        return CNABlockEntityTypes.THERMOMETER.get();
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof ThermometerBlockEntity thermometer) {
            return thermometer.powered ? 15 : 0;
        }
        return 0;
    }
}
