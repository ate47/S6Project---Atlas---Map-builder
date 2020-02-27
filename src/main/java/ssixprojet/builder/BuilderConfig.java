package ssixprojet.builder;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.imageio.ImageIO;

import lombok.Data;
import ssixprojet.builder.tool.Tool;
import ssixprojet.builder.tool.ToolSave;
import ssixprojet.builder.tool.ToolSpawn;
import ssixprojet.builder.tool.ToolWall;
import ssixprojet.common.GameMap;

@Data
public class BuilderConfig {
	public static final Image ICO;
	public static final Image DELETE;
	public static final Image LOCATION;
	public static final Image ORIENTATION;

	static {
		Image ico, delete, orientation, location;

		try {
			ico = ImageIO.read(BuilderConfig.class.getResourceAsStream("/ico.png"));
			delete = ImageIO.read(BuilderConfig.class.getResourceAsStream("/context_delete.png"));
			orientation = ImageIO
					.read(BuilderConfig.class.getResourceAsStream("/context_wall_orientation.png"));
			location = ImageIO.read(BuilderConfig.class.getResourceAsStream("/context_wall_location.png"));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		ICO = ico;
		DELETE = delete;
		ORIENTATION = orientation;
		LOCATION = location;
	}
	private final List<Tool> TOOLS = new ArrayList<>();
	private Tool selectedTool;
	private boolean needToBeSaved = false;
	private File mapFile;
	private GameMap map;
	private BufferedImage background;
	private BuilderFrame frame;

	public BuilderConfig(GameMap map, BufferedImage background, File mapFile) {
		this.map = map;
		this.background = background;
		this.mapFile = mapFile;

		registerTools(new ToolSave(this), new ToolWall(this), new ToolSpawn(this));

		this.frame = new BuilderFrame(this);

	}

	public void disableAllTools() {
		TOOLS.forEach(t -> t.toggle(false));
	}

	public Tool getSelectedTool() {
		return selectedTool;
	}

	public List<Tool> getTools() {
		return TOOLS;
	}

	public void registerTools(Tool... tools) {
		for (Tool tool : tools) {
			TOOLS.add(tool);
		}
	}

	public void saveMap() {
		map.saveMap(mapFile);
	}

	public void selectTool(Tool tool) {
		selectedTool = tool;
	}

	public void sendToSelectedTool(Consumer<Tool> action) {
		if (selectedTool != null)
			action.accept(selectedTool);
	}

	public void showContextMenu(ContextMenu menu) {
		frame.showContextMenu(menu);
	}
}
