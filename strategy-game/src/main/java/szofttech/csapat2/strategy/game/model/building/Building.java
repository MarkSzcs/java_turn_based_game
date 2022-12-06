package szofttech.csapat2.strategy.game.model.building;

import lombok.AllArgsConstructor;
import lombok.Data;
import szofttech.csapat2.strategy.game.model.Compass;
import szofttech.csapat2.strategy.game.model.Coordinate;
import szofttech.csapat2.strategy.game.model.Destructible;
import szofttech.csapat2.strategy.game.model.GameState;
import szofttech.csapat2.strategy.game.model.PlayerColor;
import szofttech.csapat2.strategy.game.model.Resources;
import szofttech.csapat2.strategy.game.model.Texture;
import szofttech.csapat2.strategy.game.model.unit.Unit;
import szofttech.csapat2.strategy.game.model.unit.UnitType;

import javax.swing.JOptionPane;
import java.util.List;
import java.util.Optional;

@Data
@AllArgsConstructor
public class Building implements Destructible {

	private Coordinate coordinate;
	private int health;
	private BuildingType type;
	private PlayerColor color;
	private Optional<Unit> currentlyProducing;
	private List<UnitType> productionQueue;

	public Texture getTexture() {
		return type.getTextureForColor(color);
	}

	@Override
	public int getMaxHealth() {
		return type.getMaxHealth();
	}

	@Override
	public void destruct(GameState gameState) {
		// If the main building is destructed the opposite player automatically wins
		if (type == BuildingType.MAIN_BUILDING) {
			var winner = color == PlayerColor.BLUE ? PlayerColor.RED : PlayerColor.BLUE;
			JOptionPane.showMessageDialog(null, "Congratulations, " + winner + " has won!");
		}
		gameState.getMap().getTileAt(coordinate).setOccupiedByBuilding(Optional.empty());
		gameState.getPlayerByColor(color).getBuildings().remove(this);
	}

	public void addToProdQueue(UnitType unitType){
		this.productionQueue.add(unitType);
	}
	
	public void deleteFromProdQueue(int index){
		this.productionQueue.remove(index);
	}
	public void dequeueProdQueue(){
		this.productionQueue.clear();
	}
	public UnitType getIndexFromProdQueue(int index){
		return this.productionQueue.get(index);
	}

	public boolean isProdQueueEmpty(){
		return this.productionQueue.isEmpty();
	}

	public Coordinate getSpawnLocation(Compass compass) {
		return switch(compass) {
			case NORTH -> new Coordinate(coordinate.x(), coordinate.y() - 1);
			case EAST -> new Coordinate(coordinate.x() + 1, coordinate.y());
			case SOUTH -> new Coordinate(coordinate.x(), coordinate.y() + 1);
			case WEST -> new Coordinate(coordinate.x() - 1, coordinate.y());
		};
	}

	public Resources getResourcesRequired(){
		Resources cost = new Resources(0, 0, 0, 0);
		this.productionQueue.forEach( (UnitType unit) -> {
			cost.setActionPoints( cost.getActionPoints() + unit.getCost().getActionPoints());
			cost.setFood( cost.getFood() + unit.getCost().getFood());
			cost.setWood( cost.getWood() + unit.getCost().getWood());
			cost.setGold( cost.getGold() + unit.getCost().getGold());
		});
		return cost;
	}
}

