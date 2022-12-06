package szofttech.csapat2.strategy.game;

import szofttech.csapat2.strategy.game.model.Coordinate;
import szofttech.csapat2.strategy.game.model.GameState;
import szofttech.csapat2.strategy.game.model.Player;
import szofttech.csapat2.strategy.game.model.PlayerColor;
import szofttech.csapat2.strategy.game.model.building.Building;
import szofttech.csapat2.strategy.game.model.building.BuildingType;
import szofttech.csapat2.strategy.game.model.map.Map;
import szofttech.csapat2.strategy.game.model.map.Resource;
import szofttech.csapat2.strategy.game.model.map.ResourceType;
import szofttech.csapat2.strategy.game.model.map.Tile;
import szofttech.csapat2.strategy.game.model.unit.Unit;
import szofttech.csapat2.strategy.game.model.unit.UnitType;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;

public class GameStateLoader {
	
	private GameStateLoader() {}
	
	public static GameState loadGameState(int mapWidth, int mapHeight) {
		var tiles = new Tile[mapWidth * mapHeight];
		var player1 = new Player(PlayerColor.BLUE);
		var player2 = new Player(PlayerColor.RED);
		
		// Initialize all tiles to basic tiles
		for (var y = 0; y < mapHeight; ++y) {
			for (var x = 0; x < mapWidth; ++x) {
				tiles[y * mapWidth + x] = new Tile(x, y, false);
			}
		}
		
		// Load the map from a text file
		try (var scanner = new Scanner(GameStateLoader.class.getResourceAsStream("/map.txt"))) {
			String line;
			for (int y = 0; y < mapHeight; ++y) {
				line = scanner.nextLine();
				for (int x = 0; x < mapWidth; ++x) {
					var tileIndex = y * mapWidth + x;
					char c = line.charAt(x);
					switch (c) {
						case 'x' -> {
							var building = new Building(
								new Coordinate(x, y),
                                BuildingType.MAIN_BUILDING.getMaxHealth(),
                                BuildingType.MAIN_BUILDING,
                                player1.getColor(),
                                Optional.empty(),
								new ArrayList<>());
							player1.getBuildings().add(building);
							tiles[tileIndex].setOccupiedByBuilding(Optional.of(building));
						}
						case 'y' -> {
							var building = new Building(
								new Coordinate(x, y),
                                BuildingType.MAIN_BUILDING.getMaxHealth(),
                                BuildingType.MAIN_BUILDING,
                                player2.getColor(),
                                Optional.empty(),
								new ArrayList<>()
								);
							player2.getBuildings().add(building);
							tiles[tileIndex].setOccupiedByBuilding(Optional.of(building));
						}
						case 'm' -> {
							var blueMiner = new Unit(player1.getColor(), UnitType.MINER, new Coordinate(x,y));
							player1.getUnits().add(blueMiner);
							tiles[tileIndex].setOccupiedByUnit(Optional.of(blueMiner));
						}
						case 'n' -> {
							var redMiner = new Unit(player2.getColor(), UnitType.MINER, new Coordinate(x,y));
							player2.getUnits().add(redMiner);
							tiles[tileIndex].setOccupiedByUnit(Optional.of(redMiner));
						}
						case 'f' -> {
							var blueFarmer = new Unit(player1.getColor(), UnitType.FARMER, new Coordinate(x,y));
							player1.getUnits().add(blueFarmer);
							tiles[tileIndex].setOccupiedByUnit(Optional.of(blueFarmer));
						}
						case 'g' -> {
							var redFarmer = new Unit(player2.getColor(), UnitType.FARMER, new Coordinate(x,y));
							player2.getUnits().add(redFarmer);
							tiles[tileIndex].setOccupiedByUnit(Optional.of(redFarmer));
						}
						case 'k' -> {
							var blueLumberer = new Unit(player1.getColor(), UnitType.LUMBERER, new Coordinate(x,y));
							player1.getUnits().add(blueLumberer);
							tiles[tileIndex].setOccupiedByUnit(Optional.of(blueLumberer));
						}
						case 'l' -> {
							var redLumberer = new Unit(player2.getColor(), UnitType.LUMBERER, new Coordinate(x,y));
							player2.getUnits().add(redLumberer);
							tiles[tileIndex].setOccupiedByUnit(Optional.of(redLumberer));
						}
						case '-' -> {
							tiles[tileIndex].setOccupiedByResource(Optional.of(new Resource(new Coordinate(x, y), ResourceType.GOLD)));
						}
						case ';' -> {
							tiles[tileIndex].setOccupiedByResource(Optional.of(new Resource(new Coordinate(x, y), ResourceType.FOOD)));
						}
						case '+' -> {
							tiles[tileIndex].setOccupiedByResource(Optional.of(new Resource(new Coordinate(x, y), ResourceType.WOOD)));
						}
						case '#' -> tiles[tileIndex] = new Tile(x, y, true);
						case '.' -> {}
						default -> throw new IllegalArgumentException("Unknown character '" + c + "'.");
					}
				}
			}
		}
		
		var map = new Map(mapWidth, mapHeight, tiles);
		var players = new Player[] { player1, player2 };
		return new GameState(map, players);
	}
	
}
