package org.antarcticgardens.cna.client.render;

import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.antarcticgardens.cna.client.CNAPartialModels;
import org.antarcticgardens.cna.content.electricity.generation.brushes.CarbonBrushesBlockEntity;

/** The shaft through the brushes, and the coil spinning with it. */
public class CarbonBrushesRenderer extends KineticPartsRenderer<CarbonBrushesBlockEntity> {
    public CarbonBrushesRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void collect(CarbonBrushesBlockEntity be, State state, float partialTicks) {
        BlockState blockState = be.getBlockState();
        Direction.Axis axis = blockState.getValue(BlockStateProperties.FACING).getAxis();

        SuperByteBuffer coil = CachedBuffers.partial(CNAPartialModels.COIL, blockState);
        coil.rotateCentered(state.angle, Direction.get(Direction.AxisDirection.POSITIVE, axis));
        coil.center();
        switch (axis) {
            case X -> coil.rotateDegrees(90, Direction.Axis.Z);
            case Z -> coil.rotateDegrees(90, Direction.Axis.X);
            default -> {
            }
        }
        coil.uncenter();
        state.add(coil);
    }
}
