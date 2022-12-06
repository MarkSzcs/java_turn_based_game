package szofttech.csapat2.strategy.game.service;

import szofttech.csapat2.strategy.game.model.Compass;
import szofttech.csapat2.strategy.game.model.GameState;
import szofttech.csapat2.strategy.game.model.Player;
import szofttech.csapat2.strategy.game.model.Resources;
import szofttech.csapat2.strategy.game.model.building.Building;
import szofttech.csapat2.strategy.game.model.map.Resource;
import szofttech.csapat2.strategy.game.model.map.ResourceType;
import szofttech.csapat2.strategy.game.model.unit.Unit;
import szofttech.csapat2.strategy.game.model.unit.UnitType;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EndTurnService {

    private final AtomicReference<GameState> gameStateReference;

    public EndTurnService(AtomicReference<GameState> gameStateReference) {
        this.gameStateReference = gameStateReference;
    }

    public void endTurn() {
        var gameState = gameStateReference.get();
        var player = gameState.getCurrentPlayer();
        var resources = player.getResources();
        var capturedResources = resources.getCapturedResources();
        var resourcesByType = capturedResources.stream().collect(Collectors.groupingBy(Resource::getType));
        Function<ResourceType, Integer> getNumberOfWorkersOnResourceType = resourceType ->
                resourcesByType.getOrDefault(resourceType, Collections.emptyList()).stream()
                    .mapToInt(Resource::getNumberOfWorkers)
                    .sum();
        var plusGold = getNumberOfWorkersOnResourceType.apply(ResourceType.GOLD) * Resources.GOLD_PER_MINE_PER_WORKER_PER_TURN;
        var plusFood = getNumberOfWorkersOnResourceType.apply(ResourceType.FOOD) * Resources.FOOD_PER_FARM_PER_WORKER_PER_TURN;
        var plusWood = getNumberOfWorkersOnResourceType.apply(ResourceType.WOOD) * Resources.WOOD_PER_LUMBERMILL_PER_WORKER_PER_TURN;
        resources.addGold(plusGold);
        resources.addFood(plusFood);
        resources.addWood(plusWood);
        resources.resetActionPoints();
        player.setActiveUnit(Optional.empty());
        player.setPendingAction(Optional.empty());
        Arrays.stream(gameState.getPlayers())
                        .map(Player::getUnits)
                        .flatMap(Collection::stream)
                        .forEach(unit -> unit.setAttackedThisTurn(false));

        List<Building> busyBuildings = player.getBusyBuildings();
        //check for each actively producing building if 'spawn location' is occupied or is an obstacle, if not create a new unit for the player based on the type of the top element of the production queue
        //actuall set the given tile in the map to occupied
        var map = gameState.getMap();
        busyBuildings.forEach(building -> {
            UnitType uType = building.getIndexFromProdQueue(0);
            for (var compass : Compass.getPreferredCompass(player.getColor())) {
                var location = building.getSpawnLocation(compass);
                var tile = map.getTileAt(location);
                if (!tile.isFreeToSpawn()) {
                    continue;
                }
                Unit unitToSpawn = new Unit(building.getColor(), uType, location);
                player.getUnits().add(unitToSpawn);
                tile.setOccupiedByUnit(Optional.of(unitToSpawn));
                building.deleteFromProdQueue(0);
                break;
            }
        });
        
        gameState.nextTurn();
    }

}
