package org.antarcticgardens.cna.content.ponders;

import com.simibubi.create.content.decoration.encasing.CasingBlock;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.antarcticgardens.cna.CNABlocks;
import org.antarcticgardens.cna.content.heat.pipe.EncasedHeatPipeBlock;
import org.antarcticgardens.cna.content.heat.pipe.ReactorEncasedHeatPipeBlock;
import org.antarcticgardens.cna.content.nuclear.reactor.ReactorCasingBlock;

public class HeatPipeEncasingPonder {
    public static void ponder(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);

        scene.configureBasePlate(0, 0, 5);
        scene.title("encased_heat_pipe", "Encasing Heat Pipes");

        BlockPos heatCasingPos = new BlockPos(3, 1, 2);
        BlockPos reactorCasingPos = new BlockPos(1, 1, 2);

        // Setup
        scene.world().modifyBlock(heatCasingPos, s -> s.setValue(BlockStateProperties.UP, false), false);
        scene.world().modifyBlock(reactorCasingPos, s -> s.setValue(BlockStateProperties.UP, false), false);
        scene.world().modifyBlock(heatCasingPos.above(), s -> s.setValue(BlockStateProperties.DOWN, false), false);
        scene.world().modifyBlock(reactorCasingPos.above(), s -> s.setValue(BlockStateProperties.DOWN, false), false);

        scene.showBasePlate();
        scene.world().showSection(util.select().layer(1), Direction.DOWN);

        scene.idle(20);

        scene.addKeyframe(); // Encasing

        // Heat casing
        BlockEntry<CasingBlock> heatCasing = CNABlocks.HEAT_CASING;
        BlockEntry<EncasedHeatPipeBlock> encasedPipe = CNABlocks.ENCASED_HEAT_PIPE;

        scene.overlay().showControls(util.vector().topOf(heatCasingPos), Pointing.DOWN, 60).rightClick()
                .withItem(heatCasing.asStack());
        scene.idle(7);
        scene.world().setBlocks(util.select().position(heatCasingPos), encasedPipe.getDefaultState()
                .setValue(BlockStateProperties.NORTH, true)
                .setValue(BlockStateProperties.SOUTH, true)
                .setValue(BlockStateProperties.EAST, false)
                .setValue(BlockStateProperties.WEST, false)
                .setValue(BlockStateProperties.UP, false)
                .setValue(BlockStateProperties.DOWN, false), true);
        scene.idle(10);

        // Reactor casing
        BlockEntry<ReactorCasingBlock> reactorCasing = CNABlocks.REACTOR_CASING;
        BlockEntry<ReactorEncasedHeatPipeBlock> reactorEncasedPipe = CNABlocks.REACTOR_ENCASED_HEAT_PIPE;

        scene.overlay().showControls(util.vector().topOf(reactorCasingPos.below()), Pointing.UP, 60).rightClick()
                .withItem(reactorCasing.asStack());
        scene.idle(7);
        scene.world().setBlocks(util.select().position(reactorCasingPos), reactorEncasedPipe.getDefaultState()
                .setValue(BlockStateProperties.NORTH, true)
                .setValue(BlockStateProperties.SOUTH, true)
                .setValue(BlockStateProperties.EAST, false)
                .setValue(BlockStateProperties.WEST, false)
                .setValue(BlockStateProperties.UP, false)
                .setValue(BlockStateProperties.DOWN, false), true);
        scene.idle(10);

        scene.overlay().showText(60)
                .placeNearTarget()
                .text("Heat Casing or Reactor Casing can be used to decorate Heat Pipes.")
                .pointAt(util.vector().topOf(reactorCasingPos));

        scene.idle(80);

        scene.addKeyframe(); // Direction lock

        Selection toRemove = util.select().cuboid(new BlockPos(1, 1, 0), new Vec3i(2, 0, 1));
        scene.world().hideSection(toRemove, Direction.UP);

        scene.idle(20);

        scene.overlay().showText(60)
                .placeNearTarget()
                .text("Once encased, Heat Pipes will be locked into their current connectivity state.")
                .pointAt(util.vector().blockSurface(heatCasingPos, Direction.NORTH));

        scene.idle(80);

        scene.addKeyframe(); // Connection lock

        scene.world().showSection(util.select().layer(2), Direction.DOWN);

        scene.idle(20);

        scene.overlay().showText(60)
                .placeNearTarget()
                .text("They will no longer connect to new pipes added nearby.")
                .colored(PonderPalette.RED)
                .pointAt(util.vector().topOf(heatCasingPos));

        scene.idle(80);

        scene.addKeyframe(); // Radiation

        scene.overlay().showText(60)
                .placeNearTarget()
                .text("Reactor Encased Heat Pipes will also block radiation!")
                .pointAt(util.vector().centerOf(reactorCasingPos));

        scene.idle(60);
    }
}
