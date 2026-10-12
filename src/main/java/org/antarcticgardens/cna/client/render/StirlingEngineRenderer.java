package org.antarcticgardens.cna.client.render;

import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.catnip.math.AngleHelper;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.antarcticgardens.cna.content.heat.stirling.StirlingEngineBlockEntity;

/** The shaft and the small flywheel on it, spinning at the engine's visual speed. */
public class StirlingEngineRenderer extends KineticPartsRenderer<StirlingEngineBlockEntity> {
    public StirlingEngineRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void collect(StirlingEngineBlockEntity be, State state, float partialTicks) {
        Direction.Axis axis = be.getBlockState().getValue(BlockStateProperties.AXIS);
        BlockState wheelState = AllBlocks.FLYWHEEL.defaultBlockState().setValue(BlockStateProperties.AXIS, axis);

        float speed = be.getVisualSpeed(partialTicks) * 3 / 10f;
        float angle = be.getAngle() + speed * partialTicks;

        SuperByteBuffer wheel = CachedBuffers.block(wheelState);
        wheel.rotateCentered(AngleHelper.rad(angle), Direction.get(Direction.AxisDirection.POSITIVE, axis));
        wheel.center().scale(0.2f, 0.2f, 0.2f).uncenter();
        state.add(wheel);
    }
}
