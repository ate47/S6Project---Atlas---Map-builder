package ssixprojet.builder.tool;

import java.awt.Graphics;
import java.awt.event.MouseEvent;

import ssixprojet.builder.BuilderConfig;
import ssixprojet.common.MapEdge;
import ssixprojet.common.MapEdge.Orientation;

public class ToolWallBox extends Tool {

	private int x, y, w, h;

	private boolean dragging = false;

	public ToolWallBox(BuilderConfig config) {
		super("Wall box builder", "/tool_multiwall.png", config);
	}

	@Override
	public boolean onDrag(int mouseX, int mouseY, int newMouseX, int newMouseY, int mouseButton) {
		if (mouseButton != MouseEvent.BUTTON1)
			return false;

		dragging = true;
		buildBox(mouseX, mouseY, newMouseX, newMouseY);

		return true;
	}

	@Override
	public void onDraw(Graphics g, int mouseX, int mouseY, double factorX, double factorY) {
		if (dragging) {
			g.setColor(ToolWall.WALL_COLOR);
			g.drawRect((int) (x * factorX), (int) (y * factorY), (int) (w * factorX), (int) (h * factorY));
		}
	}

	@Override
	public boolean onStopDrag(int mouseX, int mouseY, int newMouseX, int newMouseY, int mouseButton) {
		if (!dragging)
			return false;

		dragging = false;
		buildBox(mouseX, mouseY, newMouseX, newMouseY);

		config.getMap().getEdges().add(new MapEdge(x, y, w, Orientation.RIGHT));
		config.getMap().getEdges().add(new MapEdge(x, y, h, Orientation.BOTTOM));
		config.getMap().getEdges().add(new MapEdge(x, y + h, w, Orientation.RIGHT));
		config.getMap().getEdges().add(new MapEdge(x + w, y, h, Orientation.BOTTOM));
		config.needToBeSaved();
		return true;

	}

	private void buildBox(int mouseX, int mouseY, int newMouseX, int newMouseY) {
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
	}
}
