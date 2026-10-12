package org.antarcticgardens.cna;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;

public class CNADataComponents {
    /** The connector a wire item is being strung from. */
    public static final DataComponentType<BlockPos> BOUND_TO = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,
            CreateNewAge.asResource("bound_to"),
            DataComponentType.<BlockPos>builder().persistent(BlockPos.CODEC).networkSynchronized(BlockPos.STREAM_CODEC).build());

    public static void register() {
    }
}
