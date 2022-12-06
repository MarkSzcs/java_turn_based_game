package szofttech.csapat2.strategy.game.model.unit;

import lombok.Data;
import szofttech.csapat2.strategy.game.model.Compass;
import szofttech.csapat2.strategy.game.model.Coordinate;
import szofttech.csapat2.strategy.game.model.Destructible;
import szofttech.csapat2.strategy.game.model.Direction;
import szofttech.csapat2.strategy.game.model.GameState;
import szofttech.csapat2.strategy.game.model.PlayerColor;
import szofttech.csapat2.strategy.game.model.Texture;

import java.util.Optional;

@Data
public class Unit implements Destructible {

	private final PlayerColor color;
	private final UnitType type;
	private Coordinate coordinate;
	private int health;
	private boolean attackedThisTurn;
	
	public Unit(PlayerColor color, UnitType type, Coordinate coordinate) {
		this.color = color;
		this.type = type;
		this.coordinate = coordinate;
		this.health = type.getMaxHealth();
		this.attackedThisTurn = false;
	}

	public Texture getTexture() {
		return type.getTextureForColorAndDirection(color, Direction.RIGHT);
	}

	@Override
	public int getMaxHealth() {
		return type.getMaxHealth();
	}

	@Override
	public void destruct(GameState gameState) {
		gameState.getMap().getTileAt(coordinate).setOccupiedByUnit(Optional.empty());
		gameState.getPlayerByColor(color).getUnits().remove(this);
	}
}
