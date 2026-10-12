package org.antarcticgardens.cna.client.render;

import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBuffer;
import com.zurrtum.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

/** The half shaft sticking out of a motor. */
public class HalfShaftRenderer<T extends KineticBlockEntity> extends KineticPartsRenderer<T> {
    public HalfShaftRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected SuperByteBuffer getRotatedModel(T be, State state) {
        return CachedBuffers.partialFacing(AllPartialModels.SHAFT_HALF, be.getBlockState());
    }

    @Override
    protected void collect(T be, State state, float partialTicks) {
    }
}
