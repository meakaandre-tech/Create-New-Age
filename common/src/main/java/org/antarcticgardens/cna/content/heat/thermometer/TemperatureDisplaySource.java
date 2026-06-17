package org.antarcticgardens.cna.content.heat.thermometer;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.PercentOrProgressBarDisplaySource;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public class TemperatureDisplaySource extends PercentOrProgressBarDisplaySource {

    @Override
    protected MutableComponent formatNumeric(DisplayLinkContext context, Float currentLevel) {
        int mode = context.sourceConfig().getInt("Mode");

        if (mode == 2)
            return super.formatNumeric(context, currentLevel);

        return CreateLang.translate("tooltip.create_new_age.temperature", Math.round(currentLevel)).component();
    }

    @Override
    protected @Nullable Float getProgress(DisplayLinkContext context) {
        BlockEntity be = context.getSourceBlockEntity();
        if (!(be instanceof ThermometerBlockEntity thermometer)) {
            return null;
        }
        return switch (context.sourceConfig().getInt("Mode")) {
            case 0 -> thermometer.getHeat();
            case 1 -> thermometer.maxHeat();
            case 2, 3 -> thermometer.getHeat() / thermometer.maxHeat();
            default -> 0f;
        };
    }

    @Override
    protected boolean progressBarActive(DisplayLinkContext context) {
        return context.sourceConfig().getInt("Mode") == 3;
    }

    @Override
    protected String getTranslationKey() {
        return "temperature";
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initConfigurationWidgets(DisplayLinkContext context, ModularGuiLineBuilder builder, boolean isFirstLine) {
        super.initConfigurationWidgets(context, builder, isFirstLine);
        if (isFirstLine)
            return;
        builder.addSelectionScrollInput(0, 120,
                (si, l) -> si.forOptions(CreateLang.translatedOptions("display_source.create_new_age.temperature",
                                "amount", "max", "percent", "progress_bar"))
                        .titled(CreateLang.translateDirect("display_source.create_new_age.temperature.display")),
                "Mode");
    }

    @Override
    protected boolean allowsLabeling(DisplayLinkContext context) {
        return true;
    }
}
