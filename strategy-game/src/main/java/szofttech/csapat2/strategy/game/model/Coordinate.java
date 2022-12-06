package szofttech.csapat2.strategy.game.model;

public record Coordinate(int x, int y) {
	
	public int manhattanDistance(Coordinate other) {
		return Math.abs(other.x - x) + Math.abs(other.y - y);
	}

	public Coordinate getNeighbor(Compass compass) {
		return switch(compass) {
			case NORTH -> new Coordinate(x, y - 1);
			case EAST -> new Coordinate(x + 1, y);
			case SOUTH -> new Coordinate(x, y + 1);
			case WEST -> new Coordinate(x - 1, y);
		};
	}

}
