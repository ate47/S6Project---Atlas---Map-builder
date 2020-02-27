package ssixprojet.builder;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import lombok.Data;
import ssixprojet.common.GameMap;

@Data
public class BuilderConfig {
	public static final Image ICO;

	static {
		Image ico;

		try {
			ico = ImageIO.read(BuilderConfig.class.getResourceAsStream("/ico.png"));
		} catch (Exception e) {
			ico = null;
		}
		ICO = ico;
	}
	private boolean needToBeSaved = false;
	private File mapFile;
	private GameMap map;
	private BufferedImage background;
	private BuilderFrame frame;

	public BuilderConfig(GameMap map, BufferedImage background, File mapFile) {
		this.map = map;
		this.background = background;
		this.mapFile = mapFile;
		this.frame = new BuilderFrame(this);
	}

	public void saveMap() {
		map.saveMap(mapFile);
	}
}
