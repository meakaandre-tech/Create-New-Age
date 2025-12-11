package org.antarcticgardens.cna;

import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.nio.file.Path;

@OnlyIn(Dist.CLIENT)
public class CreateNewAgeClientForge extends CreateNewAgeClient {
    public void onClientSetup(final FMLClientSetupEvent event) {
        this.initialize();
        
        ModContainer modContainer = ModList.get()
                .getModContainerById(CreateNewAge.MOD_ID)
                .orElseThrow(() -> new IllegalStateException("What the..."));

        modContainer.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(
                        (mc, previousScreen) -> new BaseConfigScreen(previousScreen, CreateNewAge.MOD_ID)));

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerDatapack);
    }

    public void registerDatapack(final AddPackFindersEvent event) {
        if (event.getPackType() == PackType.SERVER_DATA) {
            Path path = ModList.get().getModFileById("create_new_age").getFile().findResource("resourcepacks/create_new_age_monkey_edition");
            Pack builtinDataPack = Pack.readMetaAndCreate(
                    "create_new_age:create_new_age_monkey_edition",
                    Component.translatable("create_new_age.monkey_edition"),
                    false,
                    (a) -> new PathPackResources(a, path, false),
                    PackType.SERVER_DATA,
                    Pack.Position.TOP,
                    PackSource.create((arg) -> Component.translatable("pack.nameAndSource", arg, Component.translatable("pack.source.builtin")).withStyle(ChatFormatting.GRAY), false)
            );

            event.addRepositorySource((packConsumer) -> packConsumer.accept(builtinDataPack));
        }
    }

}
