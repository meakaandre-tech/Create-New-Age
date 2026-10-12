package org.antarcticgardens.cna.client.render;

import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBufferRenderState;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.level.block.Blocks;
import org.antarcticgardens.cna.content.energising.EnergiserBlockEntity;

/** The shaft, and the energy beam down to the item being energised. */
public class EnergiserRenderer extends KineticPartsRenderer<EnergiserBlockEntity> {
    public EnergiserRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void collect(EnergiserBlockEntity be, State state, float partialTicks) {
        if (be.size <= 0f || be.getLevel() == null)
            return;

        float scalar = (1 - be.size * 0.12f) * 0.5f;
        SuperByteBufferRenderState beam = CachedBuffers.block(Blocks.CONCRETE.white().defaultBlockState())
                .color(100, 150, 200, 200)
                .translate(scalar, -1.2f, scalar)
                .scale(be.size * 0.12f, 1.3f, be.size * 0.12f)
                .extractRenderState();
        state.parts.add((matrices, queue) -> beam.submit(RenderTypes.lightning(), matrices, queue));
    }
}
