package org.antarcticgardens.cna.content.motor;

import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.content.kinetics.KineticNetwork;
import com.zurrtum.create.content.kinetics.base.DirectionalKineticBlock;
import com.zurrtum.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.zurrtum.create.content.kinetics.base.IRotate;
import com.zurrtum.create.foundation.blockEntity.behaviour.scrollValue.ServerKineticScrollValueBehaviour;
import com.zurrtum.create.foundation.blockEntity.behaviour.scrollValue.ServerScrollValueBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.antarcticgardens.cna.config.CNAConfig;
import org.antarcticgardens.cna.content.motor.extension.MotorExtensionBlockEntity;
import org.antarcticgardens.cna.content.motor.variants.AdvancedMotorVariant;
import org.antarcticgardens.cna.content.motor.variants.BasicMotorVariant;
import org.antarcticgardens.cna.content.motor.variants.IMotorVariant;
import org.antarcticgardens.cna.content.motor.variants.ReinforcedMotorVariant;
import org.antarcticgardens.cna.energy.SimpleEnergyStorage;
import org.antarcticgardens.cna.util.RunnableUtil;

import java.util.List;

/**
 * Electric motor: turns energy into rotation at the speed set on its value box. The value box itself is
 * a client behaviour (Create Fly keeps those on the client).
 */
public class MotorBlockEntity extends GeneratingKineticBlockEntity {
    private final SimpleEnergyStorage storage;

    public boolean needsPower = false;
    private final IMotorVariant variant;
    public final int tier;
    public ServerScrollValueBehaviour speedBehavior;
    public boolean powered = false;
    private float actualSpeed = 0;
    private float actualStress = 0;
    private long prvEnergy = -100000;
    private long e;

    private float speed = 0;
    private float stress = 0;

