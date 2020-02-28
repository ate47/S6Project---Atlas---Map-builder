package ssixprojet.utils;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageLoader {

	/**
	 * load an image or throw a {@link RuntimeException}
	 * 
	 * @param img
	 *            the source to load
	 * @return the image
	 */
	public static BufferedImage loadImage(String img) {
		try {
			return ImageIO.read(ImageLoader.class.getResourceAsStream(img));
		} catch (IOException | NullPointerException e) {
			throw new RuntimeException(e);
		}
	}
}
