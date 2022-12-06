package szofttech.csapat2.strategy.game.model.action;

import java.awt.Graphics;

public interface Action {

	ActionType getType();
	void render(Graphics graphics);
	void execute();
	
}
