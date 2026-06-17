package org.antarcticgardens.cna.content.heat.creative;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import org.antarcticgardens.cna.content.heat.HeatBlockEntity;
import org.antarcticgardens.cna.util.StringFormatUtil;

public class HeatScrollValueBehaviour extends ScrollValueBehaviour {

    public HeatScrollValueBehaviour(Component label, SmartBlockEntity be, HeatBlockEntity hbe, ValueBoxTransform slot) {
        super(label, be, slot);
        max = (int) hbe.maxHeat() / 100;
    }

    @Override
    public String formatValue() {
        return StringFormatUtil.formatFloat(value * 100);
    }

    @Override
    public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
        ImmutableList<Component> rows = ImmutableList.of(
                CreateLang.translateDirect("scroll.create_new_age.creative_heat.temperature"));
        ValueSettingsFormatter formatter = new ValueSettingsFormatter(this::formatSettings);
        return new ValueSettingsBoard(label, max, 10, rows, formatter);
    }

    public MutableComponent formatSettings(ValueSettings settings) {
        return CreateLang.translate("tooltip.create_new_age.temperature", StringFormatUtil.formatFloat(settings.value() * 100)).component();
    }

    @Override
    public String getClipboardKey() {
        return "Temperature";
    }
}
