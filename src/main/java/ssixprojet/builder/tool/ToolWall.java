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
	private static final int MOVE_NONE = 0;
	private static final int MOVE_X_LEFT = 1;
	private static final int MOVE_Y_TOP = 2;
	private static final int MOVE_Y_BOTTOM = 4;
	private static final int MOVE_X_RIGHT = 8;
	private static final int MOVE_ALL = MOVE_X_LEFT | MOVE_X_RIGHT | MOVE_Y_BOTTOM | MOVE_Y_TOP;

	public static final Color WALL_COLOR = new Color(MapPanel.WALL_COLOR.getRGB(), false);

	private static boolean isIn(int x, int y, int inX, int inY, int radius) {
		int dx = (x - inX);
		int dy = (y - inY);
		return dx * dx + dy * dy <= radius * radius;
	}

	private boolean dragging = false;
	private int x, y, w, h;
	private MapEdge edit;
	private int modifier;

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
				.withAction(new ImageIcon(BuilderConfig.ORIENTATION), "Orientation: " + e.getOrientation().name(),
						() -> {
							e.setOrientation(e.getOrientation().next());
							config.needToBeSaved();
						})
				.withAction(new ImageIcon(BuilderConfig.EDIT), "Edit Bounds", () -> {
					edit = e;
				}).withAction(new ImageIcon(BuilderConfig.DELETE), "Delete", () -> {
					config.getMap().getEdges().remove(e);
					config.needToBeSaved();
				}));
	}

	@Override
	public boolean onClick(int mouseX, int mouseY, int mouseButton) {
		if (edit != null) {
			edit = null;
			return true;
		}
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
		if (edit == null) {
			dragging = true;
			buildBox(mouseX, mouseY, newMouseX, newMouseY);
		} else if (!dragging) { // first drag
			dragging = true;
			int left, top, right, bottom;

			if (edit.getOrientation() == Orientation.BOTTOM) {
				left = edit.getX() - 10;
				top = edit.getY();
				right = edit.getX() + 10;
				bottom = edit.getY() + edit.getLength();
			} else {
				left = edit.getX();
				top = edit.getY() - 10;
				right = edit.getX() + edit.getLength();
				bottom = edit.getY() + 10;
			}

			int middle = (bottom + top) / 2;
			int center = (left + right) / 2;

			if (isIn(mouseX, mouseY, center, top, 5)) {
				modifier = MOVE_Y_TOP;
			} else if (isIn(mouseX, mouseY, left, middle, 5)) {
				modifier = MOVE_X_LEFT;
			} else if (isIn(mouseX, mouseY, right, middle, 5)) {
				modifier = MOVE_X_RIGHT;
			} else if (isIn(mouseX, mouseY, center, bottom, 5)) {
				modifier = MOVE_Y_BOTTOM;
			} else if (mouseX >= left && mouseX <= right && mouseY >= bottom && mouseY <= top) {
				modifier = MOVE_ALL;
			} else
				modifier = MOVE_NONE;

			x = mouseX;
			y = mouseY;
		} else { // next
			int dx = newMouseX - x;
			int dy = newMouseY - y;
			x = newMouseX;
			y = newMouseY;
			if (modifier == MOVE_ALL) {
				edit.setX(edit.getX() + dx);
				edit.setY(edit.getY() + dy);
			} else if (edit.getOrientation() == Orientation.BOTTOM) {
				if (modifier == MOVE_X_LEFT || modifier == MOVE_X_RIGHT) {
					edit.setX(edit.getX() + dx);
				} else if (modifier == MOVE_Y_TOP) {
					int l = edit.getLength() - dy;
					if (l < 0) {
						edit.setLength(-l);
						edit.setY(edit.getY() + l);
						modifier = MOVE_Y_BOTTOM;
					} else {
						edit.setLength(l);
						edit.setY(edit.getY() + dy);
					}
				} else if (modifier == MOVE_Y_BOTTOM) {
					int l = edit.getLength() + dy;
					if (l < 0) {
						edit.setLength(-l);
						edit.setY(edit.getY() + l);
						modifier = MOVE_Y_TOP;
					} else
						edit.setLength(l);
				}
			} else { // right
				if (modifier == MOVE_Y_TOP || modifier == MOVE_Y_BOTTOM) {
					edit.setY(edit.getY() + dy);
				} else if (modifier == MOVE_X_LEFT) {
					int l = edit.getLength() - dx;
					if (l < 0) {
						edit.setLength(-l);
						edit.setX(edit.getX() + l);
						modifier = MOVE_X_RIGHT;
					} else {
						edit.setLength(l);
						edit.setX(edit.getX() + dx);
					}
				} else if (modifier == MOVE_X_RIGHT) {
					int l = edit.getLength() + dx;
					if (l < 0) {
						edit.setLength(-l);
						edit.setX(edit.getX() + l);
						modifier = MOVE_X_LEFT;
					} else
						edit.setLength(l);
				}
			}
		}
		return true;
	}

	@Override
	public void onDraw(Graphics g, int mouseX, int mouseY, double factorX, double factorY) {
		if (edit != null) {
			int left, top, right, bottom;

			if (edit.getOrientation() == Orientation.BOTTOM) {
				left = (int) (factorX * (edit.getX() - 10));
				top = (int) (factorY * (edit.getY()));
				right = (int) (factorX * (edit.getX() + 10));
				bottom = (int) (factorY * (edit.getY() + edit.getLength()));
			} else {
				left = (int) (factorX * (edit.getX()));
				top = (int) (factorY * (edit.getY() - 10));
				right = (int) (factorX * (edit.getX() + edit.getLength()));
				bottom = (int) (factorY * (edit.getY() + 10));
			}

			int middle = (bottom + top) / 2;
			int center = (left + right) / 2;

			g.setColor(WALL_COLOR);
			g.drawRect(left, top, right - left, bottom - top);

			g.setColor(MapPanel.WALL_COLOR);
			g.fillOval(left - 5, middle - 5, 10, 10);
			g.fillOval(right - 5, middle - 5, 10, 10);
			g.fillOval(center - 5, top - 5, 10, 10);
			g.fillOval(center - 5, bottom - 5, 10, 10);

		} else if (dragging) {
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
		if (edit != null) {
			if (modifier != 0)
				config.needToBeSaved();
			return true;
		}
		buildBox(mouseX, mouseY, newMouseX, newMouseY);
		if (w > 2)
			config.getMap().getEdges().add(0, new MapEdge(x, y, w, MapEdge.Orientation.RIGHT));
		else if (h > 2)
			config.getMap().getEdges().add(0, new MapEdge(x, y, h, MapEdge.Orientation.BOTTOM));
		else
			return true;
		config.needToBeSaved();
		return true;
	}
}
