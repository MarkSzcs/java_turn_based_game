package szofttech.csapat2.strategy.game;

import szofttech.csapat2.strategy.game.model.Coordinate;
import szofttech.csapat2.strategy.game.model.GameState;
import szofttech.csapat2.strategy.game.model.PlayerColor;
import szofttech.csapat2.strategy.game.model.Resources;
import szofttech.csapat2.strategy.game.model.map.Resource;
import szofttech.csapat2.strategy.game.model.map.ResourceType;
import szofttech.csapat2.strategy.game.util.ImageUtil;

import java.awt.Color;
import java.awt.Graphics;
import java.util.concurrent.atomic.AtomicReference;

import static szofttech.csapat2.strategy.game.model.Constants.TILE_SIZE;

public class Renderer {

	private static final int RESOURCES_GAP_PX = 10;
	
	private final AtomicReference<GameState> gameState;

	public Renderer(AtomicReference<GameState> gameState) {
		this.gameState = gameState;
	}
	
	public void renderGamePanel(Graphics graphics, int width, int height) {
		graphics.clearRect(0, 0, width, height);
		if (gameState.get() == null) {
			return;
		}
		
		var player = gameState.get().getCurrentPlayer();
		var map = gameState.get().getMap();
		var mapWidth = map.getWidth();
		var mapHeight = map.getHeight();
		
		// Render map
		for (var y = 0; y < mapHeight; ++y) {
			for (var x = 0; x < mapWidth; ++x) {
				var tile = map.getTileAt(new Coordinate(x, y));
				// Render tile
				graphics.drawImage(Assets.BASIC_TILE.getImage(), x * TILE_SIZE, y* TILE_SIZE, TILE_SIZE, TILE_SIZE, null);
				
				// Render obstacle
				if (tile.isObstacle()) {
					graphics.drawImage(Assets.OBSTACLE.getImage(), x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE, null);
				}
				// Render resource if it is present
				if (tile.getOccupiedByResource().isPresent()) {
					var resource = tile.getOccupiedByResource().get();
					var resourceTexture = resource.getTexture();
					graphics.drawImage(resourceTexture.getImage(), x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE, null);
				}
				// Render building if it is present
				if (tile.getOccupiedByBuilding().isPresent()) {
					var building = tile.getOccupiedByBuilding().get();
					var buildingTexture = building.getTexture();
					graphics.drawImage(buildingTexture.getImage(), x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE, null);
				}
				// Render unit if it is present
				if (tile.getOccupiedByUnit().isPresent()) {
					var unit = tile.getOccupiedByUnit().get();
					var unitTexture = unit.getTexture();
					
					// Check if the unit is active, in which case we need to highlight it
					var isUnitActive = player.getActiveUnit().isPresent() && player.getActiveUnit().get().equals(unit);
					var finalTexture = isUnitActive
							? ImageUtil.createHighlightedImage(unitTexture.getImage())
							: unitTexture.getImage();
					
					graphics.drawImage(finalTexture, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE, null);
				}
				
				// Render border
				graphics.setColor(Color.GRAY);
				graphics.drawRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
			}
		}
        
        // Render overlay elements that might overlap with other tiles
        for (var y = 0; y < mapHeight; ++y) {
			for (var x = 0; x < mapWidth; ++x) {
				var tile = map.getTileAt(new Coordinate(x, y));
                if (tile.getOccupiedByResource().isPresent()) {
                    var resource = tile.getOccupiedByResource().get();
					var occupationStr = String.format("%d/%d", resource.getNumberOfWorkers(), Resource.MAX_WORKERS_ON_SAME_RESOURCE);
					var fontMetrics = graphics.getFontMetrics();
					var stringWidth = fontMetrics.stringWidth(occupationStr);
					var stringHeight = fontMetrics.getHeight();
					graphics.setColor(new Color(1.0f, 1.0f, 1.0f, 0.5f));
					graphics.fillRect(x * TILE_SIZE, y * TILE_SIZE + 5, stringWidth, stringHeight);
					graphics.setColor(Color.black);
					graphics.drawString(occupationStr, x * TILE_SIZE, y * TILE_SIZE + stringHeight + 5);
                    renderHealthbar(graphics, x, y, resource.getHealth(), resource.getType().getMaxHealth());
                }
                else if (tile.getOccupiedByBuilding().isPresent()) {
                    var building = tile.getOccupiedByBuilding().get();
                    renderHealthbar(graphics, x, y, building.getHealth(), building.getType().getMaxHealth());
                }
                else if (tile.getOccupiedByUnit().isPresent()) {
                    var unit = tile.getOccupiedByUnit().get();
					renderHealthbar(graphics, x, y, unit.getHealth(), unit.getType().getMaxHealth());
					if (unit.isAttackedThisTurn()) {
						graphics.drawImage(Assets.TIRED.getImage(), x * TILE_SIZE + 5, y * TILE_SIZE + 5, 15, 15, null);
					}
                }
            }
        }
		
		// Render the player's pending action if possible
		player.getPendingAction().ifPresent(action -> action.render(graphics));
	}

