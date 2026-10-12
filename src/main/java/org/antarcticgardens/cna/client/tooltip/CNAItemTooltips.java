package org.antarcticgardens.cna.client.tooltip;

import com.zurrtum.create.client.foundation.utility.CreateLang;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.antarcticgardens.cna.config.CNAConfig;
import org.antarcticgardens.cna.content.electricity.generation.magnet.ImplementedMagnetBlock;
import org.antarcticgardens.cna.content.electricity.light.StreetLightBlock;
import org.antarcticgardens.cna.content.electricity.wire.ElectricWireItem;
import org.antarcticgardens.cna.content.energising.EnergiserBlock;
import org.antarcticgardens.cna.content.heat.plate.SolarHeatingPlateBlock;
import org.antarcticgardens.cna.content.motor.MotorBlock;
import org.antarcticgardens.cna.content.motor.extension.MotorExtensionBlock;
import org.antarcticgardens.cna.content.motor.extension.variants.IMotorExtensionVariant;
import org.antarcticgardens.cna.content.motor.variants.IMotorVariant;
import org.antarcticgardens.cna.util.StringFormatUtil;

import java.util.List;

/** Item stat lines that the NeoForge build added through Block#appendHoverText. */
public class CNAItemTooltips {
    public static void append(ItemStack stack, List<Component> lines) {
        if (stack.getItem() instanceof ElectricWireItem wire) {
            add(lines, CreateLang.translate("tooltip.create_new_age.transfers").style(ChatFormatting.GRAY).component());
            add(lines, CreateLang.text(" ").translate("tooltip.create_new_age.energy_per_tick",
                    String.format("%,d", wire.getWireType().getConductivity())).style(ChatFormatting.AQUA).component());
            return;
        }

        if (!(stack.getItem() instanceof BlockItem blockItem))
            return;
        Block block = blockItem.getBlock();

        switch (block) {
            case MotorBlock motor -> {
                IMotorVariant variant = motor.getVariant();
                add(lines, CreateLang.translate("tooltip.create_new_age.generates").style(ChatFormatting.GRAY).component());
                add(lines, CreateLang.text(" ").add(CreateLang.number(variant.getStress() * CNAConfig.getServer().motorSUMultiplier.get())
                        .text(" ").translate("generic.unit.stress").style(ChatFormatting.AQUA)).component());
                add(lines, CreateLang.translate("tooltip.create_new_age.stores").style(ChatFormatting.GRAY).component());
                add(lines, CreateLang.text(" ").translate("tooltip.create_new_age.energy",
                        StringFormatUtil.formatLong(variant.getMaxCapacity())).style(ChatFormatting.AQUA).component());
                add(lines, CreateLang.translate("tooltip.create_new_age.max_speed").style(ChatFormatting.GRAY).component());
                add(lines, CreateLang.text(" ").translate("tooltip.create_new_age.rpm", variant.getSpeed()).style(ChatFormatting.AQUA).component());
            }
            case MotorExtensionBlock extension -> {
                IMotorExtensionVariant variant = extension.getVariant();
                add(lines, Component.translatable("tooltip.create_new_age.motor_extension").withStyle(ChatFormatting.DARK_GRAY));
                add(lines, CreateLang.translate("tooltip.create_new_age.stress_limit_multiplier").style(ChatFormatting.GRAY).component());
                add(lines, CreateLang.text(" ").add(CreateLang.number((int) (variant.getMultiplier() * 100)).text("%")
                        .style(ChatFormatting.AQUA)).component());
                add(lines, CreateLang.translate("tooltip.create_new_age.additional_capacity").style(ChatFormatting.GRAY).component());
                add(lines, CreateLang.text(" ").add(CreateLang.number(variant.getExtraCapacity()).text("⚡")
                        .style(ChatFormatting.AQUA)).component());
            }
            case EnergiserBlock energiser -> {
                int tier = energiser.getTier();
                add(lines, CreateLang.translate("tooltip.create_new_age.speed").style(ChatFormatting.GRAY).component());
                add(lines, CreateLang.text(" ").translate("tooltip.create_new_age.energy_per_tick",
                                StringFormatUtil.formatLong(EnergiserBlock.getStrength(tier))).style(ChatFormatting.AQUA)
                        .add(CreateLang.text(" ").translate("tooltip.create_new_age.per_rpm", 10).style(ChatFormatting.GRAY)).component());
                add(lines, CreateLang.translate("tooltip.create_new_age.stores").style(ChatFormatting.GRAY).component());
                add(lines, CreateLang.text(" ").translate("tooltip.create_new_age.energy",
                        StringFormatUtil.formatLong(EnergiserBlock.getCapacity(tier))).style(ChatFormatting.AQUA).component());
            }
            case StreetLightBlock light -> {
                add(lines, CreateLang.translate("tooltip.create_new_age.speed").style(ChatFormatting.GRAY).component());
                add(lines, CreateLang.text(" ").translate("tooltip.create_new_age.energy_per_tick",
                                StringFormatUtil.formatLong(CNAConfig.getServer().streetLightLevelExtraction.get())).style(ChatFormatting.AQUA)
                        .add(CreateLang.text(" ").translate("tooltip.create_new_age.per_light_level").style(ChatFormatting.GRAY)).component());
                add(lines, CreateLang.translate("tooltip.create_new_age.stores").style(ChatFormatting.GRAY).component());
                add(lines, CreateLang.text(" ").translate("tooltip.create_new_age.energy",
                        StringFormatUtil.formatLong(CNAConfig.getServer().streetLightCapacity.get())).style(ChatFormatting.AQUA).component());
            }
            case ImplementedMagnetBlock magnet -> {
                add(lines, CreateLang.translate("tooltip.create_new_age.magnetic_force").style(ChatFormatting.GRAY).component());
                add(lines, CreateLang.text(" " + magnet.getStrength()).style(ChatFormatting.AQUA).component());
            }
            case SolarHeatingPlateBlock plate -> {
                add(lines, CreateLang.translate("tooltip.create_new_age.generates").style(ChatFormatting.GRAY).component());
                add(lines, CreateLang.text(" ").translate("tooltip.create_new_age.temperature.ps",
                        plate.getStrength() * CNAConfig.getServer().solarPanelHeatMultiplier.get()).style(ChatFormatting.AQUA).component());
            }
            default -> {
            }
        }
    }

    // after the item name, before the rest of the tooltip
    private static void add(List<Component> lines, Component line) {
        lines.add(line);
    }
}
