package org.antarcticgardens.cna.forge.compat.computercraft;

import com.simibubi.create.compat.Mods;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.antarcticgardens.cna.CNABlockEntityTypes;
import org.antarcticgardens.cna.content.electricity.generation.brushes.CarbonBrushesBlockEntity;
import org.antarcticgardens.cna.content.energising.EnergiserBlockEntity;
import org.antarcticgardens.cna.content.motor.MotorBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

@Mod.EventBusSubscriber
public class ForgeComputerCraftCompat {

    @SubscribeEvent
    public static void registerComputerCraftCapabilities(AttachCapabilitiesEvent<BlockEntity> event) {
        if (!Mods.COMPUTERCRAFT.isLoaded())
            return;
        if (event.getObject() instanceof CarbonBrushesBlockEntity be)
            event.addCapability(CNABlockEntityTypes.CARBON_BRUSHES.getId(),
                    new CCCapability(be));
        if (event.getObject() instanceof EnergiserBlockEntity be)
            event.addCapability(CNABlockEntityTypes.ENERGISER.getId(),
                    new CCCapability(be));
        if (event.getObject() instanceof MotorBlockEntity be) {
            event.addCapability(CNABlockEntityTypes.BASIC_MOTOR.getId(),
                    new CCCapability(be));
            event.addCapability(CNABlockEntityTypes.ADVANCED_MOTOR.getId(),
                    new CCCapability(be));
            event.addCapability(CNABlockEntityTypes.REINFORCED_MOTOR.getId(),
                    new CCCapability(be));
        }
    }

    private record CCCapability(SmartBlockEntity blockEntity) implements ICapabilityProvider {

        @Override
        @NonNull
        public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
            ForgeComputerBehaviour behaviour = (ForgeComputerBehaviour) blockEntity.getBehaviour(ForgeComputerBehaviour.TYPE);
            if (behaviour.isPeripheralCap(capability))
                return behaviour.getPeripheralCapability();
            return LazyOptional.empty();
        }
    }
}
