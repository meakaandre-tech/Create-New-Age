package org.antarcticgardens.cna.content.energising;

import com.zurrtum.create.content.processing.AssemblyOperatorBlockItem;
import net.minecraft.world.level.block.Block;

/** Energisers can be placed above a belt as sequenced assembly operators. */
public class EnergisingBlockItem extends AssemblyOperatorBlockItem {
    public EnergisingBlockItem(Block block, Properties builder) {
        super(block, builder);
    }
}
