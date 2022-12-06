package szofttech.csapat2.strategy.game.util;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class ImageUtil {

	private static final Color TRANSPARENT_YELLOW = new Color(Color.yellow.getRGB() & 0x80ffffff, true);
	
	public static Image createHighlightedImage(Image original) {
		var width = original.getWidth(null);
		var height = original.getHeight(null);
		var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		var graphics = image.getGraphics();
		graphics.setColor(TRANSPARENT_YELLOW);
		graphics.fillRect(0, 0, width, height);
		graphics.drawImage(original, 0, 0, width, height, null);
		return image;
	}
	
	private ImageUtil() {}
	
}
