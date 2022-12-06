package szofttech.csapat2.strategy.game.view;

import szofttech.csapat2.strategy.game.Assets;
import szofttech.csapat2.strategy.game.model.GameState;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class MainMenuView extends JPanel {

	private static final int MENU_WIDTH = 400;
	private static final int MENU_HEIGHT = 225;

	private final JButton continueGameButton = new JButton("Continue Game");
	private final JButton newGameButton = new JButton("New Game");
	private final JButton quitButton = new JButton("Quit");
	private final AtomicReference<GameState> gameState;
	
	public MainMenuView(
			AtomicReference<GameState> gameState,
			Runnable continueGameFunction,
			Runnable newGameFunction,
			Runnable quitFunction) {
		this.gameState = gameState;
		setLayout(new GridBagLayout());
		var constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		add(continueGameButton, constraints);
		continueGameButton.addActionListener(e -> continueGameFunction.run());
		continueGameButton.setVisible(gameState.get() != null);
		
		++constraints.gridy;
		add(newGameButton, constraints);
		
		++constraints.gridy;
		add(quitButton, constraints);
		newGameButton.addActionListener(e -> newGameFunction.run());
		quitButton.addActionListener(e ->  quitFunction.run());
		setPreferredSize(new Dimension(MENU_WIDTH, MENU_HEIGHT));
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(Assets.BACKGROUND.getImage(), 0, 0, null);
	}

	@Override
	public void repaint() {
		if (Objects.isNull(this.gameState)) {
			return;
		}
		continueGameButton.setVisible(gameState.get() != null);
		super.repaint();
	}
	
}
