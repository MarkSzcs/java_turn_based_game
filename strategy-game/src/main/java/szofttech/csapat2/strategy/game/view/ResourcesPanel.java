package szofttech.csapat2.strategy.game.view;

import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;
import szofttech.csapat2.strategy.game.Renderer;

public class ResourcesPanel extends JPanel {

	private static final int PANEL_WIDTH = 850;
	private static final int PANEL_HEIGHT = 50;
	
	private final Renderer renderer;

	public ResourcesPanel(Renderer renderer) {
		this.renderer = renderer;
		setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
	}
	
	@Override
	protected void paintComponent(Graphics graphics) {
		renderer.renderResourcesPanel(graphics, getWidth(), getHeight());
	}
	
}