	public void renderResourcesPanel(Graphics graphics, int width, int height) {
		if (gameState.get() == null) {
			return;
		}
		
        var player = gameState.get().getCurrentPlayer();
        var color = player.getColor();
		var resources = player.getResources();
        var pendingWood = calculatePendingResourceOfColorAndType(
            color, ResourceType.WOOD, Resources.WOOD_PER_LUMBERMILL_PER_WORKER_PER_TURN);
        var pendingFood = calculatePendingResourceOfColorAndType(
            color, ResourceType.FOOD, Resources.FOOD_PER_FARM_PER_WORKER_PER_TURN);
        var pendingGold = calculatePendingResourceOfColorAndType(
            color, ResourceType.GOLD, Resources.GOLD_PER_MINE_PER_WORKER_PER_TURN);

		var playerString = "Player: ";
		var woodString = resourceString("Wood", Resources.MAX_WOOD, resources.getWood(), pendingWood);
		var foodString = resourceString("Food", Resources.MAX_FOOD, resources.getFood(), pendingFood);
		var goldString = resourceString("Gold", Resources.MAX_GOLD, resources.getGold(), pendingGold);
		var apString = resourceString("AP", Resources.MAX_ACTION_POINTS, resources.getActionPoints(), 0);
		
		var fontMetrics = graphics.getFontMetrics();
		var fontHeight = fontMetrics.getHeight();
		var playerStringWidth = fontMetrics.stringWidth(playerString);
		var woodStringWidth = fontMetrics.stringWidth(woodString);
		var foodStringWidth = fontMetrics.stringWidth(foodString);
		var goldStringWidth = fontMetrics.stringWidth(goldString);
		
		var y = height / 2 + fontHeight / 2;
		graphics.clearRect(0, 0, width, height);
		graphics.drawString(playerString, RESOURCES_GAP_PX, y);
		graphics.setColor(color.toSwingColor());
		graphics.fillRect(RESOURCES_GAP_PX + playerStringWidth, y - 15, 15, 15);
		graphics.setColor(Color.BLACK);
		graphics.drawRect(RESOURCES_GAP_PX + playerStringWidth, y - 15, 15, 15);

		var playerOffset = playerStringWidth + 15 + RESOURCES_GAP_PX;
		graphics.drawString(woodString, RESOURCES_GAP_PX + playerOffset, y);
		graphics.drawString(foodString, RESOURCES_GAP_PX * 2 + playerOffset + woodStringWidth, y);
		graphics.drawString(goldString, RESOURCES_GAP_PX * 3 + playerOffset + woodStringWidth + foodStringWidth, y);
		graphics.drawString(apString, RESOURCES_GAP_PX * 4 + playerOffset + woodStringWidth + foodStringWidth + goldStringWidth, y);
	}
	
    private void renderHealthbar(Graphics graphics, int x, int y, int health, int maxHealth) {
        var healthStr = String.format("%d", health);
        var healthStrWidth = graphics.getFontMetrics().stringWidth(healthStr);
        var minHealthBarWidth = (int) (TILE_SIZE * 0.8);
        var healthBarWidth = Math.max(healthStrWidth, minHealthBarWidth);
        var healthBarHeight = (int) (TILE_SIZE * 0.2);
        var healthBarX = (int) (x * TILE_SIZE + TILE_SIZE * 0.5 - healthBarWidth * 0.5);
        var healthBarY = (y + 1) * TILE_SIZE - healthBarHeight - 5;
		var healthStrX = (int) (healthBarX + healthBarWidth / 2.0 - healthStrWidth / 2.0);
        float healthRatio = health / (float) maxHealth;
        float missingHealthRatio = 1.0f - healthRatio;
        graphics.setColor(new Color(missingHealthRatio, healthRatio, 0.0f, 0.5f));
        graphics.fillRect(healthBarX, healthBarY, healthBarWidth, healthBarHeight);
        graphics.setColor(Color.black);
        graphics.drawString(healthStr, healthStrX, healthBarY + healthBarHeight);
    }
    
    private int calculatePendingResourceOfColorAndType(PlayerColor color, ResourceType type, int multiplier) {
        return (int) gameState.get().getCurrentPlayer().getResources()
            .getCapturedResources().stream()
            .filter(resource -> resource.getColor() == color)
            .filter(resource -> resource.getType() == type)
            .map(Resource::getNumberOfWorkers)
            .count() * multiplier;
    }
    
	private static String resourceString(String resourceName, int maxResource, int currentResource, int pendingResource) {
		return String.format("%s: %d/%d (+%d)", resourceName, maxResource, currentResource, pendingResource);
	}
	
}