package org.antarcticgardens.cna.client.tooltip;

import com.zurrtum.create.client.api.goggles.IHaveGoggleInformation;
import com.zurrtum.create.client.catnip.lang.LangBuilder;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.tooltip.GeneratingKineticTooltipBehaviour;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.tooltip.KineticTooltipBehaviour;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.tooltip.TooltipBehaviour;
import com.zurrtum.create.client.foundation.utility.CreateLang;
import com.zurrtum.create.content.processing.burner.BlazeBurnerBlock;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.antarcticgardens.cna.config.CNAConfig;
import org.antarcticgardens.cna.content.electricity.connector.ElectricalConnectorBlockEntity;
import org.antarcticgardens.cna.content.electricity.connector.ElectricalConnectorMode;
import org.antarcticgardens.cna.content.electricity.generation.brushes.CarbonBrushesBlockEntity;
import org.antarcticgardens.cna.content.electricity.generation.coil.GeneratorCoilBlockEntity;
import org.antarcticgardens.cna.content.electricity.light.StreetLightBlockEntity;
import org.antarcticgardens.cna.content.energising.EnergiserBlockEntity;
import org.antarcticgardens.cna.content.heat.HeatBlockEntity;
import org.antarcticgardens.cna.content.heat.heater.HeaterBlock;
import org.antarcticgardens.cna.content.heat.heater.HeaterBlockEntity;
import org.antarcticgardens.cna.content.heat.pipe.HeatPipeBlockEntity;
import org.antarcticgardens.cna.content.heat.plate.SolarHeatingPlateBlockEntity;
import org.antarcticgardens.cna.content.heat.pump.HeatPumpBlockEntity;
import org.antarcticgardens.cna.content.heat.stirling.StirlingEngineBlockEntity;
import org.antarcticgardens.cna.content.motor.MotorBlockEntity;
import org.antarcticgardens.cna.content.nuclear.reactor.rod.ReactorRodBlockEntity;
import org.antarcticgardens.cna.content.nuclear.reactor.vent.ReactorHeatVentBlockEntity;
import org.antarcticgardens.cna.energy.EnergyStorage;
import org.antarcticgardens.cna.util.StringFormatUtil;

import java.util.List;

/**
 * Goggle overlays of the mod's block entities. Create Fly keeps these in client-side behaviours
 * instead of on the block entity; the text is the NeoForge build's.
 */
public class CNATooltips {

    static void energyStored(List<Component> tooltip, EnergyStorage storage) {
        CreateLang.translate("tooltip.create_new_age.energy_stored").style(ChatFormatting.GRAY).forGoggles(tooltip);
        CreateLang.translate("tooltip.create_new_age.energy_storage", StringFormatUtil.formatLong(storage.getStoredEnergy()),
                StringFormatUtil.formatLong(storage.getCapacity())).style(ChatFormatting.AQUA).forGoggles(tooltip, 1);
    }

    static <T extends SmartBlockEntity & HeatBlockEntity> void heat(T self, List<Component> tooltip) {
        LangBuilder builder = CreateLang.translate("tooltip.create_new_age.temperature", StringFormatUtil.formatFloat(self.getHeat()));
        float max = self.maxHeat() * CNAConfig.getServer().overheatingMultiplier.get().floatValue();
        if (max < 0) {
            builder.style(ChatFormatting.AQUA);
        } else if (self.getHeat() >= max) {
            builder.style(ChatFormatting.DARK_RED);
        } else if (self.getHeat() >= max * 0.9) {
            builder.style(ChatFormatting.RED);
        } else if (self.getHeat() >= max * 0.75) {
            builder.style(ChatFormatting.GOLD);
        } else if (self.getHeat() >= max * 0.65) {
            builder.style(ChatFormatting.YELLOW);
        } else {
            builder.style(ChatFormatting.AQUA);
        }

        builder.add(CreateLang.text(" / ")
                .add(CreateLang.translate("tooltip.create_new_age.temperature", max > 0 ? StringFormatUtil.formatFloat(max) : "∞"))
                .style(ChatFormatting.DARK_GRAY));
        builder.forGoggles(tooltip, 1);

        float[] tiers = self.getHeatTiers();
        if (tiers == null)
            return;

        float mult = (float) self.getHeatTierMultiplier();
        float tierHeat = self.getTierHeat();
        for (int i = 0; i < tiers.length; i++) {
            float tis = tiers[i] * mult;
            float next = i + 1 < tiers.length ? tiers[i + 1] * mult : Float.MAX_VALUE;
            if (tis <= tierHeat && next > tierHeat) {
                CreateLang.text("> ").add(CreateLang.translate("tooltip.create_new_age.temperature", StringFormatUtil.formatFloat(tis)))
                        .style(ChatFormatting.GRAY).forGoggles(tooltip, 0);
            } else {
                CreateLang.text("").add(CreateLang.translate("tooltip.create_new_age.temperature", StringFormatUtil.formatFloat(tis)))
                        .style(ChatFormatting.DARK_GRAY).forGoggles(tooltip, 2);
            }
        }
    }

