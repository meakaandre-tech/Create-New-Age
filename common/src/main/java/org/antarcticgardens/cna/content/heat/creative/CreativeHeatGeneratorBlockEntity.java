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
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
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
        currentOutput = new HeatValueBehaviour(CreateLang.translateDirect("current_heat"), this, this, new HeatValueBox());
        currentOutput.value = 100;
        behaviours.add(currentOutput);
    }

    static class HeatValueBehaviour extends ScrollValueBehaviour {
        public HeatValueBehaviour(Component label, SmartBlockEntity be, HeatBlockEntity hbe, ValueBoxTransform slot) {
            super(label, be, slot);
            this.max = (int) hbe.maxHeat()/100;
            this.withFormatter(i -> Integer.toString(i*100));
        }


    }

    static class HeatValueBox extends ValueBoxTransform.Sided {

        @Override
        public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
            return super.getLocalOffset(level, pos, state);
        }

        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8, 8, 16);
        }

        @Override
        public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
            super.rotate(level, pos, state, ms);
        }
    }

    @Override
    public float getHeat() {
        return currentOutput.getValue()*100;
    }

    @Override
    public void addHeat(float amount) {
    }

    @Override
    public void setHeat(float amount) {
    }
}
