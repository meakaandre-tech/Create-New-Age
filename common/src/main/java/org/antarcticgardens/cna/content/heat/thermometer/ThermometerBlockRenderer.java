package org.antarcticgardens.cna.content.heat.thermometer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
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

        final SuperByteBuffer indicator = CachedBuffers.partial(CNAPartialModels.THERMOMETER_INDICATOR, blockState);


        indicator.translateY((dialValue * 8f) / 16f - dialValue/16f);
//        indicator.scaleY(dialValue);
//        indicator.scaleY((dialValue * 8f) / 16f);

//        if (dialValue != 0)
//            CreateNewAge.LOGGER.info(dialValue);

        final Direction direction = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);

        indicator.light(light);
        indicator.renderInto(poseStack, vb);
    }

    @Override
    protected void renderSafe(ThermometerBlockEntity blockEntity, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(blockEntity, partialTicks, ms, buffer, light, overlay);

        render(blockEntity.getBlockState(), blockEntity.tickCount, blockEntity.getHeat()/ blockEntity.maxHeat(),
                ms, buffer, light);
    }
}
