package org.antarcticgardens.cna.content.heat.thermometer;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.compat.computercraft.AbstractComputerBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.antarcticgardens.cna.compat.computercraft.CNAComputerCraftProxy;
import org.antarcticgardens.cna.content.heat.HeatBlockEntity;
import org.antarcticgardens.cna.content.heat.pipe.HeatPipeBlockEntity;

import java.util.List;

public class ThermometerBlockEntity extends SmartBlockEntity implements HeatBlockEntity, IHaveGoggleInformation {
    public AbstractComputerBehaviour computerBehaviour;

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
    }

    @Override
    public void invalidate() {
        super.invalidate();
        computerBehaviour.removePeripheral();
    }

    @Override
    public boolean canConnect(Direction from) {
        if (from.getAxis().isVertical()) {
            AttachFace face = getBlockState().getValue(BlockStateProperties.ATTACH_FACE);
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
}
