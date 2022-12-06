package szofttech.csapat2.strategy.game.model.action;

import szofttech.csapat2.strategy.game.Assets;
import szofttech.csapat2.strategy.game.model.Constants;
import szofttech.csapat2.strategy.game.model.Destructible;
import szofttech.csapat2.strategy.game.model.GameState;
import szofttech.csapat2.strategy.game.model.unit.Unit;

import java.awt.Color;
import java.awt.Graphics;
import java.util.concurrent.atomic.AtomicReference;

public record AttackAction(
        AtomicReference<GameState> gameState,
        Unit attacker,
        Destructible attacked) implements Action {

    private static final Color ATTACK_COLOR = new Color(1.0f, 0.0f, 0.0f, 0.5f);

    @Override
    public ActionType getType() {
        return ActionType.ATTACK;
    }

    @Override
    public void render(Graphics graphics) {
        var coordinate = attacked.getCoordinate();
        var x = coordinate.x() * Constants.TILE_SIZE;
        var y = coordinate.y() * Constants.TILE_SIZE;
        graphics.drawImage(Assets.SWORDS.getImage(), x, y, Constants.TILE_SIZE, Constants.TILE_SIZE, ATTACK_COLOR, null);
    }

    @Override
    public void execute() {
        var attackPower = attacker.getType().getAttack();
        attacked.setHealth(Math.max(0, attacked.getHealth() - attackPower));
        if (attacked.getHealth() == 0) {
            attacked.destruct(gameState.get());
        }
        attacker.setAttackedThisTurn(true);

        // If we are attacking a unit we need to check if the unit can attack back
        if (!(attacked instanceof Unit)) {
            return;
        }
        var attackedUnit = (Unit) attacked;
        if (attackedUnit.isAttackedThisTurn()) {
            return;
        }
        var attackedAttackPower = attackedUnit.getType().getAttack();
        attacker.setHealth(Math.max(0, attacker.getHealth() - attackedAttackPower));
        if (attacker.getHealth() == 0) {
            attacker.destruct(gameState.get());
        }
        attackedUnit.setAttackedThisTurn(true);
    }

}
