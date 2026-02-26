package org.antarcticgardens.cna;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;

public class CreateNewAgeClientFabric extends CreateNewAgeClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        this.initialize();
        BlockRenderLayerMap.INSTANCE.putBlock(CNABlocks.STREET_LIGHT.get(), RenderType.cutout());
    }
}
