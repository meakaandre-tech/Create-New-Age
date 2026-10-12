package org.antarcticgardens.cna.content.heat.plate;

import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.antarcticgardens.cna.config.CNAConfig;
import org.antarcticgardens.cna.content.heat.HeatBlockEntity;
import org.antarcticgardens.cna.util.StringFormatUtil;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SolarHeatingPlateBlockEntity extends SmartBlockEntity implements HeatBlockEntity {
    @Override
    public void addBehaviours(List<BlockEntityBehaviour<?>> behaviours) {
    }

    private final int energyPerSecond;
    private float last;

    public SolarHeatingPlateBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState, int energyPerSecond) {
        super(type, pos, blockState);
        this.energyPerSecond = energyPerSecond;
    }

    public static SolarHeatingPlateBlockEntity createBasic(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        return new SolarHeatingPlateBlockEntity(type, pos, blockState, 20);
    }

    public static SolarHeatingPlateBlockEntity createAdvanced(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        return new SolarHeatingPlateBlockEntity(type, pos, blockState, 60);
    }

    public float heat = 0;

    @Override
    protected void read(ValueInput tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        heat = tag.getFloatOr("heat", 0);
        last = tag.getFloatOr("last", 0);
    }

    @Override
    protected void write(ValueOutput tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putFloat("heat", heat);
        tag.putFloat("last", last);
    }

    @Override
    public boolean canConnect(Direction from) {
        return from != Direction.DOWN;
    }

    @Override
    public float getHeat() {
        return heat;
    }

    @Override
    public void addHeat(float amount) {
        heat += amount;
        setChanged();
    }

    @Override
    public void setHeat(float amount) {
        heat = amount;
        setChanged();
    }






    public void tick(BlockPos blockPos, Level world, BlockState blockState) {
        double generationMultiplier = CNAConfig.getServer().solarPanelHeatMultiplier.get();

        int dark = world.getSkyDarken();
        dark *= 2;
        HeatBlockEntity.transferAround(this);
        HeatBlockEntity.handleOverheat(this);

        float light = world.getBrightness(LightLayer.SKY, blockPos.above()) - dark;
        last = (float) Math.max((light/15f)*energyPerSecond*generationMultiplier - Math.max(0, heat - (20 * energyPerSecond*generationMultiplier)), 0);
        addHeat(last);
        HeatBlockEntity.trySync(this);
    }

    public float getLast() {
        return last;
    }
}
