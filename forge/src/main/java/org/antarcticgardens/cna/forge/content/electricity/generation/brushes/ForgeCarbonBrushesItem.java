package org.antarcticgardens.cna.forge.content.electricity.generation.brushes;

import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.antarcticgardens.cna.content.electricity.generation.brushes.CarbonBrushesItemRenderer;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.function.Consumer;

public class ForgeCarbonBrushesItem extends BlockItem {
    public ForgeCarbonBrushesItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new CarbonBrushesItemRenderer(
                new Vector3f(0.0f), new Quaternionf())));
    }

}
