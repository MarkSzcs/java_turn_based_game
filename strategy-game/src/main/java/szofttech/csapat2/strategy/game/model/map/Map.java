package szofttech.csapat2.strategy.game.model.map;

import szofttech.csapat2.strategy.game.model.Coordinate;

public class Map {
	
	private final int width;
	private final int height;
	private final Tile[] tiles;
	
	public Map(int width, int height, Tile[] tiles) {
		this.width = width;
		this.height = height;
		this.tiles = tiles;
		
		if (tiles.length != width * height) {
			throw new IllegalArgumentException("Width and height does not match the received number of tiles.");
		}
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public Tile getTileAt(Coordinate coordinate) {
		return tiles[coordinate.y() * width + coordinate.x()];
	}

	public void setTileAt(Coordinate coordinate, Tile tile){
		tiles[coordinate.y() * width + coordinate.x()] = tile;
	}

}
