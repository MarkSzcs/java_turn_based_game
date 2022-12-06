package szofttech.csapat2.strategy.game.model.map;

import szofttech.csapat2.strategy.game.Assets;
import szofttech.csapat2.strategy.game.model.PlayerColor;
import szofttech.csapat2.strategy.game.model.Texture;

public enum ResourceType {
	GOLD(50, new Texture[] { Assets.GOLDMINE_FRIENDLY, Assets.GOLDMINE_ENEMY, Assets.GOLDMINE_NEUTRAL }),
	WOOD(50, new Texture[] { Assets.LUMBERMILL_FRIENDLY, Assets.LUMBERMILL_ENEMY, Assets.LUMBERMILL_NEUTRAL }),
	FOOD(50, new Texture[] { Assets.FARM_FRIENDLY, Assets.FARM_ENEMY, Assets.FARM_NEUTRAL });
	
    private final int maxHealth;
	private final Texture[] textures;

	ResourceType(int maxHealth, Texture[] textures) {
        this.maxHealth = maxHealth;
		this.textures = textures;
	}
    
    public int getMaxHealth() {
        return maxHealth;
    }
	
	public Texture getTextureForColor(PlayerColor color) {
		return textures[color.ordinal()];
	}
	
}
