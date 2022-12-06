package szofttech.csapat2.strategy.game.model;


public enum Compass {
    NORTH,
    EAST,
    SOUTH,
    WEST;

    public static Compass[] getPreferredCompass(PlayerColor color) {
        return switch (color) {
            case RED -> getPreferredCompassRed();
            case BLUE -> getPreferredCompassBlue();
            default -> throw new IllegalArgumentException("Compass requested for NEUTRAL color.");
        };
    }

    public static Compass[] getPreferredCompassBlue() {
        return new Compass[] { SOUTH, EAST, WEST, NORTH };
    }

    public static Compass[] getPreferredCompassRed() {
        return new Compass[] { NORTH, WEST, EAST, SOUTH };
    }

}
    

