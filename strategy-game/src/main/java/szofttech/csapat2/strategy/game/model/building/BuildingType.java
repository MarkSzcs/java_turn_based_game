package szofttech.csapat2.strategy.game.model.building;

import szofttech.csapat2.strategy.game.Assets;
import szofttech.csapat2.strategy.game.model.PlayerColor;
import szofttech.csapat2.strategy.game.model.Resources;
import szofttech.csapat2.strategy.game.model.Texture;

public enum BuildingType {
	MAIN_BUILDING(300, new Resources(0, 30, 100, 0), new Texture[]{ Assets.HQ_FRIENDLY, Assets.HQ_ENEMY }),
	BARRACKS(150, new Resources(0, 10, 50, 0), new Texture[]{ Assets.BARRACKS_FRIENDLY, Assets.BARRACKS_ENEMY });
	
    private final int maxHealth;
	private final Resources buildingCost;
	private final Texture[] textures;
	
	BuildingType(int maxHealth, Resources buildingCost, Texture[] textures) {
        this.maxHealth = maxHealth;
		this.buildingCost = buildingCost;
		this.textures = textures;
	}
	
    public int getMaxHealth() {
        return maxHealth;
    }
    
	public Resources getBuildingCost() {
		return buildingCost;
	}
	
	public Texture getTextureForColor(PlayerColor color) {
		return textures[color.ordinal()];
	}
	
}
