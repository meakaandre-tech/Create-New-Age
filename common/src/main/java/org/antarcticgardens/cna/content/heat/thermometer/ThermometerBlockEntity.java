package org.antarcticgardens.cna.content.heat.thermometer;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.compat.computercraft.AbstractComputerBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
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
        HeatBlockEntity.addToolTips(this, tooltip);

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
        return from == getBlockState().getValue(BlockStateProperties.FACING).getOpposite();
    }

    @Override
    public float getHeat() {
        BlockEntity be = getLevel().getBlockEntity(getBlockPos().relative(getBlockState().getValue(BlockStateProperties.FACING)));
        if (be instanceof HeatPipeBlockEntity heatPipe) {
            return heatPipe.getHeat();
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
