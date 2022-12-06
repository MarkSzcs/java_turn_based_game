package szofttech.csapat2.strategy.game.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.JButton;
import javax.swing.JPanel;
import szofttech.csapat2.strategy.game.Renderer;
import szofttech.csapat2.strategy.game.input.InputListener;
import szofttech.csapat2.strategy.game.model.GameState;
import szofttech.csapat2.strategy.game.service.EndTurnService;
import szofttech.csapat2.strategy.game.service.PathfindingService;

public class GameView extends JPanel {

	private final InputListener inputListener;
	private final GamePanel gamePanel;
	private final ResourcesPanel resourcesPanel;
	private final JButton endTurnButton;
	private final EndTurnService endTurnService;

	public GameView(AtomicReference<GameState> gameState, Renderer renderer) {
		this.inputListener = new InputListener(
			gameState,
			new PathfindingService(gameState),
			this::repaintGamePanel,
			this::repaintResources);
		this.gamePanel = new GamePanel(renderer, inputListener);
		this.resourcesPanel = new ResourcesPanel(renderer);
		this.endTurnButton = new JButton("End Turn");
		this.endTurnButton.setPreferredSize(new Dimension(150, this.endTurnButton.getPreferredSize().height));
		this.endTurnService = new EndTurnService(gameState);
		this.endTurnButton.addActionListener(e -> endTurn());
		
		setLayout(new GridBagLayout());
		var constraints = new GridBagConstraints();
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		add(this.gamePanel, constraints);
		
		constraints.gridwidth = 1;
		constraints.gridy++;
		add(this.resourcesPanel, constraints);
		constraints.gridx++;
		add(this.endTurnButton, constraints);
	}

	public InputListener getInputListener() {
		return inputListener;
	}

	private void repaintGamePanel() {
		gamePanel.repaint();
	}
	
	private void repaintResources() {
		resourcesPanel.repaint();
	}

	private void endTurn() {
		endTurnService.endTurn();
		repaintGamePanel();
		repaintResources();
	}
		
}
