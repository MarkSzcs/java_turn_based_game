package szofttech.csapat2.strategy.game.model;

import szofttech.csapat2.strategy.game.model.map.Map;

import java.util.Optional;

public class GameState {

	private final Map map;
	private final Player[] players;
	private int playerIndex;
	
	public GameState(Map map, Player[] players) {
		this.map = map;
		this.players = players;
		this.playerIndex = 0;
	}
	
	public Map getMap() {
		return map;
	}
	
	public Player getCurrentPlayer() {
		return players[playerIndex];
	}

	public Player[] getPlayers() {
		return players;
	}

	public Player getPlayerByColor(PlayerColor color) {
		return players[color.ordinal()];
	}
	
	public void nextTurn() {
		playerIndex = 1 - playerIndex;
	}
	
	public Optional<Player> getWinner() {
		return Optional.empty();
	}
	
}
