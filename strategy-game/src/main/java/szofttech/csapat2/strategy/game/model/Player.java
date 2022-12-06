package szofttech.csapat2.strategy.game.model;

import lombok.Getter;
import lombok.Setter;
import szofttech.csapat2.strategy.game.model.action.Action;
import szofttech.csapat2.strategy.game.model.building.Building;
import szofttech.csapat2.strategy.game.model.unit.Unit;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
public class Player {

	private final PlayerColor color;
	private final Resources resources;
	private final List<Unit> units;
	private final List<Building> buildings;
	private Optional<Unit> activeUnit = Optional.empty();
	private Optional<Action> pendingAction = Optional.empty();

	public Player(PlayerColor color) {
		this(color, Resources.startingResources(), new ArrayList<>(), new ArrayList<>());
	}
	
	public Player(PlayerColor color, Resources resources, List<Unit> units, List<Building> buildings) {
		this.color = color;
		this.resources = resources;
		this.units = units;
		this.buildings = buildings;
	}

	public void updateResources(int actionPoints, int gold, int wood, int food){
		this.resources.update(actionPoints, gold, wood, food);
	}
	
	public Optional<Unit> getUnitAt(Coordinate coordinate) {
		return units.stream().filter(unit -> unit.getCoordinate().equals(coordinate)).findAny();
	}

	public Optional<Building> getBuildingAt(Coordinate coordinate) {
		return buildings.stream().filter(building -> building.getCoordinate().equals(coordinate)).findAny();
	}

	public List<Building> getBusyBuildings() {
		return buildings.stream().filter(building -> !(building.isProdQueueEmpty())).toList();
	}
}
