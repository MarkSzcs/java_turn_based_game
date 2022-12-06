package szofttech.csapat2.strategy.game.model;

public interface Destructible {
    PlayerColor getColor();
    int getHealth();
    int getMaxHealth();
    void setHealth(int health);
    Coordinate getCoordinate();
    void destruct(GameState gameState);
}
