package org.antarcticgardens.cna.forge.content.energising;

import com.mojang.math.Axis;
import com.simibubi.create.content.processing.AssemblyOperatorBlockItem;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.antarcticgardens.cna.rendering.ItemShaftRenderer;
import org.joml.Vector3f;

import java.util.function.Consumer;

public class ForgeEnergisingBlockItem extends AssemblyOperatorBlockItem {
    public ForgeEnergisingBlockItem(Block block, Properties builder) {
        super(block, builder);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new ItemShaftRenderer(
                new Vector3f(0.5f, 0.0f, 0.0f), Axis.XP.rotationDegrees(90.0f))));
    }
}
