package org.antarcticgardens.cna.content.heat.thermometer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.compat.computercraft.AbstractComputerBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.antarcticgardens.cna.compat.computercraft.CNAComputerCraftProxy;
import org.antarcticgardens.cna.content.electricity.light.StreetLightBlock;
import org.antarcticgardens.cna.content.electricity.light.StreetLightBlockEntity;
import org.antarcticgardens.cna.content.heat.HeatBlockEntity;
import org.antarcticgardens.cna.content.heat.pipe.HeatPipeBlockEntity;

import java.util.List;

public class ThermometerBlockEntity extends SmartBlockEntity implements HeatBlockEntity, IHaveGoggleInformation {
    public AbstractComputerBehaviour computerBehaviour;
    public ScrollValueBehaviour thresholdBehaviour;

    boolean powered = false;

    public float dialValue = 0;
    public int tickCount = 0;

    public ThermometerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (getConnectedHeatBlock() != null) {
            HeatBlockEntity.addToolTips(this, tooltip);
        }else {
            CreateLang.translate("tooltip.create_new_age.no_connected_block").forGoggles(tooltip, 1);
        }

        return IHaveGoggleInformation.super.addToGoggleTooltip(tooltip, isPlayerSneaking);
    }


    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(computerBehaviour = CNAComputerCraftProxy.behaviour(this));

        thresholdBehaviour = new ThermometerScrollValueBehaviour(CreateLang.translateDirect("scroll.create_new_age.thermometer.threshold"), this, new ThermometerBox());
        thresholdBehaviour.value = 50;
        behaviours.add(thresholdBehaviour);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        computerBehaviour.removePeripheral();
    }

    @Override
    public void tick() {
        super.tick();

        this.tickCount++;

        float dialTarget = getConnectedHeatBlock() != null ? getHeat() / maxHeat() : 0;
        dialValue += (dialTarget - dialValue) * .125f;

        boolean lastPowered = powered;
        powered = isPowered();

        if (lastPowered != powered) {
            updateSignal();
        }
    }

    @Override
    public void notifyUpdate() {
        super.notifyUpdate();
        updateSignal();
    }

    public void updateSignal() {
        getLevel().setBlockAndUpdate(getBlockPos(), getBlockState().setValue(BlockStateProperties.POWERED, isPowered()));
        getLevel().updateNeighborsAt(getBlockPos(), getBlockState().getBlock());
    }

    public boolean isPowered() {
        if (getConnectedHeatBlock() == null) return false;
        float current = getHeat() / maxHeat();
        float threshold = Math.abs(thresholdBehaviour.getValue()) / 100f;
        return thresholdBehaviour.getValue() < 0 ? current <= threshold : current >= threshold;
    }

    @Override
    public boolean canConnect(Direction from) {
        AttachFace face = getBlockState().getValue(BlockStateProperties.ATTACH_FACE);
        if (face != AttachFace.WALL) {
            return from == Direction.UP && face == AttachFace.FLOOR || from == Direction.DOWN && face == AttachFace.CEILING;
        }
        return from == getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
    }

    @Override
    public boolean canAdd(Direction from) {
        return false;
    }

    public HeatBlockEntity getConnectedHeatBlock() {
        Direction dir = getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite();
        if (getBlockState().getValue(BlockStateProperties.ATTACH_FACE) == AttachFace.CEILING) {
            dir = Direction.UP;
        } else if (getBlockState().getValue(BlockStateProperties.ATTACH_FACE) == AttachFace.FLOOR) {
            dir = Direction.DOWN;
        }
        BlockEntity be = getLevel().getBlockEntity(getBlockPos().relative(dir));
        if (be instanceof HeatBlockEntity hbe && !(be instanceof ThermometerBlockEntity)) {
            return hbe;
        }
        return null;
    }

    @Override
    public float getHeat() {
        HeatBlockEntity hbe = getConnectedHeatBlock();
        if (hbe != null) {
            return hbe.getHeat();
        }
        return 0;
    }

    @Override
    public float maxHeat() {
        HeatBlockEntity hbe = getConnectedHeatBlock();
        if (hbe != null) {
            return hbe.maxHeat();
        }
        return 0;
    }

    @Override
    public void addHeat(float amount) {
    }

    @Override
    public void setHeat(float amount) {
    }

    static class ThermometerBox extends ValueBoxTransform.Sided {

        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8, 8, 1.5);
        }

        @Override
        public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
            super.rotate(level, pos, state, ms);
            if (state.getValue(BlockStateProperties.ATTACH_FACE) == AttachFace.WALL)
                return;
            int flip = -1;
            if (state.getValue(BlockStateProperties.ATTACH_FACE) == AttachFace.CEILING) {
                flip = 1;
            }
            TransformStack.of(ms).rotateZDegrees(flip * AngleHelper.horizontalAngle(state.getValue(BlockStateProperties.HORIZONTAL_FACING)));
        }

        @Override
        protected boolean isSideActive(BlockState state, Direction direction) {
            if (state.getValue(BlockStateProperties.ATTACH_FACE) == AttachFace.CEILING) {
                return direction == Direction.DOWN;
            } else if (state.getValue(BlockStateProperties.ATTACH_FACE) == AttachFace.FLOOR) {
                return direction == Direction.UP;
            }
            return direction == state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        }
    }
}
