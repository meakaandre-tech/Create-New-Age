package org.antarcticgardens.cna.content.electricity.connector;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.antarcticgardens.cna.content.electricity.network.ElectricalNetwork;
import org.antarcticgardens.cna.content.electricity.network.NetworkEnergyStorage;

public class ElectricalConnectorBlockEntity extends AbstractElectricalConnector {
    private final NetworkEnergyStorage storage;

    public ElectricalConnectorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        storage = new NetworkEnergyStorage(this, null);
    }

    public ElectricalConnectorMode getMode() {
        return getBlockState().getValue(ElectricalConnectorBlock.MODE);
    }

    @Override
    public BlockPos getSupportingBlockPos() {
        return getBlockPos().relative(getBlockState().getValue(BlockStateProperties.FACING).getOpposite());
    }

    @Override
    public void setNetwork(ElectricalNetwork network) {
        super.setNetwork(network);
        storage.setNetwork(network);
    }

    @Override
    public Direction getFacing() {
        return getBlockState().getValue(BlockStateProperties.FACING);
    }

    public NetworkEnergyStorage getEnergyStorage() {
        return storage;
    }
}
