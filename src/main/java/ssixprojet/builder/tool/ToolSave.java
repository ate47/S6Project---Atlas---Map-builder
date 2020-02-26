package ssixprojet.builder.tool;

import ssixprojet.builder.BuilderConfig;

public class ToolSave extends Tool {
	private BuilderConfig cfg;

	public ToolSave(BuilderConfig cfg) {
		super("Save", "/tool_save.png");
		this.cfg = cfg;
	}

	@Override
	public boolean isToggleTool() {
		return false;
	}

	@Override
	protected void onEnabled() {
		cfg.saveMap();
		super.onEnabled();
	}
}
