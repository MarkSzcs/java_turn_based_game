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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicReference;

public class BarracksMenu extends JDialog {

    private static final String TITLE = "Barracks production";
	private final Player player;
	private final Runnable repaintResources;

    public BarracksMenu(
			Building building,
			AtomicReference<GameState> gameStateReference,
			Runnable repaintResources){
		this.player = gameStateReference.get().getPlayerByColor(building.getColor());
		this.repaintResources = repaintResources;

		setLayout(new GridBagLayout());
		setTitle(TITLE);

		JPanel healthPane = new JPanel();
		JPanel prodPane = new JPanel();
		JPanel queuePane = new JPanel();
		JPanel dequeuePane = new JPanel();

		//health
		renderHealthPane(building, healthPane);

		//production
		renderProdPane(building, prodPane, queuePane);

		//queue
		renderQueuePane(building, queuePane);

		//dequeue
		renderDequePane(building, dequeuePane, queuePane, prodPane);

		add(healthPane);
		add(prodPane);
		add(queuePane);
		add(dequeuePane);

		setResizable(true);
		setLocationRelativeTo(null);

		displayBarracksMenu();
    }

	private void renderHealthPane(Building b, JPanel healthPane){
		healthPane.removeAll();

		JLabel hpLabel = new JLabel("HP:");
		BuildingType bType = b.getType();
		JLabel healthInfoLabel = new JLabel(bType.getMaxHealth() + "/" + b.getHealth());
		healthPane.add(hpLabel);
		healthPane.add(healthInfoLabel);
	}

	private void renderProdPane(Building building, JPanel prodPane, JPanel queuePane){
		prodPane.removeAll();

		JLabel prodLabel = new JLabel("Create:");
		prodPane.add(prodLabel);
		JButton peasantBTN = new JButton("P");
		JButton soldierBTN = new JButton("S");
		JButton knightBTN = new JButton("K");
		JButton dragonBTN = new JButton("D");

		peasantBTN.setPreferredSize(new DimensionUIResource(50, 50));
		peasantBTN.addActionListener(e -> {
			building.addToProdQueue(UnitType.PEASANT);
			player.getResources().deduct(UnitType.PEASANT.getCost());
			renderQueuePane(building, queuePane);
			renderProdPane(building, prodPane, queuePane);
			repaintResources.run();
		});

		if (!player.getResources().canAfford(UnitType.PEASANT.getCost()) || building.getProductionQueue().size() >= 5){
			ActionListener[] listeners = peasantBTN.getActionListeners();
			for (ActionListener listener : listeners) {
				peasantBTN.removeActionListener(listener);
			}
		}
		prodPane.add(peasantBTN);
		
		soldierBTN.setPreferredSize(new DimensionUIResource(50, 50));
		soldierBTN.addActionListener(e -> {
			building.addToProdQueue(UnitType.SOLDIER);
			player.getResources().deduct(UnitType.SOLDIER.getCost());
			renderQueuePane(building, queuePane);
			renderProdPane(building, prodPane, queuePane);
			repaintResources.run();
		});

		if (!player.getResources().canAfford(UnitType.SOLDIER.getCost()) || building.getProductionQueue().size() >= 5) {
			ActionListener[] listeners = soldierBTN.getActionListeners();
			for (ActionListener listener : listeners) {
				soldierBTN.removeActionListener(listener);
			}
		}
		prodPane.add(soldierBTN);
		
		knightBTN.setPreferredSize(new DimensionUIResource(50, 50));
		knightBTN.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				building.addToProdQueue(UnitType.KNIGHT);
				player.getResources().update(
					-(UnitType.KNIGHT.getCost().getActionPoints()),
					-(UnitType.KNIGHT.getCost().getGold()),
					-(UnitType.KNIGHT.getCost().getWood()),
					-(UnitType.KNIGHT.getCost().getFood())
				);
				renderQueuePane(building, queuePane);
				renderProdPane(building, prodPane, queuePane);
				//checkEnabled(b, resc, peasantBTN, soldierBTN, knightBTN);
				repaintResources.run();
			}
		});
		if(	player.getResources().getActionPoints() < UnitType.KNIGHT.getCost().getActionPoints() ||
			player.getResources().getFood() < UnitType.KNIGHT.getCost().getFood() ||
			player.getResources().getGold() < UnitType.KNIGHT.getCost().getGold() ||
			player.getResources().getWood() < UnitType.KNIGHT.getCost().getWood() ||
			building.getProductionQueue().size() >= 5 ){
			ActionListener[] listeners = knightBTN.getActionListeners();
			for (ActionListener listener : listeners) {
				knightBTN.removeActionListener(listener);
			}
		}
		prodPane.add(knightBTN);

		dragonBTN.setPreferredSize(new DimensionUIResource(50, 50));
		dragonBTN.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				building.addToProdQueue(UnitType.DRAGON);
				player.getResources().update(
					-(UnitType.DRAGON.getCost().getActionPoints()),
					-(UnitType.DRAGON.getCost().getGold()),
					-(UnitType.DRAGON.getCost().getWood()),
					-(UnitType.DRAGON.getCost().getFood())
				);
				renderQueuePane(building, queuePane);
				renderProdPane(building, prodPane, queuePane);
				//checkEnabled(b, resc, peasantBTN, soldierBTN, dragonBTN);
				repaintResources.run();
			}
		});
		if(	player.getResources().getActionPoints() < UnitType.DRAGON.getCost().getActionPoints() ||
			player.getResources().getFood() < UnitType.DRAGON.getCost().getFood() ||
			player.getResources().getGold() < UnitType.DRAGON.getCost().getGold() ||
			player.getResources().getWood() < UnitType.DRAGON.getCost().getWood() ||
			building.getProductionQueue().size() >= 5 ){
			ActionListener[] listeners = dragonBTN.getActionListeners();
			for (ActionListener listener : listeners) {
				dragonBTN.removeActionListener(listener);
			}
		}
		prodPane.add(dragonBTN);
		displayBarracksMenu();
	}
	
	public void renderQueuePane(Building b, JPanel queuePane){
		queuePane.removeAll();
		queuePane.setPreferredSize(new Dimension(75, 150));
		//interpret the queue
		JLabel queueLabel = new JLabel("In queue:");
		queueLabel.setLabelFor(queuePane);
		queuePane.add(queueLabel);
		for (int i = 0; i < b.getProductionQueue().size(); i++) {
			JLabel unitLabel = new JLabel(""+ b.getIndexFromProdQueue(i).name());
			queuePane.add(unitLabel);
		}
	}

	private void renderDequePane(Building b, JPanel dequeuePane, JPanel queuePane, JPanel prodPane) {
		dequeuePane.removeAll();

		JButton deleteQueueBTN = new JButton("Dequeue all");
		deleteQueueBTN.setPreferredSize(new DimensionUIResource(100, 50));
		deleteQueueBTN.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Resources costToReFound = b.getResourcesRequired();
				b.dequeueProdQueue();
				player.getResources().update(
					costToReFound.getActionPoints(),
					costToReFound.getGold(),
					costToReFound.getWood(),
					costToReFound.getFood()
				);
				renderProdPane(b, prodPane, queuePane);
				renderQueuePane(b, queuePane);
				repaint();
				displayBarracksMenu();
				repaintResources.run();
			}
		});
		dequeuePane.add(deleteQueueBTN);
	}

	private void displayBarracksMenu() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
		setVisible(true);
    }
	
}
