package szofttech.csapat2.strategy.game.model.action;

import szofttech.csapat2.strategy.game.Assets;
import szofttech.csapat2.strategy.game.model.Compass;
import szofttech.csapat2.strategy.game.model.Constants;
import szofttech.csapat2.strategy.game.model.Coordinate;
import szofttech.csapat2.strategy.game.model.GameState;
import szofttech.csapat2.strategy.game.model.PlayerColor;
import szofttech.csapat2.strategy.game.model.building.Building;
import szofttech.csapat2.strategy.game.model.building.BuildingType;
import szofttech.csapat2.strategy.game.model.unit.Unit;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class BarracksConstruction implements Action {

    private final Unit worker;
    private final AtomicReference<GameState> gameState;
    private Coordinate selectedCoordinate = null;

    public BarracksConstruction(Unit worker, AtomicReference<GameState> gameState) {
        this.worker = worker;
        this.gameState = gameState;
    }

    public Unit getWorker() {
        return worker;
    }

    public void setSelectedCoordinate(Coordinate coordinate) {
        this.selectedCoordinate = coordinate;
    }

    @Override
    public ActionType getType() {
        return ActionType.BARRACKS_CONSTRUCTION;
    }

    @Override
    public void render(Graphics graphics) {
        var color = worker.getColor();
        var map = gameState.get().getMap();
        var graphics2d = (Graphics2D) graphics;
        var barrackImage = color == PlayerColor.BLUE
                ? Assets.BARRACKS_FRIENDLY.getImage()
                : Assets.BARRACKS_ENEMY.getImage();

        var oldComposite = graphics2d.getComposite();
        graphics2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        for (var compass : Compass.values()) {
            var coordinate = worker.getCoordinate().getNeighbor(compass);
            // Skip coordinates where a barrack cannot spawn
            if (!map.getTileAt(coordinate).isFreeToSpawn()){
                continue;
            }

            var x = coordinate.x() * Constants.TILE_SIZE;
            var y = coordinate.y() * Constants.TILE_SIZE;
            graphics2d.drawImage(barrackImage, x, y, Constants.TILE_SIZE, Constants.TILE_SIZE, null);
        }
        graphics2d.setComposite(oldComposite);
    }

    @Override
    public void execute() {
        if (Objects.isNull(selectedCoordinate)) {
            throw new IllegalStateException("Selected tile was not set before action execution.");
        }
        var map = gameState.get().getMap();
        var player = gameState.get().getCurrentPlayer();
        var resources = player.getResources();
        // Make sure that the player can afford to build the barrack
        if (!resources.canAfford(BuildingType.BARRACKS.getBuildingCost())) {
            return;
        }
        var barrack = new Building(
                selectedCoordinate,
                BuildingType.BARRACKS.getMaxHealth(),
                BuildingType.BARRACKS,
                worker.getColor(),
                Optional.empty(),
                new ArrayList<>());
        player.getBuildings().add(barrack);
        player.setPendingAction(Optional.empty());
        var selectedTile = map.getTileAt(selectedCoordinate);
        selectedTile.setOccupiedByBuilding(Optional.of(barrack));
        // Deduct the resources required to build the barrack
        resources.deduct(BuildingType.BARRACKS.getBuildingCost());
    }

}