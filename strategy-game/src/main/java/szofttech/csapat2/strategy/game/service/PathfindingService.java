package szofttech.csapat2.strategy.game.service;

import szofttech.csapat2.strategy.game.model.Coordinate;
import szofttech.csapat2.strategy.game.model.GameState;
import szofttech.csapat2.strategy.game.model.PlayerColor;
import szofttech.csapat2.strategy.game.model.unit.Unit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class PathfindingService {
    
    private final AtomicReference<GameState> gameState;

    public PathfindingService(AtomicReference<GameState> gameState) {
        this.gameState = gameState;
    }

    public List<Coordinate> calculatePath(Unit unit, Coordinate destination) {
        Queue<Coordinate> queue = new LinkedList<>();
        queue.add(unit.getCoordinate());
        var parents = new HashMap<Coordinate, Optional<Coordinate>>();
        parents.put(unit.getCoordinate(), Optional.empty());

        while (!queue.isEmpty()) {
            var currentCoordinate = queue.poll();
            if (currentCoordinate.equals(destination)) {
                break;
            }

            var neighbors = getNeighbors(currentCoordinate, unit.getType().isCapableOfFlying(), unit, destination);
            for (var neighbor : neighbors) {
                if (!parents.containsKey(neighbor)) {
                    queue.add(neighbor);
                    parents.put(neighbor, Optional.of(currentCoordinate));
                }
            }
        }

        // Make sure that the path exists
        if (Objects.isNull(parents.get(destination)) || parents.get(destination).isEmpty()) {
            return Collections.emptyList();
        }

        // Build the path by walking the parent labels
        Coordinate current = destination;
        var coordinates = new ArrayList<Coordinate>();
        Optional<Coordinate> parent;
        while ((parent = Optional
                .ofNullable(parents.get(current))
                .flatMap(Function.identity())).isPresent()) {
            coordinates.add(current);
            current = parent.get();
        }
        Collections.reverse(coordinates);
        return coordinates;
    }

    private List<Coordinate> getNeighbors(Coordinate center, boolean canFly, Unit unit, Coordinate destination) {
        var neighbors = new ArrayList<Coordinate>();
        var map = gameState.get().getMap();
        var width = map.getWidth();
        var height = map.getHeight();
        for (var xOffset = -1; xOffset <= 1; ++xOffset) {
            for (var yOffset = -1; yOffset <= 1; ++yOffset) {
                // [0, 0] is not a valid offset
                if (xOffset == 0 && yOffset == 0) {
                    continue;
                }

                // If the absolute value of both x and y is 1 then we'd be stepping diagonally
                if (xOffset * xOffset + yOffset * yOffset == 2) {
                    continue;
                }
                var x = center.x() + xOffset;
                var y = center.y() + yOffset;

                // Do not step out of boundaries
                if (x < 0 || x >= width || y < 0 || y >= height) {
                    continue;
                }

                // Do not step on tiles that have unit/building on it
                var coordinate = new Coordinate(x, y);
                var tile = map.getTileAt(coordinate);
                if (tile.getOccupiedByUnit().isPresent() ||
                    tile.getOccupiedByBuilding().isPresent()) {
                    continue;
                }

                // If the unit cannot fly and there is an obstacle on the tile skip it
                if (!canFly && tile.isObstacle()) {
                    continue;
                }

                // If the tile has a resource on it then only ever add it to the list of neighbors if all of these are true:
                // - The tile is the destination tile
                // - The unit stepping is a worker that can work the resource type on the tile
                // - The resource is not full of workers already
                // - The resource is not currently captured by the opposing faction
                if (tile.getOccupiedByResource().isPresent()) {
                    var resource = tile.getOccupiedByResource().get();
                    if (!coordinate.equals(destination) ||
                            resource.isFull() ||
                            unit.getType().getCapturableResource().isEmpty() ||
                            unit.getType().getCapturableResource().get() != resource.getType()) {
                        continue;
                    }
                    var resourceColor = resource.getColor();
                    var unitColor = unit.getColor();
                    if (resourceColor != PlayerColor.NEUTRAL && resourceColor != unitColor) {
                        continue;
                    }
                }

                neighbors.add(coordinate);
            }
        }
        return neighbors;
    }

}