    public MotorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, IMotorVariant variant) {
        super(type, pos, state);
        this.variant = variant;
        this.tier = switch (variant) {
            case BasicMotorVariant v -> 1;
            case AdvancedMotorVariant v -> 2;
            case ReinforcedMotorVariant v -> 3;
            case null, default -> 0;
        };

        storage = new SimpleEnergyStorage(variant.getMaxCapacity())
                .onFinalCommit(RunnableUtil.createBlockEntityUpdater(this))
                .setSupportsExtraction(false);
    }

    @Override
    public void initialize() {
        powered = getLevel().hasNeighborSignal(this.getBlockPos());
        super.initialize();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour<?>> behaviours) {
        super.addBehaviours(behaviours);
        // runs from the SmartBlockEntity constructor, before this class's fields are set; the
        // variant's real speed range is applied in tick()
        speedBehavior = new ServerKineticScrollValueBehaviour(this);
        speedBehavior.between(-256, 256);
        speedBehavior.setValue(getDefaultSpeed());
        speedBehavior.withCallback(i -> this.updateGeneratedRotation());
        behaviours.add(speedBehavior);
    }

    public int getDefaultSpeed() {
        return 16;
    }

    public long getEnergyUsage() {
        return e;
    }

    @Override
    protected void read(ValueInput compound, boolean clientPacket) {
        storage.setStoredEnergy(compound.getLongOr("energy", 0));
        actualSpeed = compound.getFloatOr("aSpeed", 0);
        needsPower = compound.getBooleanOr("needsPower", false);
        stress = compound.getFloatOr("lastGeneratedStress", 0);
        speed = compound.getFloatOr("lastGeneratedSpeed", 0);
        e = compound.getLongOr("eUse", 0);
        actualStress = compound.getFloatOr("actualStress", 0);
        super.read(compound, clientPacket);
    }

    @Override
    protected void write(ValueOutput compound, boolean clientPacket) {
        compound.putLong("energy", storage.getStoredEnergy());
        compound.putFloat("aSpeed", actualSpeed);
        compound.putBoolean("needsPower", needsPower);
        compound.putFloat("lastGeneratedStress", stress);
        compound.putFloat("lastGeneratedSpeed", speed);
        compound.putLong("eUse", e);
        compound.putFloat("actualStress", actualStress);
        super.write(compound, clientPacket);
    }

    @Override
    public float calculateStressApplied() {
        return 0;
    }

    @Override
    public float calculateAddedStressCapacity() {
        if (level != null && level.isClientSide()) {
            return this.lastCapacityProvided;
        }

        this.lastCapacityProvided = actualStress / actualSpeed;
        this.lastCapacityProvided = Float.isNaN(this.lastCapacityProvided) || Float.isInfinite(this.lastCapacityProvided)
                ? 0 : Math.abs(this.lastCapacityProvided);
        return this.lastCapacityProvided;
    }

    @Override
    public float getGeneratedSpeed() {
        return actualSpeed;
    }

    @Override
    public void updateGeneratedRotation() {
        float speed = getGeneratedSpeed();
        float prevSpeed = this.speed;

        if (level == null || level.isClientSide())
            return;

        if (prevSpeed != speed) {
            if (!hasSource()) {
                IRotate.SpeedLevel levelBefore = IRotate.SpeedLevel.of(this.speed);
                IRotate.SpeedLevel levelAfter = IRotate.SpeedLevel.of(speed);
                if (levelBefore != levelAfter)
                    effects.queueRotationIndicators();
            }

            applyNewSpeed(prevSpeed, speed);
        }

        if (hasNetwork() && speed != 0) {
            KineticNetwork network = getOrCreateNetwork();
            network.updateCapacityFor(this, stress);
            notifyStressCapacityChange(calculateAddedStressCapacity());
            getOrCreateNetwork().updateStressFor(this, calculateStressApplied());
            network.updateStress();
        }

        onSpeedChanged(prevSpeed);

        sendData();
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null)
            return;

        float stressMultiplier = 1;
        long extraEnergy = 0;

        Direction dir = this.getBlockState().getValue(DirectionalKineticBlock.FACING);
        BlockPos behind = getBlockPos().relative(dir.getOpposite());
        if (level.getBlockState(behind).getOptionalValue(DirectionalKineticBlock.FACING).orElse(dir.getOpposite()) == dir
                && level.getBlockEntity(behind) instanceof MotorExtensionBlockEntity extension) {
            stressMultiplier = extension.getMultiplier();
            extraEnergy = extension.getVariant().getExtraCapacity();
        }

        storage.setCapacity(extraEnergy + variant.getMaxCapacity());

        if (!level.isClientSide()) {
            // keep the set speed inside this motor's range, which follows the config
            int max = (int) variant.getSpeed();
            speedBehavior.between(-max, max);
            if (Math.abs(speedBehavior.getValue()) > max)
                speedBehavior.setValue(Math.max(-max, Math.min(max, speedBehavior.getValue())));

            int needed = (int) Math.ceil((variant.getStress() * stressMultiplier
                    * CNAConfig.getServer().motorSUMultiplier.get())
                    * CNAConfig.getServer().suToEnergy.get());
            e = needsPower == powered ? storage.internalExtract(needed, false) : 0;
            if (e > 0) {
                actualSpeed = speedBehavior.getValue();
                actualStress = (float) Math.ceil((variant.getStress() * stressMultiplier
                        * CNAConfig.getServer().motorSUMultiplier.get())
                        * (e / (float) needed));
            } else {
                actualSpeed = 0;
                actualStress = 0;
            }
            if (actualSpeed != speed || actualStress != stress) {
                updateGeneratedRotation();
                speed = actualSpeed;
                stress = actualStress;
            } else if (storage.getStoredEnergy() != prvEnergy && level.getGameTime() % 20 == 0) {
                this.sendData();
                prvEnergy = storage.getStoredEnergy();
            }
        }
    }

    public SimpleEnergyStorage getEnergyStorage() {
        return storage;
    }
}
