package org.antarcticgardens.cna.client;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.catnip.math.AngleHelper;
import com.zurrtum.create.catnip.math.VecHelper;
import com.zurrtum.create.client.content.kinetics.motor.MotorValueBox;
import com.zurrtum.create.client.flywheel.lib.transform.TransformStack;
import com.zurrtum.create.client.foundation.blockEntity.ValueSettingsBoard;
import com.zurrtum.create.client.foundation.blockEntity.ValueSettingsFormatter;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.scrollValue.KineticScrollValueBehaviour;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.zurrtum.create.client.foundation.utility.CreateLang;
import com.zurrtum.create.content.kinetics.base.DirectionalKineticBlock;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import com.zurrtum.create.foundation.blockEntity.behaviour.ValueSettings;
import com.zurrtum.create.foundation.blockEntity.behaviour.scrollValue.ServerScrollValueBehaviour;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.antarcticgardens.cna.content.electricity.light.StreetLightBlockEntity;
import org.antarcticgardens.cna.content.motor.MotorBlockEntity;
import org.antarcticgardens.cna.content.motor.extension.MotorExtensionBlockEntity;
import org.antarcticgardens.cna.content.motor.extension.MotorExtensionValueBehaviour;

/** Client halves of the value boxes (what players see and scroll); the values live in the server behaviours. */
public class CNAValueBoxes {

    /** Motor speed, set with a wrench, as the NeoForge build required. */
    public static BlockEntityBehaviour<SmartBlockEntity> motor(MotorBlockEntity be) {
        return new KineticScrollValueBehaviour(CreateLang.translateDirect("kinetics.creative_motor.rotation_speed"), be, new MotorValueBox()) {
            {
                needsWrench = true;
            }
        };
    }

    public static BlockEntityBehaviour<SmartBlockEntity> motorExtension(MotorExtensionBlockEntity be) {
        return new ExtensionScroll(be);
    }

    public static BlockEntityBehaviour<SmartBlockEntity> streetLight(StreetLightBlockEntity be) {
        return new ScrollValueBehaviour<SmartBlockEntity, ServerScrollValueBehaviour>(
                CreateLang.translateDirect("create_new_age.street_light.light_level"), be, new StreetLightBox()) {
            {
                needsWrench = true;
            }
        };
    }

    static class ExtensionScroll extends ScrollValueBehaviour<SmartBlockEntity, MotorExtensionValueBehaviour> {
        ExtensionScroll(SmartBlockEntity be) {
            super(CreateLang.translateDirect("scroll.create_new_age.motor.stress_multiplier"), be, new MotorExtensionValueBox());
            withFormatter(v -> String.valueOf(Math.abs(v)));
        }

        private int step() {
            return behaviour == null ? 1 : Math.max(1, behaviour.step);
        }

        @Override
        public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
            ImmutableList<Component> rows = ImmutableList.of(Component.literal("%").withStyle(ChatFormatting.BOLD));
            return new ValueSettingsBoard(label, behaviour.getMax() / step(), 100, rows, new ValueSettingsFormatter(this::formatSettings));
        }

        public MutableComponent formatSettings(ValueSettings settings) {
            return CreateLang.number(Math.max(1, Math.abs(settings.value() * step())))
                    .add(CreateLang.text("%").style(ChatFormatting.BOLD))
                    .component();
        }
    }

    static class MotorExtensionValueBox extends ValueBoxTransform.Sided {
        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8, 8, 13.5);
        }

        @Override
        public Vec3 getLocalOffset(BlockState state) {
            Direction facing = state.getValue(DirectionalKineticBlock.FACING);
            return super.getLocalOffset(state)
                    .add(Vec3.atLowerCornerOf(facing.getUnitVec3i()).scale(-1 / 16f))
                    .add(new Vec3(facing.step()).scale(2 / 16f));
        }

        @Override
        public void rotate(BlockState state, PoseStack ms) {
            super.rotate(state, ms);
            Direction facing = state.getValue(DirectionalKineticBlock.FACING);
            if (facing.getAxis() == Direction.Axis.Y || getSide() != Direction.UP)
                return;
            TransformStack.of(ms).rotateZDegrees(-AngleHelper.horizontalAngle(facing) + 180);
        }

        @Override
        protected boolean isSideActive(BlockState state, Direction direction) {
            Direction facing = state.getValue(DirectionalKineticBlock.FACING);
            if (facing.getAxis() != Direction.Axis.Y && direction == Direction.DOWN)
                return false;
            return direction.getAxis() != facing.getAxis();
        }
    }

    static class StreetLightBox extends ValueBoxTransform.Sided {
        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8, 9, 12.5);
        }

        @Override
        protected boolean isSideActive(BlockState state, Direction direction) {
            if (direction == Direction.UP || direction == Direction.DOWN)
                return false;
            return super.isSideActive(state, direction);
        }
    }
}
