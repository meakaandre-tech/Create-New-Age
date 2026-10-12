package org.antarcticgardens.cna.content.energising;

import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.antarcticgardens.cna.energy.SimpleEnergyStorage;
import org.antarcticgardens.cna.util.RunnableUtil;

import java.util.List;

public class EnergiserBlockEntity extends KineticBlockEntity {
    private final SimpleEnergyStorage storage;

    public int tier;
    public float size = 0f;
    public long lastCharged = -1;
    private EnergiserBehaviour energisingBehaviour;

    public EnergiserBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        tier = state.getBlock() instanceof EnergiserBlock energiser ? energiser.getTier() : 1;

        storage = new SimpleEnergyStorage(EnergiserBlock.getCapacity(tier))
                .onFinalCommit(RunnableUtil.createBlockEntityUpdater(this));

        this.energisingBehaviour.tier = tier;
    }

    @Override
    protected void write(ValueOutput compound, boolean clientPacket) {
        compound.putLong("Energy", storage.getStoredEnergy());
        super.write(compound, clientPacket);
    }

    @Override
    protected void read(ValueInput compound, boolean clientPacket) {
        storage.setStoredEnergy(compound.getLongOr("Energy", 0));
        super.read(compound, clientPacket);
    }

    @Override
    protected AABB createRenderBoundingBox() {
        var pos = new Vec3(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
        return new AABB(pos.subtract(1, 3, 1), pos.add(1, 1, 1));
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour<?>> behaviours) {
        super.addBehaviours(behaviours);
        energisingBehaviour = new EnergiserBehaviour(this);
        behaviours.add(energisingBehaviour);
    }

    public EnergiserBehaviour getEnergisingBehaviour() {
        return energisingBehaviour;
    }

    public SimpleEnergyStorage getEnergyStorage() {
        return storage;
    }

    @Override
    public float calculateStressApplied() {
        float impact = switch (tier) {
            case 1 -> 4.0f;
            case 2 -> 8.0f;
            default -> 32.0f;
        };
        this.lastStressApplied = impact;
        return impact;
    }
}
