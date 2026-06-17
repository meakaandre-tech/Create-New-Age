package org.antarcticgardens.cna.content.heat.thermometer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.antarcticgardens.cna.CNAPartialModels;

public class ThermometerBlockRenderer extends SmartBlockEntityRenderer<ThermometerBlockEntity> {
    public ThermometerBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    public static void render(final BlockState blockState, final int tickCount, final float dialValue, PoseStack poseStack,
                              MultiBufferSource buffer, int light) {
        final VertexConsumer vb = buffer.getBuffer(RenderType.cutout());

        final PartialModel partialModel = switch (blockState.getValue(BlockStateProperties.ATTACH_FACE)) {
            case FLOOR -> CNAPartialModels.FLOOR_THERMOMETER_INDICATOR;
            case WALL -> CNAPartialModels.WALL_THERMOMETER_INDICATOR;
            case CEILING -> CNAPartialModels.CEILING_THERMOMETER_INDICATOR;
        };
        float indicatorPivot = switch (blockState.getValue(BlockStateProperties.ATTACH_FACE)) {
            case FLOOR -> 7.5f / 16;
            case WALL -> 5.5f / 16;
            case CEILING -> 3.5f / 16;
        };

        final SuperByteBuffer indicator = CachedBuffers.partial(partialModel, blockState);

        final Direction direction = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        final float yRot = !direction.getAxis().equals(Direction.Axis.Z) ?
                (float) Math.toRadians(blockState.getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite().toYRot()) :
                (float) Math.toRadians(blockState.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot());
        indicator.rotateCentered((float) (yRot + Math.PI), Direction.UP);

        indicator.translate(0, indicatorPivot, 0)
                .scaleY(dialValue)
                .translate(0, -indicatorPivot, 0)
                .light(light)
                .renderInto(poseStack, vb);
    }

    @Override
    protected void renderSafe(ThermometerBlockEntity blockEntity, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(blockEntity, partialTicks, ms, buffer, light, overlay);

        render(blockEntity.getBlockState(), blockEntity.tickCount, blockEntity.dialValue, ms, buffer, light);
    }
}
