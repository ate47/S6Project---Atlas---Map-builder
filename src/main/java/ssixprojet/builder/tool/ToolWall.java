package ssixprojet.builder.tool;

import java.awt.Color;
import java.awt.Graphics;

import ssixprojet.builder.BuilderConfig;
import ssixprojet.builder.MapPanel;
import ssixprojet.common.MapEdge;

public class ToolWall extends Tool {

	public static final Color WALL_COLOR = new Color(MapPanel.WALL_COLOR.getRGB(), false);
	private boolean dragging = false;
	private int x, y, w, h;

	public ToolWall(BuilderConfig config) {
		super("Wall builder", "/tool_wall.png", config);
	}

	private void buildBox(int mouseX, int mouseY, int newMouseX, int newMouseY) {
		int x, y, w, h;
		// get the box boundaries
		if (mouseX < newMouseX) {
			x = mouseX;
			w = newMouseX - mouseX;
		} else {
			x = newMouseX;
			w = mouseX - newMouseX;
		}

		if (mouseY < newMouseY) {
			y = mouseY;
			h = newMouseY - mouseY;
		} else {
			y = newMouseY;
			h = mouseY - newMouseY;
		}

		// get line orientation

		if (w < h) { // vertical
			this.x = mouseX;
			this.y = y;
			this.w = 2;
			this.h = h;
		} else { // horizontal
			this.x = x;
			this.y = mouseY;
			this.w = w;
			this.h = 2;
		}
	}

	@Override
	public boolean onClick(int mouseX, int mouseY, int mouseButton) {

		return false;
	}

	@Override
	public boolean onDrag(int mouseX, int mouseY, int newMouseX, int newMouseY, int mouseButton) {
		dragging = true;
		buildBox(mouseX, mouseY, newMouseX, newMouseY);
		return true;
	}

	@Override
	public void onDraw(Graphics g, int mouseX, int mouseY, double factorX, double factorY) {
		if (dragging) {
			g.setColor(WALL_COLOR);
			g.fillRect((int) (x * factorX), (int) (y * factorY), (int) (w * factorX), (int) (h * factorY));
		}
		super.onDraw(g, mouseX, mouseY, factorX, factorY);
	}

	@Override
	public boolean onStopDrag(int mouseX, int mouseY, int newMouseX, int newMouseY, int mouseButton) {
		dragging = false;
		buildBox(mouseX, mouseY, newMouseX, newMouseY);
		if (w > 2)
			config.getMap().getEdges().add(new MapEdge(x, y, w, MapEdge.Orientation.RIGHT));
		else if (h > 2)
			config.getMap().getEdges().add(new MapEdge(x, y, h, MapEdge.Orientation.BOTTOM));
		else
			return true;
		config.setNeedToBeSaved(true);
		return true;
	}
}
