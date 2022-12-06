package szofttech.csapat2.strategy.game.model.action;

import szofttech.csapat2.strategy.game.Assets;
import szofttech.csapat2.strategy.game.model.Constants;
import szofttech.csapat2.strategy.game.model.Coordinate;
import szofttech.csapat2.strategy.game.model.GameState;
import szofttech.csapat2.strategy.game.model.unit.Unit;

import java.awt.Graphics;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public record MoveAction(
		Unit unit,
		AtomicReference<GameState> gameState,
		List<Coordinate> coordinates,
		int playerAp) implements Action {
	
	@Override
	public ActionType getType() {
		return ActionType.MOVE;
	}
	
	public void render(Graphics graphics) {
		var requiredAp = 0;
		for (var coordinate : coordinates) {
			requiredAp += Constants.AP_PER_DISTANCE;
			var x = coordinate.x() * Constants.TILE_SIZE;
			var y = coordinate.y() * Constants.TILE_SIZE;
			graphics.drawImage(requiredAp > playerAp
					? Assets.MOVE_X_NO_AP.getImage()
					: Assets.MOVE_X.getImage(), x, y, Constants.TILE_SIZE, Constants.TILE_SIZE, null);
		}
	}
	
	public int apCost() {
		return Math.min(playerAp / Constants.AP_PER_DISTANCE, coordinates.size()) * Constants.AP_PER_DISTANCE;
	}

	public Optional<Coordinate> lastCoordinate() {
		if (coordinates.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(coordinates.get(coordinates.size() - 1));
	}

	public Optional<Coordinate> lastCoordinateForAP() {
		var tilesToTravel = playerAp / Constants.AP_PER_DISTANCE;
		if (tilesToTravel < 1) {
			return Optional.empty();
		}
		return Optional.of(coordinates.get(Math.min(tilesToTravel - 1, coordinates.size() - 1)));
	}

	@Override
	public void execute() {
		if (coordinates.isEmpty()) {
			return;
		}
		var maybeLastTileForAp = lastCoordinateForAP();
		if (maybeLastTileForAp.isEmpty()) {
			return;
		}
		var lastTileForAp = maybeLastTileForAp.get();
		var previousCoordinate = unit.getCoordinate();
		var map = gameState.get().getMap();
		map.getTileAt(previousCoordinate).setOccupiedByUnit(Optional.empty());

		unit.setCoordinate(lastTileForAp);
		map.getTileAt(lastTileForAp).setOccupiedByUnit(Optional.of(unit));
		var player = gameState.get().getPlayerByColor(unit.getColor());
		var apCost = apCost();
		player.getResources().deductActionPoints(apCost);
		player.setPendingAction(Optional.empty());
		
		// Exit early if the moved unit is not a worker or if the last tile for AP is not a resource
		var maybeLastTileResource = map.getTileAt(lastTileForAp).getOccupiedByResource();
		if (unit.getType().getCapturableResource().isEmpty() || maybeLastTileResource.isEmpty()) {
			return;
		}
		var lastTileResource = maybeLastTileResource.get();
		var capturableResource = unit.getType().getCapturableResource().get();
		if (lastTileResource.getType() != capturableResource || lastTileResource.isFull()) {
			return;
		}
		// Once workers enter a resource they should not block that tile anymore
		map.getTileAt(lastTileForAp).setOccupiedByUnit(Optional.empty());
		lastTileResource.setColor(unit.getColor());
		lastTileResource.addWorker(unit);
        player.getResources().getCapturedResources().add(lastTileResource);
	}
	
}
