package ssixprojet.builder;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import ssixprojet.builder.tool.Tool;
import ssixprojet.builder.tool.ToolDeleteWall;
import ssixprojet.builder.tool.ToolSave;
import ssixprojet.builder.tool.ToolSpawn;
import ssixprojet.builder.tool.ToolWall;
import ssixprojet.builder.tool.ToolWallBox;
import ssixprojet.common.GameMap;
import ssixprojet.utils.ImageLoader;

@Data
public class BuilderConfig {
	public static final Image ICO = ImageLoader.loadImage("/ico.png");
	public static final Image DELETE = ImageLoader.loadImage("/context_delete.png");
	public static final Image EDIT = ImageLoader.loadImage("/context_edit.png");
	public static final Image LOCATION = ImageLoader.loadImage("/context_wall_orientation.png");
	public static final Image ORIENTATION = ImageLoader.loadImage("/context_wall_location.png");
	public static final Image SPAWN = ImageLoader.loadImage("/context_spawn_inside.png");

	private Tool saveTool;
	private final List<Tool> TOOLS = new ArrayList<>();
	private Tool selectedTool;
	@Setter(value = AccessLevel.NONE)
	private boolean needToBeSaved = false;
	private File mapFile;
	private GameMap map;
	private BufferedImage background;
	private BuilderFrame frame;

	public BuilderConfig(GameMap map, BufferedImage background, File mapFile) {
		this.map = map;
		this.background = background;
		this.mapFile = mapFile;

		registerTools(saveTool = new ToolSave(this), new ToolWall(this), new ToolWallBox(this),
				new ToolDeleteWall(this), new ToolSpawn(this));

		saveTool.getButton().setEnabled(false);
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

	public void needToBeSaved() {
		needToBeSaved = true;
		saveTool.getButton().setEnabled(true);
	}

	public void registerTools(Tool... tools) {
		for (Tool tool : tools) {
			TOOLS.add(tool);
		}
	}

	public void saveMap() {
		map.saveMap(mapFile);
		needToBeSaved = false;
		saveTool.getButton().setEnabled(false);
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
