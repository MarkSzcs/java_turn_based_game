package szofttech.csapat2.strategy.game.view;

import szofttech.csapat2.strategy.game.model.GameState;
import szofttech.csapat2.strategy.game.model.Player;
import szofttech.csapat2.strategy.game.model.Resources;
import szofttech.csapat2.strategy.game.model.building.Building;
import szofttech.csapat2.strategy.game.model.building.BuildingType;
import szofttech.csapat2.strategy.game.model.unit.UnitType;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.DimensionUIResource;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicReference;

public class HqMenu extends JDialog {

    private static final String TITLE = "HQ production";
	private final Building building;
	private final Player player;
	private final Runnable repaintResources;

    public HqMenu(
			Building building,
			AtomicReference<GameState> gameStateReference,
			Runnable repaintResources) {
		this.building = building;
		this.player = gameStateReference.get().getPlayerByColor(building.getColor());
		this.repaintResources = repaintResources;
		
		setLayout(new GridBagLayout());
		setTitle(TITLE);

		JPanel healthPane = new JPanel();
		JPanel prodPane = new JPanel();
		JPanel queuePane = new JPanel();
		JPanel dequeuePane = new JPanel();
		renderHealthPane(healthPane);
		renderProdPane(prodPane, queuePane);
		renderQueuePane(queuePane);
		renderDequePane(dequeuePane, queuePane, prodPane);

		add(healthPane);
		add(prodPane);
		add(queuePane);
		add(dequeuePane);

		setLocationRelativeTo(null);
		displayHqMenu();
    }

	private void renderHealthPane(JPanel healthPane){
		healthPane.removeAll();

		JLabel hpLabel = new JLabel("HP:");
		BuildingType buildingType = building.getType();
		JLabel healthInfoLabel = new JLabel(buildingType.getMaxHealth() + "/" + building.getHealth());
		healthPane.add(hpLabel);
		healthPane.add(healthInfoLabel);
	}

	private void renderProdPane(JPanel prodPane, JPanel queuePane){
		prodPane.removeAll();

		JLabel prodLabel = new JLabel("Create:");
		prodPane.add(prodLabel);
		JButton minerBTN = new JButton("M");
		JButton farmerBTN = new JButton("F");
		JButton lumbererBTN = new JButton("L");

		minerBTN.setPreferredSize(new DimensionUIResource(50, 50));
		minerBTN.addActionListener(e -> {
			building.addToProdQueue(UnitType.MINER);
			player.getResources().deduct(UnitType.MINER.getCost());
			renderQueuePane(queuePane);
			renderProdPane(prodPane, queuePane);
			repaintResources.run();
		});
		if (!player.getResources().canAfford(UnitType.MINER.getCost()) || building.getProductionQueue().size() >= 5) {
			ActionListener[] listeners = minerBTN.getActionListeners();
			for (ActionListener listener : listeners) {
				minerBTN.removeActionListener(listener);
			}
		}
		prodPane.add(minerBTN);
		
		farmerBTN.setPreferredSize(new DimensionUIResource(50, 50));
		farmerBTN.addActionListener(e -> {
			building.addToProdQueue(UnitType.FARMER);
			player.getResources().deduct(UnitType.FARMER.getCost());
			renderQueuePane(queuePane);
			renderProdPane(prodPane, queuePane);
			repaintResources.run();
		});
		if (!player.getResources().canAfford(UnitType.FARMER.getCost()) || building.getProductionQueue().size() >= 5) {
			ActionListener[] listeners = farmerBTN.getActionListeners();
			for (ActionListener listener : listeners) {
				farmerBTN.removeActionListener(listener);
			}
		}
		prodPane.add(farmerBTN);
		
		lumbererBTN.setPreferredSize(new DimensionUIResource(50, 50));
		lumbererBTN.addActionListener(e -> {
			building.addToProdQueue(UnitType.LUMBERER);
			player.getResources().deduct(UnitType.LUMBERER.getCost());
			renderQueuePane(queuePane);
			renderProdPane(prodPane, queuePane);
			repaintResources.run();
		});
		if (!player.getResources().canAfford(UnitType.LUMBERER.getCost()) || building.getProductionQueue().size() >= 5) {
			ActionListener[] listeners = lumbererBTN.getActionListeners();
			for (ActionListener listener : listeners) {
				lumbererBTN.removeActionListener(listener);
			}
		}
		prodPane.add(lumbererBTN);
		displayHqMenu();
	}

	public void renderQueuePane(JPanel queuePane){
		queuePane.removeAll();
		queuePane.setPreferredSize(new Dimension(75, 150));
		JLabel queueLabel = new JLabel("In queue:");
		queueLabel.setLabelFor(queuePane);
		queuePane.add(queueLabel);
		for (int i = 0; i < building.getProductionQueue().size(); i++) {
			JLabel unitLabel = new JLabel(building.getIndexFromProdQueue(i).name());
			queuePane.add(unitLabel);
		}
	}

	private void renderDequePane(JPanel dequeuePane, JPanel queuePane, JPanel prodPane) {
		dequeuePane.removeAll();

		JButton deleteQueueBTN = new JButton("Dequeue all");
		deleteQueueBTN.setPreferredSize(new DimensionUIResource(100, 50));
		deleteQueueBTN.addActionListener(e -> {
			Resources costToReFound = building.getResourcesRequired();
			building.dequeueProdQueue();
			player.getResources().update(
				costToReFound.getActionPoints(),
				costToReFound.getGold(),
				costToReFound.getWood(),
				costToReFound.getFood()
			);
			renderProdPane(prodPane, queuePane);
			renderQueuePane(queuePane);
			repaint();
			displayHqMenu();
			repaintResources.run();
		});
		dequeuePane.add(deleteQueueBTN);
	}

	private void displayHqMenu() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
		setVisible(true);
    }

}
