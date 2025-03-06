package org.antarcticgardens.newage.content.electricity.network;

    import net.minecraft.server.level.ServerLevel;
    import net.minecraftforge.event.TickEvent;
    import net.minecraftforge.event.level.ChunkEvent;
    import net.minecraftforge.event.level.LevelEvent;
    import net.minecraftforge.eventbus.api.SubscribeEvent;
    import net.minecraftforge.fml.common.Mod;
    import org.antarcticgardens.newage.CreateNewAge;

    import java.util.HashMap;
    import java.util.Map;

    @Mod.EventBusSubscriber(modid = CreateNewAge.MOD_ID)
    public class ElectricityEvents {
        // Track chunk loading activity per dimension
        private static final Map<ServerLevel, ChunkLoadingTracker> chunkLoadingTrackers = new HashMap<>();
        private static final int CHUNKS_LOADED_THRESHOLD = 5; // Consider loading complete when fewer than this chunks load in a window
        private static final int TICKS_WITHOUT_CHUNKS_THRESHOLD = 40; // ~2 seconds without significant chunk loading

        @SubscribeEvent
        public static void onWorldLoad(LevelEvent.Load event) {
            NetworkPathConductivityContext.setChunkLoading(true);
            if (event.getLevel() instanceof ServerLevel serverLevel) {
                chunkLoadingTrackers.put(serverLevel, new ChunkLoadingTracker());
            }
        }

        @SubscribeEvent
        public static void onWorldUnload(LevelEvent.Unload event) {
            if (event.getLevel() instanceof ServerLevel serverLevel) {
                chunkLoadingTrackers.remove(serverLevel);
            }
        }

        @SubscribeEvent
        public static void onChunkLoad(ChunkEvent.Load event) {
            if (event.getLevel() instanceof ServerLevel serverLevel) {
                ChunkLoadingTracker tracker = chunkLoadingTrackers.get(serverLevel);
                if (tracker != null) {
                    tracker.registerChunkLoad();
                }
            }
        }

        @SubscribeEvent
        public static void onServerTick(TickEvent.ServerTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                boolean anyStillLoading = false;

                for (ChunkLoadingTracker tracker : chunkLoadingTrackers.values()) {
                    tracker.tick();
                    anyStillLoading |= tracker.isActivelyLoadingChunks();
                }

                // When all dimensions have stabilized, re-enable electricity
                if (!anyStillLoading) {
                    NetworkPathConductivityContext.setChunkLoading(false);
                }
            }
        }

        private static class ChunkLoadingTracker {
            private int chunksLoadedInWindow = 0;
            private int ticksWithoutSignificantLoading = 0;

            public void registerChunkLoad() {
                chunksLoadedInWindow++;
                ticksWithoutSignificantLoading = 0;
            }

            public void tick() {
                if (chunksLoadedInWindow < CHUNKS_LOADED_THRESHOLD) {
                    ticksWithoutSignificantLoading++;
                }
                chunksLoadedInWindow = 0;
            }

            public boolean isActivelyLoadingChunks() {
                return ticksWithoutSignificantLoading < TICKS_WITHOUT_CHUNKS_THRESHOLD;
            }
        }
    }