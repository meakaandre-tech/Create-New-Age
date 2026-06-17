package org.antarcticgardens.cna.content.heat.creative;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.antarcticgardens.cna.content.heat.HeatBlockEntity;

import java.util.List;

public class CreativeHeatGeneratorBlockEntity extends SmartBlockEntity implements HeatBlockEntity, IHaveGoggleInformation {
    private ScrollValueBehaviour currentOutput;

    public CreativeHeatGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        HeatBlockEntity.addToolTips(this, tooltip);
        return IHaveGoggleInformation.super.addToGoggleTooltip(tooltip, isPlayerSneaking);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        currentOutput = new HeatScrollValueBehaviour(CreateLang.translateDirect("scroll.create_new_age.creative_heat.temperature"), this, this, new HeatValueBox());
        currentOutput.value = 50;
        behaviours.add(currentOutput);
    }

    static class HeatValueBox extends ValueBoxTransform.Sided {

        @Override
        public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
            return super.getLocalOffset(level, pos, state);
        }

        @Override
        protected boolean isSideActive(BlockState state, Direction direction) {
            return direction == state.getValue(BlockStateProperties.FACING);
        }

        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8, 8, 15.5f);
        }

        @Override
        public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
            super.rotate(level, pos, state, ms);
        }
    }

    @Override
    public boolean canConnect(Direction from) {
        return true;
    }

    @Override
    public boolean canAdd(Direction from) {
        return false;
    }

    @Override
    public float getHeat() {
        return currentOutput.getValue() * 100;
    }

    @Override
    public void addHeat(float amount) {
    }

    @Override
    public void setHeat(float amount) {
    }
}
