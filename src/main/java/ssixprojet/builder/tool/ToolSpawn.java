package ssixprojet.builder.tool;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;

import ssixprojet.builder.BuilderConfig;
import ssixprojet.builder.ContextMenu;
import ssixprojet.builder.MapPanel;
import ssixprojet.common.SpawnLocation;

public class ToolSpawn extends Tool {
	public static final Color SPAWN_COLOR = new Color(MapPanel.SPAWN_COLOR.getRGB(), false);
	private boolean dragging = false;
	private int x, y, w, h;

	public ToolSpawn(BuilderConfig config) {
		super("Spawn builder", "/tool_spawn.png", config);
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

	@Override
	public void onDraw(Graphics g, int mouseX, int mouseY, double factorX, double factorY) {
		if (dragging) {
			g.setColor(SPAWN_COLOR);
			g.fillRect((int) (x * factorX), (int) (y * factorY), (int) (w * factorX), (int) (h * factorY));
		}
	}

	@Override
	public boolean onClick(int mouseX, int mouseY, int mouseButton) {
		if (mouseButton != MouseEvent.BUTTON3)
			return false;
		for (SpawnLocation l : config.getMap().getSpawnLocations())
			if (l.isIn(mouseX, mouseY)) {
				config.showContextMenu(new ContextMenu(200)

						.withText(new ImageIcon(BuilderConfig.LOCATION), "X: " + l.getX() + ", Y: " + l.getY())
						.withText(new ImageIcon(BuilderConfig.LOCATION), "W: " + l.getWidth() + ", H: " + l.getHeight())
						.withAction(new ImageIcon(BuilderConfig.SPAWN), "Outside: " + (l.isOutside() ? "Yes" : "No"),
								() -> {
									l.setOutside(!l.isOutside());
									config.needToBeSaved();
								})
						.withAction(new ImageIcon(BuilderConfig.DELETE), "Delete", () -> {
							config.getMap().getSpawnLocations().remove(l);
							config.needToBeSaved();
						})

				);
				return false;
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
	public boolean onStopDrag(int mouseX, int mouseY, int newMouseX, int newMouseY, int mouseButton) {
		if (!dragging)
			return false;
		dragging = false;
		if (w > 5 && h > 5) {
			buildBox(mouseX, mouseY, newMouseX, newMouseY);
			config.getMap().getSpawnLocations().add(new SpawnLocation(true, x, y, w, h));
			config.needToBeSaved();
		}
		return true;
	}
}
