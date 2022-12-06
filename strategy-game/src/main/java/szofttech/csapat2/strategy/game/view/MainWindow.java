package szofttech.csapat2.strategy.game.view;

import szofttech.csapat2.strategy.game.GameStateLoader;
import szofttech.csapat2.strategy.game.Renderer;
import szofttech.csapat2.strategy.game.model.GameState;

import javax.swing.JFrame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicReference;

public class MainWindow extends JFrame implements KeyEventDispatcher {

	private static final String TITLE = "Strategy Game - Csoport 2";
	private static final int MAP_WIDTH = 20;
	private static final int MAP_HEIGHT = 20;
	
	private final AtomicReference<GameState> currentGame;
	private final Renderer renderer;
	private final MainMenuView mainMenuView;
	private final GameView gameView;
	
    public MainWindow() {
		this.currentGame = new AtomicReference<>();
		this.renderer = new Renderer(currentGame);
		this.mainMenuView = new MainMenuView(currentGame, this::continueGame, 
			this::startNewGame,
			this::quit);

		this.gameView = new GameView(currentGame, renderer);
		setTitle(TITLE);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new GridBagLayout());
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);
		
		displayMainMenu();
    }
	
	private void displayMainMenu() {
		getContentPane().removeAll();
		
		var constraints = new GridBagConstraints();
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 0;
		constraints.gridy = 0;
		add(mainMenuView, constraints);
		mainMenuView.repaint();
		
		pack();
		setLocationRelativeTo(null);
	}
	
	private void startNewGame() {
		// Drop the previous game state and create a new one
		var newGame = GameStateLoader.loadGameState(MAP_WIDTH, MAP_HEIGHT);
		currentGame.set(newGame);
		continueGame();
	}
	
	private void continueGame() {
		getContentPane().removeAll();
		var constraints = new GridBagConstraints();
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 0;
		constraints.gridy = 0;
		add(gameView, constraints);
		
		pack();
		setLocationRelativeTo(null);
		gameView.repaint();
	}
	
	private void quit() {
		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		if (e.getID() != KeyEvent.KEY_RELEASED) {
			return false;
		}
		// Only catch the escape key, forward everything else to InputListener
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			displayMainMenu();
		}
		else if (currentGame.get() != null) {
			gameView.getInputListener().handleButtonPress(e.getKeyCode());
		}
		return true;
	}
	
}
