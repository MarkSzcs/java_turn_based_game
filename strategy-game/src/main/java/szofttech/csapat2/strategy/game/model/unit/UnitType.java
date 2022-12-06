package szofttech.csapat2.strategy.game.model.unit;

import lombok.Getter;
import szofttech.csapat2.strategy.game.Assets;
import szofttech.csapat2.strategy.game.model.Direction;
import szofttech.csapat2.strategy.game.model.PlayerColor;
import szofttech.csapat2.strategy.game.model.Resources;
import szofttech.csapat2.strategy.game.model.Texture;
import szofttech.csapat2.strategy.game.model.map.ResourceType;

import java.util.Optional;

@Getter
public enum UnitType {

	MINER(10, 1, 1, 1, false, new Resources(0, 2, 0, 5), new Texture[] {
			Assets.MINER_FRIENDLY_RIGHT, Assets.MINER_FRIENDLY_LEFT,
			Assets.MINER_ENEMY_RIGHT, Assets.MINER_ENEMY_LEFT },
			Optional.of(ResourceType.GOLD)),
	LUMBERER(10, 1, 1, 1, false, new Resources(0, 2, 0, 5), new Texture[] {
			Assets.LUMBERER_FRIENDLY_RIGHT, Assets.LUMBERER_FRIENDLY_LEFT,
			Assets.LUMBERER_ENEMY_RIGHT, Assets.LUMBERER_ENEMY_LEFT },
			Optional.of(ResourceType.WOOD)),
	FARMER(10, 1, 1, 1, false, new Resources(0, 2, 0, 5), new Texture[] {
			Assets.FARMER_FRIENDLY_RIGHT, Assets.FARMER_FRIENDLY_LEFT,
			Assets.FARMER_ENEMY_RIGHT, Assets.FARMER_ENEMY_LEFT },
			Optional.of(ResourceType.FOOD)),
	PEASANT(20, 5, 2, 1, false, new Resources(0, 5, 0, 10), new Texture[] {
			Assets.PEASANT_FRIENDLY_RIGHT, Assets.PEASANT_FRIENDLY_LEFT,
			Assets.PEASANT_ENEMY_RIGHT, Assets.PEASANT_ENEMY_LEFT },
			Optional.empty()),
	SOLDIER(30, 10, 3, 1, false, new Resources(0, 7, 0, 15), new Texture[] {
			Assets.SOLDIER_FRIENDLY_RIGHT, Assets.SOLDIER_FRIENDLY_LEFT,
			Assets.SOLDIER_ENEMY_RIGHT, Assets.SOLDIER_ENEMY_LEFT },
			Optional.empty()),
	KNIGHT(50, 25, 5, 2, false, new Resources(0, 10, 0, 25), new Texture[] {
			Assets.KNIGHT_FRIENDLY_RIGHT, Assets.KNIGHT_FRIENDLY_LEFT,
			Assets.KNIGHT_ENEMY_RIGHT, Assets.KNIGHT_ENEMY_LEFT },
			Optional.empty()),
	DRAGON(100, 50, 10, 5, true, new Resources(0, 20, 0, 50), new Texture[] {
			Assets.DRAGON_FRIENDLY_RIGHT, Assets.DRAGON_FRIENDLY_LEFT,
			Assets.DRAGON_ENEMY_RIGHT, Assets.DRAGON_ENEMY_LEFT },
			Optional.empty());
	
	private final int maxHealth;
	private final int attack;
	private final int defense;
	private final int speed;
	private final boolean capableOfFlying;
	private final Resources cost;
	private final Texture[] textures;
	private final Optional<ResourceType> capturableResource;

	UnitType(
			int maxHealth, int attack, int defense, int speed,
			boolean capableOfFlying, Resources cost, Texture[] textures,
			Optional<ResourceType> capturableResource) {
		this.maxHealth = maxHealth;
		this.attack = attack;
		this.defense = defense;
		this.speed = speed;
		this.capableOfFlying = capableOfFlying;
		this.cost = cost;
		this.textures = textures;
		this.capturableResource = capturableResource;
	}
	
	public Texture getTextureForColorAndDirection(PlayerColor color, Direction direction) {
		return textures[color.ordinal() * 2 + direction.ordinal()];
	}

	public boolean isWorker() {
		return this == MINER || this == LUMBERER || this == FARMER;
	}

}