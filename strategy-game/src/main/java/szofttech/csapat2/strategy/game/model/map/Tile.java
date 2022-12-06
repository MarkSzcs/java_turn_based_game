package szofttech.csapat2.strategy.game.model.map;

import lombok.Data;
import szofttech.csapat2.strategy.game.model.building.Building;
import szofttech.csapat2.strategy.game.model.unit.Unit;

import java.util.Optional;

@Data
public class Tile {

	private final int x;
	private final int y;
	private final boolean isObstacle;
	private Optional<Resource> occupiedByResource = Optional.empty();
	private Optional<Building> occupiedByBuilding = Optional.empty();
	private Optional<Unit> occupiedByUnit = Optional.empty();

	public void setOccupiedByBuilding(Optional<Building> occupiedByBuilding) {
		this.occupiedByBuilding = occupiedByBuilding;
	}
	
	public void setOccupiedByUnit(Optional<Unit> occupiedByUnit){
		this.occupiedByUnit = occupiedByUnit;
	}

	public boolean isFreeToSpawn() {
		return occupiedByResource.isEmpty() && occupiedByBuilding.isEmpty() && occupiedByUnit.isEmpty() && !isObstacle;
	}

}
