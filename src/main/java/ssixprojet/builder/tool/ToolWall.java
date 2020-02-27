package ssixprojet.builder.tool;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;

import ssixprojet.builder.BuilderConfig;
import ssixprojet.builder.ContextMenu;
import ssixprojet.builder.MapPanel;
import ssixprojet.common.MapEdge;
import ssixprojet.common.MapEdge.Orientation;

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

	private void clickOnButton(MapEdge e) {
		config.showContextMenu(new ContextMenu(200)
				.withText(new ImageIcon(BuilderConfig.LOCATION),
						"X: " + e.getX() + ", Y: " + e.getY() + ", Len: " + e.getLength())
				.withAction(new ImageIcon(BuilderConfig.ORIENTATION),
						"Orientation: " + e.getOrientation().name(), () -> {
							e.setOrientation(e.getOrientation().next());
							config.setNeedToBeSaved(true);
						})
				.withAction(new ImageIcon(BuilderConfig.DELETE), "Delete", () -> {
					config.getMap().getEdges().remove(e);
					config.setNeedToBeSaved(true);
				}));
	}

	@Override
	public boolean onClick(int mouseX, int mouseY, int mouseButton) {
		if (mouseButton != MouseEvent.BUTTON3)
			return false;
		for (MapEdge e : config.getMap().getEdges()) {
			if (e.getOrientation() == Orientation.BOTTOM) {
				int left = mouseX - 10;
				int right = mouseX + 10;
				if (
				// x between the line
				e.getX() > left && e.getX() < right &&
				// inside the line
						e.getY() < mouseY && e.getY() + e.getLength() > mouseY) {
					clickOnButton(e);
					return false;
				}
			} else { // RIGHT
				int top = mouseY + 10;
				int bottom = mouseY - 10;
				if (
				// y between the line
				e.getY() > bottom && e.getY() < top &&
				// inside the line
						e.getX() < mouseX && e.getX() + e.getLength() > mouseX) {
					clickOnButton(e);
					return false;
				}
			}
		}

		return false;
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
			g.setColor(WALL_COLOR);
			g.fillRect((int) (x * factorX), (int) (y * factorY), (int) (w * factorX), (int) (h * factorY));
		}
		super.onDraw(g, mouseX, mouseY, factorX, factorY);
	}

	@Override
	public boolean onStopDrag(int mouseX, int mouseY, int newMouseX, int newMouseY, int mouseButton) {
		if (!dragging)
			return false;
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
