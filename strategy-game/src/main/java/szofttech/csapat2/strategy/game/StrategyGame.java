package szofttech.csapat2.strategy.game;

import java.io.FileNotFoundException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import szofttech.csapat2.strategy.game.view.MainWindow;

public class StrategyGame {
	
    public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			try {
				createAndDisplayMainWindow();
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(null, "Hiányzó mapInit.txt", "Hiba!", JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}
		});
    }

	private static void createAndDisplayMainWindow() throws FileNotFoundException {
		var window = new MainWindow();
		window.setVisible(true);
		window.pack();
		window.setLocationRelativeTo(null);
		window.setResizable(false);
	}

}
