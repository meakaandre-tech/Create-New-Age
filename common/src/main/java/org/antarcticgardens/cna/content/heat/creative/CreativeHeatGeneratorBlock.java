package org.antarcticgardens.cna.content.heat.creative;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.antarcticgardens.cna.CNABlockEntityTypes;

public class CreativeHeatGeneratorBlock extends Block implements IBE<CreativeHeatGeneratorBlockEntity>, IWrenchable {

    public CreativeHeatGeneratorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Class<CreativeHeatGeneratorBlockEntity> getBlockEntityClass() {
        return CreativeHeatGeneratorBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CreativeHeatGeneratorBlockEntity> getBlockEntityType() {
        return CNABlockEntityTypes.CREATIVE_HEAT_GENERATOR.get();
    }
}
