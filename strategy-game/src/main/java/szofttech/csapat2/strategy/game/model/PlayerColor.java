package szofttech.csapat2.strategy.game.model;

import java.awt.*;

public enum PlayerColor {

	BLUE(Color.BLUE),
	RED(Color.RED),
	NEUTRAL(Color.WHITE);

	private final Color swingColor;

	PlayerColor(Color swingColor) {
		this.swingColor = swingColor;
	}

	public Color toSwingColor() {
		return swingColor;
	}

}
