package org.antarcticgardens.newage.content.electricity.network;

import org.antarcticgardens.newage.config.NewAgeConfig;
import org.antarcticgardens.newage.content.electricity.connector.ElectricalConnectorBlockEntity;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.antarcticgardens.newage.content.electricity.network.NetworkPathConductivityContext.OPERATION_TIMEOUT_NANOS;
import static org.antarcticgardens.newage.content.electricity.network.NetworkPathConductivityContext.disableSystem;

public class ElectricalNetworkPathManager {
    private NetworkPathConductivityContext context = new NetworkPathConductivityContext();
    // Maximum number of nodes to process before giving up
    private static final int MAX_NODES_TO_PROCESS = 1000;

    protected void addConnection(ElectricalConnectorBlockEntity node, ElectricalConnectorBlockEntity node1) {
        context.addConnection(node, node1);
    }

    private static final AtomicInteger pathFindAttempts = new AtomicInteger(0);
    private static final int MAX_PATH_FIND_PER_TICK = 50;

    protected NetworkPath findConductiblePath(ElectricalConnectorBlockEntity a, ElectricalConnectorBlockEntity b) {
        // Circuit breaker to prevent excessive path searches
        if (pathFindAttempts.incrementAndGet() > MAX_PATH_FIND_PER_TICK) {
            if (pathFindAttempts.get() == MAX_PATH_FIND_PER_TICK + 1) {
                NetworkPathConductivityContext.disableSystem("Too many pathfinding attempts");
            }
            return null;
        }
        if (a.equals(b)) return null; // Quick rejection for same nodes

        Set<ElectricalConnectorBlockEntity> visited = new HashSet<>(); // Use HashSet for O(1) lookups
        Queue<QueueElement> queue = new LinkedList<>();
        queue.add(new QueueElement(a, null, 0));
        visited.add(a);

        int nodesProcessed = 0;
        int maxDepth = NewAgeConfig.getCommon().maxPathfindingDepth.get();

        while (!queue.isEmpty() && nodesProcessed < MAX_NODES_TO_PROCESS) {
            var element = queue.poll();
            nodesProcessed++;

            if (element.connector.equals(b)) {
                NetworkPath path = unwrapConductiblePath(element);
                if (path != null && context.calculatePathConductivity(path) > 0)
                    return path;
            }

            if (element.depth >= maxDepth) continue; // Skip if we've reached max depth

            for (ElectricalConnectorBlockEntity connector : element.connector.getConnectors().keySet()) {
                if (!visited.contains(connector)) {
                    visited.add(connector);
                    queue.add(new QueueElement(connector, element, element.depth + 1));
                }
            }
        }

        return null;
    }

    private NetworkPath unwrapConductiblePath(QueueElement element) {
        NetworkPath path = new NetworkPath();
        long startTime = System.nanoTime();

        while (element != null) {

            // set timeout
            if (System.nanoTime() - startTime > OPERATION_TIMEOUT_NANOS) {
                disableSystem("Timeout in unwrapConductiblePath");
                return null;
            }

            if (path.getLength() != 0) {
                NetworkPathKey<ElectricalConnectorBlockEntity> key = new NetworkPathKey<>(element.connector, path.getFirstNode());
                // Check if connection exists before getting conductivity
                if (!context.containsConnection(key) ||
                        context.getConnectionConductivity(key) <= 0)
                    return null;
            }

            path.addNodeToBeginning(element.connector);
            element = element.parent;
        }

        if (path.getLength() < 2)
            return null;

        return path;
    }

    protected NetworkPathConductivityContext getConductivityContext() {
        return context;
    }

    protected void setConductivityContext(NetworkPathConductivityContext context) {
        this.context = context;
    }

    protected void tick() {
        pathFindAttempts.set(0); // Reset counter each tick
        context.updateConductivity();
    }

    private record QueueElement(ElectricalConnectorBlockEntity connector, QueueElement parent, int depth) {
    }
}