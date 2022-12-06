package szofttech.csapat2.strategy.game.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import szofttech.csapat2.strategy.game.model.map.Resource;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class Resources {

    // Max values
	public static final int MAX_GOLD = 30;
	public static final int MAX_WOOD = 200;
	public static final int MAX_FOOD = 150;
	public static final int MAX_ACTION_POINTS = 90;
    
    // Resource / worker / turn values
    public static final int GOLD_PER_MINE_PER_WORKER_PER_TURN = 2;
    public static final int WOOD_PER_LUMBERMILL_PER_WORKER_PER_TURN = 5;
    public static final int FOOD_PER_FARM_PER_WORKER_PER_TURN = 3;
    
    // Starting values
	private static final int STARTING_ACTION_POINTS = 90;
	private static final int STARTING_GOLD = 15;
	private static final int STARTING_WOOD = 100;
	private static final int STARTING_FOOD = 75;
	
	public static Resources startingResources() {
		return new Resources(STARTING_ACTION_POINTS, STARTING_GOLD, STARTING_WOOD, STARTING_FOOD);
	}
	
	private int actionPoints;
	private int gold;
	private int wood;
	private int food;
    private final List<Resource> capturedResources = new ArrayList<>();
	
	public void deductActionPoints(int ap) {
		if (actionPoints < ap) {
			throw new IllegalStateException(String.format("Trying to deduct more AP (%d) than what the player has (%d).", ap, actionPoints));
		}
		actionPoints -= ap;
	}

	public void deduct(Resources resources) {
		this.actionPoints = Math.max(0, this.actionPoints - resources.getActionPoints());
		this.gold = Math.max(0, this.gold - resources.getGold());
		this.wood = Math.max(0, this.wood - resources.getWood());
		this.food = Math.max(0, this.food - resources.getFood());
	}

	public void update(int actionPoints, int gold, int wood, int food){
		this.actionPoints = this.actionPoints + actionPoints;
		this.gold = this.gold + gold;
		this.wood = this.wood + wood;
		this.food = this.food + food;
	}
	
	public void resetActionPoints() {
		this.actionPoints = MAX_ACTION_POINTS;
	}

	public void addGold(int gold) {
		this.gold = Math.min(MAX_GOLD, this.gold + gold);
	}

	public void addWood(int wood) {
		this.wood = Math.min(MAX_WOOD, this.wood + wood);
	}

	public void addFood(int food) {
		this.food = Math.min(MAX_FOOD, this.food + food);
	}

	public boolean canAfford(Resources cost) {
		return this.actionPoints >= cost.actionPoints &&
				this.gold >= cost.gold &&
				this.wood >= cost.wood &&
				this.food >= cost.food;
	}
	
}
