package org.antarcticgardens.cna;

import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;

public class CNACreativeTab {
    public static CreativeModeTab TAB;

    public static void register() {
        TAB = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, CreateNewAge.asResource("tab"),
                FabricCreativeModeTab.builder()
                        .title(Component.translatable("tab." + CreateNewAge.MOD_ID + ".tab"))
                        .icon(CNABlocks.GENERATOR_COIL::asStack)
                        .displayItems((parameters, output) -> {
                            CNABlocks.ALL.forEach(entry -> output.accept(entry.asItem()));
                            CNAItems.TAB.forEach(entry -> output.accept(entry.asItem()));
                        })
                        .build());
    }
}
