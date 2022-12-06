package szofttech.csapat2.strategy.game.model;

import java.awt.Image;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Texture {

	public static Texture loadFromClassPath(String fileName) {
		try {
			var image = ImageIO.read(Texture.class.getResourceAsStream(fileName));
			return new Texture(image);
		} catch (IOException e) {
			throw new RuntimeException(String.format("Failed to load '%s': %s", fileName, e.getMessage()), e);
		}
	}
	
    private final Image image;

	// TODO: Remove this constructor once we have every asset
	public Texture() {
		this.image = null;
	}
	
	public Texture(Image image) {
		this.image = image;
	}
	
    public Image getImage() {
		return image;
	}
    
}
