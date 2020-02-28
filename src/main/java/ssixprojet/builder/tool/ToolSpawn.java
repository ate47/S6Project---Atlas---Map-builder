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
	private static final int MOVE_NONE = 0;
	private static final int MOVE_X_LEFT = 1;
	private static final int MOVE_Y_TOP = 2;
	private static final int MOVE_Y_BOTTOM = 4;
	private static final int MOVE_X_RIGHT = 8;
	private static final int MOVE_ALL = MOVE_X_LEFT | MOVE_X_RIGHT | MOVE_Y_BOTTOM | MOVE_Y_TOP;

	public static final Color SPAWN_COLOR = new Color(MapPanel.SPAWN_COLOR.getRGB(), false);
	public static final Color SPAWN_INSIDE_COLOR = new Color(MapPanel.SPAWN_INSIDE_COLOR.getRGB(), false);

	private static boolean isIn(int x, int y, int inX, int inY, int radius) {
		int dx = (x - inX);
		int dy = (y - inY);
		return dx * dx + dy * dy <= radius * radius;
	}

	private boolean dragging = false;
	private int x, y, w, h;
	private SpawnLocation edit;
	private int modifier = MOVE_NONE;

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
	public boolean onClick(int mouseX, int mouseY, int mouseButton) {
		if (edit != null) {
			edit = null;
			return true;
		}
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
						.withAction(new ImageIcon(BuilderConfig.EDIT), "Edit Bounds", () -> {
							edit = l;
						}).withAction(new ImageIcon(BuilderConfig.DELETE), "Delete", () -> {
							config.getMap().getSpawnLocations().remove(l);
							config.needToBeSaved();
						})

				);
				return false;
			}
		return false;
	}

	@Override
	protected void onDisabled() {
		edit = null;
		super.onDisabled();
	}

	@Override
	public boolean onDrag(int mouseX, int mouseY, int newMouseX, int newMouseY, int mouseButton) {
		if (mouseButton != MouseEvent.BUTTON1)
			return false;
		if (edit == null) {
			dragging = true;
			buildBox(mouseX, mouseY, newMouseX, newMouseY);
		} else {
			if (!dragging) {
				dragging = true;
				int top = edit.getY();
				int left = edit.getX();
				int bottom = edit.getY() + edit.getHeight();
				int right = edit.getX() + edit.getWidth();

				int center = (left + right) / 2;
				int middle = (top + bottom) / 2;

				if (isIn(mouseX, mouseY, left, top, 5)) {
					modifier = MOVE_X_LEFT | MOVE_Y_TOP;
				} else if (isIn(mouseX, mouseY, left, bottom, 5)) {
					modifier = MOVE_X_LEFT | MOVE_Y_BOTTOM;
				} else if (isIn(mouseX, mouseY, right, top, 5)) {
					modifier = MOVE_X_RIGHT | MOVE_Y_TOP;
				} else if (isIn(mouseX, mouseY, right, bottom, 5)) {
					modifier = MOVE_X_RIGHT | MOVE_Y_BOTTOM;
				} else if (isIn(mouseX, mouseY, center, top, 5)) {
					modifier = MOVE_Y_TOP;
				} else if (isIn(mouseX, mouseY, left, middle, 5)) {
					modifier = MOVE_X_LEFT;
				} else if (isIn(mouseX, mouseY, right, middle, 5)) {
					modifier = MOVE_X_RIGHT;
				} else if (isIn(mouseX, mouseY, center, bottom, 5)) {
					modifier = MOVE_Y_BOTTOM;
				} else if (edit.isIn(mouseX, mouseY)) {
					modifier = MOVE_ALL;
				} else
					modifier = MOVE_NONE;
				x = mouseX;
				y = mouseY;
			} else {
				int dx = newMouseX - x;
				int dy = newMouseY - y;
				x = newMouseX;
				y = newMouseY;
				
				// top and left in first to avoid reducing with MOVE_ALL

				if ((modifier & MOVE_X_LEFT) != 0) {
					if ((modifier & MOVE_X_RIGHT) == 0 && dx > edit.getWidth()) {
						edit.setWidth(dx - edit.getWidth());
						edit.setX(edit.getX() + edit.getWidth());
						modifier = (modifier & ~MOVE_X_LEFT) | MOVE_X_RIGHT; // remove the left modifier and add the
																				// right modifier
					} else {
						edit.setX(edit.getX() + dx);
						edit.setWidth(edit.getWidth() - dx);
					}
				}

				if ((modifier & MOVE_Y_TOP) != 0) {
					if ((modifier & MOVE_Y_BOTTOM) == 0 && dy > edit.getHeight()) {
						edit.setHeight(dy - edit.getHeight());
						edit.setY(edit.getY() + edit.getHeight());
						modifier = (modifier & ~MOVE_Y_TOP) | MOVE_Y_BOTTOM; // remove the top modifier and add the
																				// bottom modifier
					} else {
						edit.setY(edit.getY() + dy);
						edit.setHeight(edit.getHeight() - dy);
					}
				}

				if ((modifier & MOVE_X_RIGHT) != 0) {
					int w = edit.getWidth() + dx;
					if (w < 0) {
						edit.setWidth(-w);
						edit.setX(edit.getX() + w);
						modifier = (modifier & ~MOVE_X_RIGHT) | MOVE_X_LEFT; // remove the right modifier and add the
																				// left modifier
					} else {
						edit.setWidth(w);
					}
				}

				if ((modifier & MOVE_Y_BOTTOM) != 0) {
					int h = edit.getHeight() + dy;
					if (h < 0) {
						edit.setHeight(-h);
						edit.setY(edit.getY() + h);
						modifier = (modifier & ~MOVE_Y_BOTTOM) | MOVE_Y_TOP; // remove the bottom modifier and add the
																				// top modifier
					} else {
						edit.setHeight(h);
					}
				}
			}
		}
		return true;
	}

	@Override
	public void onDraw(Graphics g, int mouseX, int mouseY, double factorX, double factorY) {
		if (edit != null) {
			int top = (int) (factorY * edit.getY());
			int left = (int) (factorX * edit.getX());
			int bottom = (int) (factorY * (edit.getY() + edit.getHeight()));
			int right = (int) (factorX * (edit.getX() + edit.getWidth()));

			int center = (left + right) / 2;
			int middle = (top + bottom) / 2;
			if (edit.isOutside())
				g.setColor(SPAWN_COLOR);
			else
				g.setColor(SPAWN_INSIDE_COLOR);

			g.fillOval(left - 5, top - 5, 10, 10);
			g.fillOval(left - 5, bottom - 5, 10, 10);
			g.fillOval(right - 5, top - 5, 10, 10);
			g.fillOval(right - 5, bottom - 5, 10, 10);

			g.fillOval(center - 5, top - 5, 10, 10);
			g.fillOval(left - 5, middle - 5, 10, 10);
			g.fillOval(right - 5, middle - 5, 10, 10);
			g.fillOval(center - 5, bottom - 5, 10, 10);

			g.drawLine(left, top, right, bottom);
			g.drawLine(right, top, left, bottom);

			g.drawLine(center, top, center, bottom);
			g.drawLine(right, middle, left, middle);
		} else if (dragging) {
			g.setColor(SPAWN_COLOR);
			g.fillRect((int) (x * factorX), (int) (y * factorY), (int) (w * factorX), (int) (h * factorY));
		}
	}

	@Override
	public boolean onStopDrag(int mouseX, int mouseY, int newMouseX, int newMouseY, int mouseButton) {
		if (!dragging)
			return false;
		dragging = false;
		if (edit != null) {
			if (modifier != 0)
				config.needToBeSaved();
			return true;
		}
		if (w > 5 && h > 5) {
			buildBox(mouseX, mouseY, newMouseX, newMouseY);
			config.getMap().getSpawnLocations().add(0, new SpawnLocation(true, x, y, w, h));
			config.needToBeSaved();
		}
		return true;
	}
}
