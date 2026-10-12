package org.antarcticgardens.cna.content.motor.extension;

import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.antarcticgardens.cna.content.motor.extension.variants.IMotorExtensionVariant;

import java.util.List;

public class MotorExtensionBlockEntity extends SmartBlockEntity {
    public MotorExtensionValueBehaviour stressBehavior;
    private float multiplier = 1;
    private final IMotorExtensionVariant variant;

    public MotorExtensionBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, IMotorExtensionVariant variant) {
        super(type, pos, state);
        this.variant = variant;
    }

    @Override
    public void tick() {
        super.tick();

        if (multiplier > variant.getMultiplier()) {
            multiplier = variant.getMultiplier();
            stressBehavior.setValue((int) (multiplier * 100));
        }

        stressBehavior.step = variant.getScrollStep();
        stressBehavior.betweenValidated(1, (int) (100 * variant.getMultiplier()));
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour<?>> behaviours) {
        stressBehavior = new MotorExtensionValueBehaviour(this);
        stressBehavior.between(1, (int) (100 * variant.getMultiplier()));
        stressBehavior.setValue(100);
        stressBehavior.withCallback(i -> {
            multiplier = i / 100f;
            this.notifyUpdate();
        });
        behaviours.add(stressBehavior);
    }

    public float getMultiplier() {
        return multiplier;
    }

    public IMotorExtensionVariant getVariant() {
        return variant;
    }

    @Override
    protected void read(ValueInput tag, boolean clientPacket) {
        multiplier = tag.getFloatOr("stressMultiplier", 1);
        super.read(tag, clientPacket);
    }

    @Override
    protected void write(ValueOutput tag, boolean clientPacket) {
        tag.putFloat("stressMultiplier", multiplier);
        super.write(tag, clientPacket);
    }
}
