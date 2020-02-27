package ssixprojet.builder.tool;

import ssixprojet.builder.BuilderConfig;

public class ToolSave extends Tool {

	public ToolSave(BuilderConfig config) {
		super("Save", "/tool_save.png", config);
	}

	@Override
	public boolean isToggleTool() {
		return false;
	}

	@Override
	protected void onEnabled() {
		config.saveMap();
		super.onEnabled();
	}
}