    static void rate(List<Component> tooltip, String label, String unitKey, float value) {
        CreateLang.translate(label).style(ChatFormatting.GRAY).forGoggles(tooltip, 1);
        CreateLang.translate(unitKey, StringFormatUtil.formatFloat(value)).style(ChatFormatting.AQUA).forGoggles(tooltip, 2);
    }

    public static class Connector extends TooltipBehaviour<ElectricalConnectorBlockEntity> implements IHaveGoggleInformation {
        public Connector(ElectricalConnectorBlockEntity be) {
            super(be);
        }

        @Override
        public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
            CreateLang.translate("tooltip.create_new_age.connector_info").style(ChatFormatting.WHITE).forGoggles(tooltip);
            CreateLang.translate("tooltip.create_new_age.mode").style(ChatFormatting.GRAY).forGoggles(tooltip);
            ElectricalConnectorMode mode = blockEntity.getMode();
            CreateLang.translate("tooltip.create_new_age.connector_mode." + mode.getSerializedName())
                    .style(ChatFormatting.AQUA).forGoggles(tooltip, 1);
            return true;
        }
    }

    public static class StreetLight extends TooltipBehaviour<StreetLightBlockEntity> implements IHaveGoggleInformation {
        public StreetLight(StreetLightBlockEntity be) {
            super(be);
        }

        @Override
        public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
            energyStored(tooltip, blockEntity.getEnergyStorage());
            return true;
        }
    }

    public static class Motor extends GeneratingKineticTooltipBehaviour<MotorBlockEntity> {
        public Motor(MotorBlockEntity be) {
            super(be);
        }

        @Override
        public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
            energyStored(tooltip, blockEntity.getEnergyStorage());
            CreateLang.translate("tooltip.create_new_age.using").style(ChatFormatting.GRAY).forGoggles(tooltip);
            CreateLang.translate("tooltip.create_new_age.energy_per_tick", StringFormatUtil.formatLong(blockEntity.getEnergyUsage()))
                    .style(ChatFormatting.AQUA).forGoggles(tooltip, 1);
            super.addToGoggleTooltip(tooltip, isPlayerSneaking);
            return true;
        }
    }

    public static class CarbonBrushes extends KineticTooltipBehaviour<CarbonBrushesBlockEntity> {
        public CarbonBrushes(CarbonBrushesBlockEntity be) {
            super(be);
        }

        @Override
        public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
            CreateLang.translate("tooltip.create_new_age.energy_stats").style(ChatFormatting.WHITE).forGoggles(tooltip);
            CreateLang.translate("tooltip.create_new_age.energy_output").style(ChatFormatting.GRAY).forGoggles(tooltip);
            CreateLang.translate("tooltip.create_new_age.energy_per_tick", StringFormatUtil.formatLong(blockEntity.getLastOutput()))
                    .style(ChatFormatting.AQUA).forGoggles(tooltip, 1);
            return true;
        }
    }

    public static class GeneratorCoil extends KineticTooltipBehaviour<GeneratorCoilBlockEntity> {
        public GeneratorCoil(GeneratorCoilBlockEntity be) {
            super(be);
        }

        @Override
        public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
            CreateLang.translate("tooltip.create_new_age.efficiency").style(ChatFormatting.GRAY).forGoggles(tooltip, 1);
            CreateLang.translate("tooltip.create_new_age.percent", StringFormatUtil.formatPercentFloat(blockEntity.getEfficiency()))
                    .style(ChatFormatting.AQUA).forGoggles(tooltip, 2);
            return super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        }
    }

    public static class Energiser extends KineticTooltipBehaviour<EnergiserBlockEntity> {
        public Energiser(EnergiserBlockEntity be) {
            super(be);
        }

        @Override
        public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
            CreateLang.translate("tooltip.create_new_age.energy_stats").style(ChatFormatting.WHITE).forGoggles(tooltip);
            energyStored(tooltip, blockEntity.getEnergyStorage());
            if (blockEntity.lastCharged != -1) {
                CreateLang.translate("tooltip.create_new_age.energy_usage").style(ChatFormatting.GRAY).forGoggles(tooltip);
                CreateLang.translate("tooltip.create_new_age.energy_per_tick", StringFormatUtil.formatLong(blockEntity.lastCharged))
                        .style(ChatFormatting.AQUA).forGoggles(tooltip, 1);
            }
            return super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        }
    }

    public static class HeatPipe extends TooltipBehaviour<HeatPipeBlockEntity> implements IHaveGoggleInformation {
        public HeatPipe(HeatPipeBlockEntity be) {
            super(be);
        }

        @Override
        public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
            heat(blockEntity, tooltip);
            if (blockEntity.generating > 0.05)
                rate(tooltip, "tooltip.create_new_age.generating", "tooltip.create_new_age.temperature.ps", blockEntity.generating);
            return true;
        }
    }

    public static class HeatPump extends TooltipBehaviour<HeatPumpBlockEntity> implements IHaveGoggleInformation {
        public HeatPump(HeatPumpBlockEntity be) {
            super(be);
        }

        @Override
        public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
            rate(tooltip, "tooltip.create_new_age.pump", "tooltip.create_new_age.temperature.ps", blockEntity.lastPump);
            return true;
        }
    }

    public static class Heater extends TooltipBehaviour<HeaterBlockEntity> implements IHaveGoggleInformation {
        public Heater(HeaterBlockEntity be) {
            super(be);
        }

        @Override
        public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
            heat(blockEntity, tooltip);
            BlazeBurnerBlock.HeatLevel strength = blockEntity.getBlockState().getValue(HeaterBlock.STRENGTH);
            double mult = CNAConfig.getServer().heaterRequiredHeatMultiplier.get();
            double released = switch (strength) {
                case SMOULDERING -> 50 * mult;
                case FADING -> 100 * mult;
                case KINDLED -> 400 * mult;
                case SEETHING -> 500 * mult;
                default -> 0;
            };
            rate(tooltip, "tooltip.create_new_age.releasing", "tooltip.create_new_age.temperature.ps", (float) released);
            return true;
        }
    }

    public static class SolarPlate extends TooltipBehaviour<SolarHeatingPlateBlockEntity> implements IHaveGoggleInformation {
        public SolarPlate(SolarHeatingPlateBlockEntity be) {
            super(be);
        }

        @Override
        public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
            heat(blockEntity, tooltip);
            rate(tooltip, "tooltip.create_new_age.generating", "tooltip.create_new_age.temperature.ps", blockEntity.getLast());
            return true;
        }
    }

    public static class StirlingEngine extends GeneratingKineticTooltipBehaviour<StirlingEngineBlockEntity> {
        public StirlingEngine(StirlingEngineBlockEntity be) {
            super(be);
        }

        @Override
        public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
            heat(blockEntity, tooltip);
            // 3.125 is 50 / 16
            rate(tooltip, "tooltip.create_new_age.using", "tooltip.create_new_age.temperature.ps", blockEntity.speed * 3.125f);
            return super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        }
    }

    public static class ReactorRod extends TooltipBehaviour<ReactorRodBlockEntity> implements IHaveGoggleInformation {
        public ReactorRod(ReactorRodBlockEntity be) {
            super(be);
        }

        @Override
        public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
            heat(blockEntity, tooltip);
            rate(tooltip, "tooltip.create_new_age.generating", "tooltip.create_new_age.temperature.ps", (float) (blockEntity.last * 20));
            CreateLang.translate("tooltip.create_new_age.fuel_ticks_left").style(ChatFormatting.GRAY).forGoggles(tooltip, 1);
            CreateLang.text(StringFormatUtil.formatFloat(blockEntity.fuel)).style(ChatFormatting.AQUA).forGoggles(tooltip, 2);
            return true;
        }
    }

    public static class ReactorHeatVent extends TooltipBehaviour<ReactorHeatVentBlockEntity> implements IHaveGoggleInformation {
        public ReactorHeatVent(ReactorHeatVentBlockEntity be) {
            super(be);
        }

        @Override
        public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
            heat(blockEntity, tooltip);
            rate(tooltip, "tooltip.create_new_age.extracting", "tooltip.create_new_age.temperature", blockEntity.getExtract());
            return true;
        }
    }
}
