package org.antarcticgardens.cna.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.antarcticgardens.cna.CreateNewAge;
import org.antarcticgardens.cna.config.CNAConfig;
import org.antarcticgardens.cna.content.electricity.connector.AbstractElectricalConnector;
import org.antarcticgardens.cna.content.electricity.wire.ElectricWireItem;
import org.antarcticgardens.cna.content.electricity.wire.WireType;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/** Draws the wires of a connector (or street light), and the wire being strung from it in first person. */
public class ElectricalConnectorRenderer implements BlockEntityRenderer<AbstractElectricalConnector, ElectricalConnectorRenderer.State> {
    private static final Identifier RED_WIRE = CreateNewAge.asResource("textures/wire/red.png");

    public ElectricalConnectorRenderer(BlockEntityRendererProvider.Context ignored) {
    }

    public static class State extends BlockEntityRenderState {
        final List<WireDraw> wires = new ArrayList<>();
    }

    record WireDraw(Identifier texture, Wire wire) {
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(AbstractElectricalConnector be, State state, float partialTick, Vec3 cameraPos,
                                   ModelFeatureRenderer.@Nullable CrumblingOverlay crumbling) {
        BlockEntityRenderState.extractBase(be, state, crumbling);
        state.wires.clear();
        Level level = be.getLevel();
        if (level == null)
            return;

        be.getConnectorPositions().forEach((endPos, wireType) -> addConnection(state, be, endPos, wireType, level));
        addHand(state, be, partialTick, level);
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector queue, CameraRenderState camera) {
        for (WireDraw draw : state.wires)
            queue.submitCustomGeometry(poseStack, RenderTypes.entityCutout(draw.texture()), draw.wire()::emit);
    }

    // only one of the two connectors draws the wire between them
    private static boolean shouldRenderConnection(BlockPos pos, BlockPos endPos) {
        return pos.compareTo(endPos) < 0;
    }

    private static void addConnection(State state, AbstractElectricalConnector be, BlockPos endPos, WireType wireType, Level level) {
        BlockPos pos = be.getBlockPos();
        if (!shouldRenderConnection(pos, endPos))
            return;
        if (!(level.getBlockEntity(endPos) instanceof AbstractElectricalConnector endConnector))
            return;

        Vec3 originPoint = be.getConnectionPoint().add(Vec3.atLowerCornerOf(pos));
        Vec3 endPoint = endConnector.getConnectionPoint().add(Vec3.atLowerCornerOf(endPos));
        add(state, wireType.getTextureLocation(), originPoint, endPoint, level);
    }

    private static void add(State state, Identifier texture, Vec3 from, Vec3 to, Level level) {
        Wire wire = Wire.build(from.toVector3f(), to.toVector3f(),
                CNAConfig.getClient().wireSectionsPerMeter.get(),
                CNAConfig.getClient().wireThickness.get().floatValue(),
                CNAConfig.getServer().maxWireLength.get(), level);
        if (!wire.isEmpty())
            state.wires.add(new WireDraw(texture, wire));
    }

    private static void addHand(State state, AbstractElectricalConnector be, float partialTick, Level level) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || !minecraft.options.getCameraType().isFirstPerson())
            return;

        ItemStack itemStack = player.getMainHandItem();
        if (!(itemStack.getItem() instanceof ElectricWireItem))
            itemStack = player.getOffhandItem();
        if (!(itemStack.getItem() instanceof ElectricWireItem wireItem))
            return;

        BlockPos bound = wireItem.getBoundConnector(itemStack);
        if (bound == null || !bound.equals(be.getBlockPos()))
            return;

        Vec3 eyePos = minecraft.gameRenderer.mainCamera().position();
        Vec3 viewVector = player.getViewVector(partialTick);
        Vec3 wireEnd = eyePos.add(viewVector.scale(player.blockInteractionRange()));
        Vec3 wireStart = be.getConnectionPoint().add(Vec3.atLowerCornerOf(bound));

        HitResult lookingAt = minecraft.hitResult;
        if (lookingAt instanceof BlockHitResult blockHit) {
            BlockPos lookedAtPos = blockHit.getBlockPos();
            BlockEntity lookedBlockEntity = level.getBlockEntity(lookedAtPos);
            boolean snapped = false;

            if (lookedBlockEntity != null) {
                if (lookedBlockEntity == be)
                    return;
                if (lookedBlockEntity instanceof AbstractElectricalConnector otherConnector) {
                    if (otherConnector.isConnected(be.getBlockPos()))
                        return;
                    wireEnd = otherConnector.getConnectionPoint().add(Vec3.atLowerCornerOf(lookedAtPos));
                    snapped = true;
                }
            }

            if (!snapped) {
                Vec3 vec = blockHit.getLocation().subtract(viewVector.scale(0.1));
                if (eyePos.distanceToSqr(wireEnd) > eyePos.distanceToSqr(vec))
                    wireEnd = vec;
            }
        }

        Identifier texture = wireItem.getWireType().getTextureLocation();
        double distanceSqr = wireEnd.distanceToSqr(wireStart);
        int maxDistance = CNAConfig.getServer().maxWireLength.get();
        if (distanceSqr >= (maxDistance * maxDistance)) {
            if (distanceSqr > (maxDistance * maxDistance) * 4)
                return;
            texture = RED_WIRE;
        }

        add(state, texture, wireStart, wireEnd, level);
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 128;
    }
}
