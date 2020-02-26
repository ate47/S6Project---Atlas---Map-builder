package ssixprojet.builder;

import java.awt.Image;

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
	private GameMap map;
	private Image background;
	private BuilderFrame frame;
	public BuilderConfig(GameMap map, Image background) {
		this.map = map;
		this.background = background;
		this.frame = new BuilderFrame(this);
	}
	
	
}
