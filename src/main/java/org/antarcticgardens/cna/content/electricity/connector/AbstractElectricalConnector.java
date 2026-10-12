package org.antarcticgardens.cna.content.electricity.connector;

import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.antarcticgardens.cna.content.electricity.network.ElectricalNetwork;
import org.antarcticgardens.cna.content.electricity.wire.WireType;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractElectricalConnector extends SmartBlockEntity {
    protected final Map<AbstractElectricalConnector, WireType> connectors = new HashMap<>();
    protected final Map<BlockPos, WireType> connectorPositions = new HashMap<>();

    protected ElectricalNetwork network;

    protected boolean connectionsInitialized = false;
    boolean needsInstanceUpdate = true;

    public AbstractElectricalConnector(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour<?>> behaviours) {}

    @Override
    protected void write(ValueOutput tag, boolean clientPacket) {
        ValueOutput.ValueOutputList list = tag.childrenList("connections");

        for (Map.Entry<BlockPos, WireType> e : connectorPositions.entrySet()) {
            ValueOutput connection = list.addChild();
            connection.store("position", BlockPos.CODEC, e.getKey());
            connection.putString("wire", e.getValue().name());
        }

        super.write(tag, clientPacket);
    }

    @Override
    protected void read(ValueInput tag, boolean clientPacket) {
        connectorPositions.clear();

        for (ValueInput connection : tag.childrenListOrEmpty("connections")) {
            BlockPos pos = connection.read("position", BlockPos.CODEC).orElse(null);
            String wire = connection.getStringOr("wire", "");
            if (pos == null || wire.isEmpty())
                continue;
            try {
                connectorPositions.put(pos, WireType.valueOf(wire));
            } catch (IllegalArgumentException ignored) {
            }
        }

        needsInstanceUpdate = true;
        super.read(tag, clientPacket);
    }

    public Map<BlockPos, WireType> getConnectorPositions() {
        return Collections.unmodifiableMap(connectorPositions);
    }

    public BlockPos getSupportingBlockPos() {
        return this.getBlockPos();
    }

    public abstract Direction getFacing();

    protected void serverTick() {
        if (network == null)
            setNetwork(new ElectricalNetwork(this));

        if (!connectionsInitialized) {
            updateConnections();
            connectionsInitialized = true;
        }
    }

    public void neighborChanged() {
        if (network != null) {
            network.updateConsumersAndSources();
        }
    }

    private void updateConnections() {
        for (Map.Entry<BlockPos, WireType> e : connectorPositions.entrySet()) {
            if (getLevel().isLoaded(e.getKey())) {
                if (getLevel().getBlockEntity(e.getKey()) instanceof AbstractElectricalConnector connector) {
                    connect(connector, e.getValue());
                }
            }
        }

        needsInstanceUpdate = true;
    }

    /** Called when the block is broken or replaced: disconnects every wire and drops the wire items. */
    @Override
    public void destroy() {
        super.destroy();
        if (level != null)
            remove(level);
    }

    public void remove(Level level) {
        if (!level.isClientSide() && network != null)
            network.destroy();

        for (Map.Entry<AbstractElectricalConnector, WireType> e : connectors.entrySet()) {
            e.getKey().disconnect(this);
            e.getKey().updateConnections();

            e.getKey().setChanged();

            if (level instanceof ServerLevel serverLevel) {
                serverLevel.getChunkSource().blockChanged(e.getKey().getBlockPos());

                Containers.dropContents(level, getBlockPos(), NonNullList.of(ItemStack.EMPTY, e.getValue().getDroppedItem()));
            }
        }
    }

    public void connect(AbstractElectricalConnector entity, WireType wireType) {
        entity.connectWithoutNetworking(this, wireType);
        connectWithoutNetworking(entity, wireType);

        entity.setChanged();
        setChanged();

        if (level instanceof ServerLevel serverLevel) {
            network.addNode(entity);

            serverLevel.getChunkSource().blockChanged(entity.getBlockPos());
            serverLevel.getChunkSource().blockChanged(getBlockPos());
        }
    }

    private void connectWithoutNetworking(AbstractElectricalConnector entity, WireType wireType) {
        if (!connectors.containsKey(entity))
            connectors.put(entity, wireType);

        if (!connectorPositions.containsKey(entity.getBlockPos()))
            connectorPositions.put(entity.getBlockPos(), wireType);
    }

    public void disconnect(AbstractElectricalConnector entity) {
        if (network != null)
            network.removeConnection(this, entity);
        if (entity.getNetwork() != null && entity.getNetwork() != network)
            entity.getNetwork().removeConnection(this, entity);

        connectors.remove(entity);
        connectorPositions.remove(entity.getBlockPos());
    }

    public Map<AbstractElectricalConnector, WireType> getConnectedConnectors() {
        return Collections.unmodifiableMap(connectors);
    }

    public boolean isConnected(BlockPos pos) {
        return connectorPositions.containsKey(pos);
    }

    public void setNetwork(ElectricalNetwork network) {
        this.network = network;
    }

    public ElectricalNetwork getNetwork() {
        return network;
    }

    /** Wires reach other blocks, so the renderer is never culled by this block's own box. */
    @Override
    protected AABB createRenderBoundingBox() {
        return new AABB(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    public Vec3 getConnectionPoint() {
        return new Vec3(0.5f, 0.5f, 0.5f);
    }
}
