package szofttech.csapat2.strategy.game.view;

import szofttech.csapat2.strategy.game.Renderer;
import szofttech.csapat2.strategy.game.input.InputListener;
import szofttech.csapat2.strategy.game.model.Constants;
import szofttech.csapat2.strategy.game.model.Coordinate;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class GamePanel extends JPanel implements MouseListener {

	private static final int PANEL_WIDTH = 1000;
	private static final int PANEL_HEIGHT = 1000;
	
	private final Renderer renderer;
	private final InputListener inputListener;
	
	public GamePanel(Renderer renderer, InputListener inputListener) {
		this.renderer = renderer;
		this.inputListener = inputListener;
		setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
		addMouseListener(this);
	}

	@Override
	protected void paintComponent(Graphics graphics) {
		renderer.renderGamePanel(graphics, getWidth(), getHeight());
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {
		var coordinate = new Coordinate(e.getX() / Constants.TILE_SIZE, e.getY() / Constants.TILE_SIZE);
		inputListener.handleClick(e, coordinate);
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

}
