package szofttech.csapat2.strategy.game.model.map;

import szofttech.csapat2.strategy.game.model.Coordinate;
import szofttech.csapat2.strategy.game.model.Destructible;
import szofttech.csapat2.strategy.game.model.GameState;
import szofttech.csapat2.strategy.game.model.PlayerColor;
import szofttech.csapat2.strategy.game.model.Texture;
import szofttech.csapat2.strategy.game.model.unit.Unit;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Resource implements Destructible {

	public static final int MAX_WORKERS_ON_SAME_RESOURCE = 5;

	private final Coordinate coordinate;
	private final ResourceType type;
	private final List<Unit> currentWorkers;
	private PlayerColor color;
	private int health;
    
	public Resource(Coordinate coordinate, ResourceType type) {
		this.coordinate = coordinate;
		this.type = type;
		this.currentWorkers = new ArrayList<>();
		this.color = PlayerColor.NEUTRAL;
        this.health = type.getMaxHealth();
	}

	@Override
	public Coordinate getCoordinate() {
		return coordinate;
	}

	public ResourceType getType() {
		return type;
	}
	
	public int getNumberOfWorkers() {
		return currentWorkers.size();
	}
	
	public void addWorker(Unit worker) {
		var capturableResource = worker.getType().getCapturableResource();
		if (capturableResource.isEmpty() || capturableResource.get() != type) {
			throw new IllegalArgumentException("Only matching worker types can be assigned to resources.");
		}
		currentWorkers.add(worker);
	}

	@Override
	public PlayerColor getColor() {
		return color;
	}

	@Override
    public int getHealth() {
        return health;
    }

	@Override
	public int getMaxHealth() {
		return type.getMaxHealth();
	}

	@Override
	public void setHealth(int health) {
		this.health = health;
	}

	public void setColor(PlayerColor color) {
		this.color = color;
	}
	
	public Texture getTexture() {
		return type.getTextureForColor(color);
	}
	
	public boolean isFull() {
		return currentWorkers.size() >= MAX_WORKERS_ON_SAME_RESOURCE;
	}

	@Override
	public void destruct(GameState gameState) {
		if (color == PlayerColor.NEUTRAL) {
			throw new IllegalStateException("Neutral resources cannot be destructed.");
		}
		var map = gameState.getMap();
		var player = gameState.getPlayerByColor(color);
		map.getTileAt(coordinate).setOccupiedByResource(Optional.empty());
		player.getResources().getCapturedResources().remove(this);
		for (var worker : currentWorkers) {
			player.getUnits().remove(worker);
		}
	}
}
