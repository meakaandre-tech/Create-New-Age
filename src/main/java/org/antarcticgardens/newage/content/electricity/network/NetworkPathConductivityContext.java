package org.antarcticgardens.newage.content.electricity.network;

import net.minecraft.util.Tuple;
import org.antarcticgardens.newage.content.electricity.connector.ElectricalConnectorBlockEntity;
import org.antarcticgardens.newage.content.electricity.wire.WireType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class NetworkPathConductivityContext {
    // Use ConcurrentHashMap instead of HashMap for thread safety
    private final Map<NetworkPathKey<ElectricalConnectorBlockEntity>, Tuple<Long, Long>> connections;
    private static final int MAX_PATH_LENGTH = 100;
    public static final long OPERATION_TIMEOUT_NANOS = 100_000_000; // 100ms timeout
    private static final int MAX_CONNECTIONS = 5000; // Cap network size

    // Circuit breaker pattern to disable electricity when system is unstable
    private static final AtomicBoolean DISABLED = new AtomicBoolean(false);
    private static long disabledUntilTime = 0;
    private static final long RECOVERY_TIME_MS = 10000; // 10 seconds cooldown

    // Track chunk loading state
    private static boolean inChunkLoading = false;

    private static final int MAX_ITERATION_NODES = 200; // Hard cap on iterations

    protected long calculatePathConductivity(NetworkPath path) {
        if (isSystemDisabled() || inChunkLoading) return 0;
        if (path == null || path.getLength() < 2 || path.getLength() > MAX_PATH_LENGTH) return 0;

        long startTime = System.nanoTime();
        ElectricalConnectorBlockEntity prevNode = null;
        long conductivity = Long.MAX_VALUE;

        try {
            // Convert to array instead of using the iterator directly
            Object[] nodes = path.getNodes().toArray();
            if (nodes.length > MAX_ITERATION_NODES) {
                disableSystem("Path too large: " + nodes.length + " nodes");
                return 0;
            }

            for (int i = 0; i < nodes.length; i++) {
                // Check timeout every few nodes
                if (i % 10 == 0 && System.nanoTime() - startTime > OPERATION_TIMEOUT_NANOS) {
                    disableSystem("Timeout in calculatePathConductivity loop");
                    return 0;
                }

                ElectricalConnectorBlockEntity node = (ElectricalConnectorBlockEntity) nodes[i];
                if (node == null) return 0;

                if (prevNode == null) {
                    prevNode = node;
                    continue;
                }

                NetworkPathKey<ElectricalConnectorBlockEntity> key = new NetworkPathKey<>(prevNode, node);
                // Safe connection check without using containsKey
                Tuple<Long, Long> connection = connections.get(key);
                if (connection == null) return 0;

                long connectionConductivity = connection.getB();
                conductivity = Math.min(connectionConductivity, conductivity);
                prevNode = node;
            }

            return conductivity;
        } catch (Exception e) {
            disableSystem("Exception in calculatePathConductivity: " + e.getMessage());
            return 0;
        }
    }

    public NetworkPathConductivityContext() {
        connections = new ConcurrentHashMap<>(256);
    }

    public NetworkPathConductivityContext(NetworkPathConductivityContext context) {
        connections = new ConcurrentHashMap<>(256);

        if (context != null && context.connections != null && !isSystemDisabled()) {
            try {
                for (Map.Entry<NetworkPathKey<ElectricalConnectorBlockEntity>, Tuple<Long, Long>> e : context.connections.entrySet()) {
                    if (connections.size() >= MAX_CONNECTIONS) break;

                    if (e != null && e.getKey() != null && e.getValue() != null) {
                        connections.put(e.getKey(), new Tuple<>(e.getValue().getA(), e.getValue().getB()));
                    }
                }
            } catch (Exception ex) {
                disableSystem("Error in constructor");
            }
        }
    }

    // Call this when chunks are loading
    public static void setChunkLoading(boolean loading) {
        inChunkLoading = loading;
        if (loading) {
            // When chunks start loading, temporarily disable electricity
            disableSystem("Chunk loading");
        } else {
            // When chunk loading is done, enable electricity
            if (!isSystemDisabled()) {
                // If system is already enabled, no need to do anything
                return;
            }
                enableSystem();
        }
    }

    private static void enableSystem() {
        DISABLED.set(false);
        disabledUntilTime = 0;
        System.out.println("[CreateNewAge] Electricity system re-enabled - chunk loading completed");
    }

    static void disableSystem(String reason) {
        DISABLED.set(true);
        disabledUntilTime = System.currentTimeMillis() + RECOVERY_TIME_MS;
        System.out.println("[CreateNewAge] Electricity system temporarily disabled: " + reason);
    }


    private static boolean isSystemDisabled() {
        if (DISABLED.get()) {
            // Check if recovery time has passed
            if (System.currentTimeMillis() > disabledUntilTime) {
                DISABLED.set(false);
                return false;
            }
            return true;
        }
        return false;
    }

    public void addConnection(ElectricalConnectorBlockEntity node, ElectricalConnectorBlockEntity node1) {
        if (isSystemDisabled() || inChunkLoading) return;
        if (node == null || node1 == null) return;
        if (node.equals(node1)) return;
        if (connections.size() >= MAX_CONNECTIONS) return;

        long startTime = System.nanoTime();

        try {
            NetworkPathKey<ElectricalConnectorBlockEntity> key = new NetworkPathKey<>(node, node1);

            // Check timeout
            if (System.nanoTime() - startTime > OPERATION_TIMEOUT_NANOS) {
                disableSystem("Timeout in addConnection");
                return;
            }

            if (!connections.containsKey(key)) {
                WireType conductivity = node.getConnectors().getOrDefault(node1, null);
                if (conductivity != null) {
                    connections.put(key, new Tuple<>(conductivity.getConductivity(), 0L));
                }
            }
        } catch (Exception e) {
            disableSystem("Exception in addConnection");
        }
    }

    private boolean isConnectionValid(NetworkPathKey<ElectricalConnectorBlockEntity> key) {
        if (isSystemDisabled() || inChunkLoading) return false;
        if (key == null) return false;

        try {
            long startTime = System.nanoTime();
            boolean result = connections.containsKey(key);

            if (System.nanoTime() - startTime > OPERATION_TIMEOUT_NANOS) {
                disableSystem("Timeout in isConnectionValid");
                return false;
            }

            return result;
        } catch (Exception e) {
            disableSystem("Exception in isConnectionValid");
            return false;
        }
    }

    private Tuple<Long, Long> safeGet(NetworkPathKey<ElectricalConnectorBlockEntity> key) {
        if (isSystemDisabled() || inChunkLoading) return null;
        if (key == null) return null;

        try {
            long startTime = System.nanoTime();
            Tuple<Long, Long> result = connections.get(key);

            if (System.nanoTime() - startTime > OPERATION_TIMEOUT_NANOS) {
                disableSystem("Timeout in safeGet");
                return null;
            }

            return result;
        } catch (Exception e) {
            disableSystem("Exception in safeGet");
            return null;
        }
    }

    protected long getConnectionConductivity(NetworkPathKey<ElectricalConnectorBlockEntity> key) {
        if (isSystemDisabled() || inChunkLoading) return 0;
        Tuple<Long, Long> connection = safeGet(key);
        return connection != null ? connection.getB() : 0;
    }

    protected boolean containsConnection(NetworkPathKey<ElectricalConnectorBlockEntity> key) {
        return !isSystemDisabled() && !inChunkLoading && isConnectionValid(key);
    }

    protected void decreasePathConductivity(NetworkPath path, long amount) {
        if (isSystemDisabled() || inChunkLoading) return;
        if (path == null || path.getLength() < 2) return;

        long startTime = System.nanoTime();

        try {
            ElectricalConnectorBlockEntity prevNode = null;
            int nodeCount = 0;

            for (ElectricalConnectorBlockEntity node : path.getNodes()) {
                if (System.nanoTime() - startTime > OPERATION_TIMEOUT_NANOS) {
                    disableSystem("Timeout in decreasePathConductivity");
                    return;
                }

                if (nodeCount++ > MAX_PATH_LENGTH) return;
                if (node == null) return;

                if (prevNode == null) {
                    prevNode = node;
                    continue;
                }

                NetworkPathKey<ElectricalConnectorBlockEntity> key = new NetworkPathKey<>(prevNode, node);
                Tuple<Long, Long> connection = connections.get(key);

                if (connection == null) return;

                long connectionConductivity = connection.getB();
                connection.setB(connectionConductivity - amount);
                prevNode = node;
            }
        } catch (Exception e) {
            disableSystem("Exception in decreasePathConductivity");
        }
    }

    protected void updateConductivity() {
        if (isSystemDisabled() || inChunkLoading) return;

        long startTime = System.nanoTime();

        try {
            for (Map.Entry<NetworkPathKey<ElectricalConnectorBlockEntity>, Tuple<Long, Long>> e : connections.entrySet()) {
                if (System.nanoTime() - startTime > OPERATION_TIMEOUT_NANOS) {
                    disableSystem("Timeout in updateConductivity");
                    return;
                }

                if (e != null && e.getKey() != null && e.getValue() != null) {
                    e.getValue().setB(e.getValue().getA());
                }
            }
        } catch (Exception e) {
            disableSystem("Exception in updateConductivity");
        }
    }

}