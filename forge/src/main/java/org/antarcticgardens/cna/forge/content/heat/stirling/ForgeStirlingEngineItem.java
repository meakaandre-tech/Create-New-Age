package org.antarcticgardens.cna.forge.content.heat.stirling;

import com.mojang.math.Axis;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.antarcticgardens.cna.rendering.ItemShaftRenderer;
import org.joml.Vector3f;

import java.util.function.Consumer;

public class ForgeStirlingEngineItem extends BlockItem {
    public ForgeStirlingEngineItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new ItemShaftRenderer(
                new Vector3f(0.5f, 0.0f, 0.0f), Axis.XP.rotationDegrees(90.0f))));
    }

}
