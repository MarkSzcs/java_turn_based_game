package szofttech.csapat2.strategy.game.input;

import szofttech.csapat2.strategy.game.model.Coordinate;
import szofttech.csapat2.strategy.game.model.Destructible;
import szofttech.csapat2.strategy.game.model.GameState;
import szofttech.csapat2.strategy.game.model.PlayerColor;
import szofttech.csapat2.strategy.game.model.action.ActionType;
import szofttech.csapat2.strategy.game.model.action.AttackAction;
import szofttech.csapat2.strategy.game.model.action.BarracksConstruction;
import szofttech.csapat2.strategy.game.model.action.MoveAction;
import szofttech.csapat2.strategy.game.model.building.BuildingType;
import szofttech.csapat2.strategy.game.model.unit.Unit;
import szofttech.csapat2.strategy.game.service.PathfindingService;
import szofttech.csapat2.strategy.game.view.BarracksMenu;
import szofttech.csapat2.strategy.game.view.HqMenu;

import javax.swing.JOptionPane;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class InputListener {

	private final AtomicReference<GameState> gameState;
	private final PathfindingService pathfindingService;
	private final Runnable repaintGamePanel;
	private final Runnable repaintResources;
	
	public InputListener(
			AtomicReference<GameState> gameState,
			PathfindingService pathfindingService,
			Runnable repaintGamePanel,
			Runnable repaintResources) {
		this.gameState = gameState;
		this.pathfindingService = pathfindingService;
		this.repaintGamePanel = repaintGamePanel;
		this.repaintResources = repaintResources;
	}

	public void handleButtonPress(int keyCode) {
		// Currently we only handle 'B' for barrack building
		if (keyCode != KeyEvent.VK_B) {
			return;
		}
		var player = gameState.get().getCurrentPlayer();
		var maybeUnit = player.getActiveUnit();

		// If there is no active unit or it is not a worker exit early
		if (maybeUnit.isEmpty() || !maybeUnit.get().getType().isWorker()) {
			return;
		}
		var unit = maybeUnit.get();
		var availableResources = player.getResources();
		var requiredResources = BuildingType.BARRACKS.getBuildingCost();

		// Check that the player can afford the barrack
		if(!availableResources.canAfford(requiredResources)) {
			JOptionPane.showMessageDialog(
					null,
					"You don't have enough resources to build a barrack.",
					"Not enough resources",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		player.setPendingAction(Optional.of(new BarracksConstruction(unit, gameState)));
		repaintGamePanel.run();
	}

	public void handleClick(MouseEvent e, Coordinate coordinate) {
		var player = gameState.get().getCurrentPlayer();
		var map = gameState.get().getMap();
		var tile = map.getTileAt(coordinate);
		var maybePendingAction = player.getPendingAction();

		// Handle left button presses
		if (e.getButton() == MouseEvent.BUTTON1) {
			// If there is a pending barrack construction check if this is an execution attempt
			if (maybePendingAction.isPresent() &&
					maybePendingAction.get().getType() == ActionType.BARRACKS_CONSTRUCTION) {
				var buildingAction = (BarracksConstruction) maybePendingAction.get();
				if (handleBarrackConstruction(buildingAction, coordinate)) {
					return;
				}
			}

			// If there is a building on the tile display the appropriate menu
			if (tile.getOccupiedByBuilding().isPresent()) {
				var building = tile.getOccupiedByBuilding().get();
				// Make sure that the building is owned by the current player
				if (building.getColor() != player.getColor()) {
					return;
				}
				// Display the appropriate menu
				switch (building.getType()) {
					case BARRACKS -> new BarracksMenu(building, gameState, repaintResources);
					case MAIN_BUILDING -> new HqMenu(building, gameState, repaintResources);
				}
			}

			// Otherwise try to select a friendly unit on the tile on left click and clear all pending actions
			player.setActiveUnit(player.getUnitAt(coordinate));

			player.setPendingAction(Optional.empty());
			repaintGamePanel.run();
			return;
		}

		// Now we only want to respond to right click events, and only if there is an active unit
		if (e.getButton() != MouseEvent.BUTTON3 || player.getActiveUnit().isEmpty()) {
			return;
		}

		var activeUnit = player.getActiveUnit().get();
		// If there is already a pending move action to the target coordinate, execute it and clear the pending action
		if (maybePendingAction.isPresent() && maybePendingAction.get().getType() == ActionType.MOVE) {
			var moveAction = (MoveAction) maybePendingAction.get();
			var lastCoordinate = moveAction.lastCoordinate();
			if (lastCoordinate.isPresent() && lastCoordinate.get().equals(coordinate)) {
				maybePendingAction.get().execute();
				repaintGamePanel.run();
				repaintResources.run();
				return;
			}
		}

		// Otherwise simply create a pending move action to the target coordinate
		var coordinates = pathfindingService.calculatePath(activeUnit, coordinate);

		// If there is an enemy unit/building/resource on the tile clicked we need an attack action
		var playerColor = player.getColor();
		var tileAtClickLocation = map.getTileAt(coordinate);
		Optional<Destructible> maybeAttacked = Optional.empty();
		var maybeEnemyUnit = tileAtClickLocation.getOccupiedByUnit();
		var maybeEnemyBuilding = tileAtClickLocation.getOccupiedByBuilding();
		var maybeEnemyResource = tileAtClickLocation.getOccupiedByResource();
		if (maybeEnemyUnit.isPresent() && maybeEnemyUnit.get().getColor() != playerColor) {
			maybeAttacked = Optional.of(maybeEnemyUnit.get());
		}
		else if (maybeEnemyBuilding.isPresent() && maybeEnemyBuilding.get().getColor() != playerColor) {
			maybeAttacked = Optional.of(maybeEnemyBuilding.get());
		}
		else if (maybeEnemyResource.isPresent()) {
			var resourceColor = maybeEnemyResource.get().getColor();
			if (resourceColor != PlayerColor.NEUTRAL && resourceColor != playerColor) {
				maybeAttacked = Optional.of(maybeEnemyResource.get());
			}
		}
		// If we found an attacked target right next to our unit then we need to handle the attacking
		if (maybeAttacked.isPresent()) {
			var attacked = maybeAttacked.get();
			var attackedCoordinates = attacked.getCoordinate();
			if (attackedCoordinates.manhattanDistance(activeUnit.getCoordinate()) <= 1) {
				handleAttacking(activeUnit, attacked);
				return;
			}
		}

		// If the coordinates is empty now do nothing
		if (coordinates.isEmpty()) {
			return;
		}

		// Otherwise this is simply a move action
		player.setPendingAction(Optional.of(new MoveAction(
				activeUnit, gameState, coordinates, player.getResources().getActionPoints())));
		repaintGamePanel.run();
	}

	private void handleAttacking(Unit attacker, Destructible attacked) {
		// If the unit has already attacked this turn do nothing
		if (attacker.isAttackedThisTurn()) {
			return;
		}
		var action = new AttackAction(gameState, attacker, attacked);
		var player = gameState.get().getCurrentPlayer();
		var maybePendingAction = player.getPendingAction();
		// Check if there is already a pending attack between these two
		if (maybePendingAction.isPresent() && maybePendingAction.get().getType() == ActionType.ATTACK) {
			var pendingAction = maybePendingAction.get();
			// If the two actions are the same execute the action
			if (pendingAction.equals(action)) {
				pendingAction.execute();
				player.setPendingAction(Optional.empty());
				repaintGamePanel.run();
				return;
			}
		}

		// Otherwise simply add a pending attack action
		player.setPendingAction(Optional.of(action));
		repaintGamePanel.run();
	}

	private boolean handleBarrackConstruction(
			BarracksConstruction barracksConstruction,
			Coordinate clickedCoordinate) {
		var worker = barracksConstruction.getWorker();
		if (clickedCoordinate.manhattanDistance(worker.getCoordinate()) != 1) {
			return false;
		}
		var map = gameState.get().getMap();
		var tile = map.getTileAt(clickedCoordinate);
		if (!tile.isFreeToSpawn()) {
			return false;
		}
		barracksConstruction.setSelectedCoordinate(clickedCoordinate);
		barracksConstruction.execute();
		repaintGamePanel.run();
		repaintResources.run();
		return true;
	}

}
