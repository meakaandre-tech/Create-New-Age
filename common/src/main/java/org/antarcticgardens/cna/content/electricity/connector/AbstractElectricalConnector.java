package org.antarcticgardens.cna.content.electricity.connector;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.antarcticgardens.cna.content.electricity.network.ElectricalNetwork;
import org.antarcticgardens.cna.content.electricity.wire.WireType;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractElectricalConnector extends SmartBlockEntity {
    private final Map<AbstractElectricalConnector, WireType> connectors = new HashMap<>();
    private final Map<BlockPos, WireType> connectorPositions = new HashMap<>();

    private ElectricalNetwork network;

    private boolean connectionsInitialized = false;
    boolean needsInstanceUpdate = true;

    public AbstractElectricalConnector(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}

    @Override
    protected void write(CompoundTag nbt, boolean clientPacket) {
        ListTag list = new ListTag();

        for (Map.Entry<BlockPos, WireType> e : connectorPositions.entrySet()) {
            CompoundTag compound = new CompoundTag();
            compound.put("position", NBTHelper.writeVec3i(e.getKey()));
            compound.put("wire", StringTag.valueOf(e.getValue().name()));

            list.add(compound);
        }

        nbt.put("connections", list);
        super.write(nbt, clientPacket);
    }

    @Override
    protected void read(CompoundTag nbt, boolean clientPacket) {
        ListTag list = nbt.getList("connections", Tag.TAG_COMPOUND);
        connectorPositions.clear();

        for (Tag listTag : list.toArray(new Tag[0])) {
            if (listTag instanceof CompoundTag ct && ct.contains("position") && ct.contains("wire")) {
                BlockPos pos = new BlockPos(NBTHelper.readVec3i((ListTag) ct.get("position")));
                WireType wire = WireType.valueOf(ct.getString("wire"));

                connectorPositions.put(pos, wire);
            }
        }

        needsInstanceUpdate = true;
        super.read(nbt, clientPacket);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    public Map<BlockPos, WireType> getConnectorPositions() {
        return Collections.unmodifiableMap(connectorPositions);
    }

    public BlockPos getSupportingBlockPos() {
        return this.getBlockPos();
    }

    public abstract Direction getFacing();

    @Override
    public AABB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

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
            if (getLevel().getBlockEntity(e.getKey()) instanceof AbstractElectricalConnector connector)
                connect(connector, e.getValue());
        }

        needsInstanceUpdate = true;
    }

    public void remove(Level level) {
        if (!level.isClientSide())
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
        connectors.remove(entity);
        connectorPositions.remove(entity.getBlockPos());
    }

    public boolean isConnected(BlockPos pos) {
        return connectorPositions.containsKey(pos);
    }

    public Map<AbstractElectricalConnector, WireType> getConnectedConnectors() {
        return Collections.unmodifiableMap(connectors);
    }


    public void setNetwork(ElectricalNetwork network) {
        this.network = network;
    }

    public ElectricalNetwork getNetwork() {
        return network;
    }

    public Vec3 getConnectionPoint() {
        return new Vec3(0.5f, 0.5f, 0.5f);
    }

}
