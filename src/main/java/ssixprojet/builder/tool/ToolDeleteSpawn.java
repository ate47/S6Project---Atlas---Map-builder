package ssixprojet.builder.tool;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.List;

import ssixprojet.builder.BuilderConfig;
import ssixprojet.builder.MapPanel;
import ssixprojet.common.SpawnLocation;

public class ToolDeleteSpawn extends Tool {

	private int x, y, w, h;

	private boolean dragging = false;

	public ToolDeleteSpawn(BuilderConfig config) {
		super("Spawn digger", "/tool_deletespawn.png", config);
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
			g.setXORMode(ToolSpawn.SPAWN_COLOR);
			g.drawRect((int) (x * factorX), (int) (y * factorY), (int) (w * factorX), (int) (h * factorY));
			g.setXORMode(MapPanel.SPAWN_COLOR);
			g.fillRect((int) (x * factorX), (int) (y * factorY), (int) (w * factorX), (int) (h * factorY));
		}
	}

	private static final int CLEAR_LEFT = 1;
	private static final int CLEAR_RIGHT = 2;
	private static final int CLEAR_TOP = 4;
	private static final int CLEAR_BOTTOM = 8;
	private static final int CLEAR_ALL = CLEAR_LEFT | CLEAR_BOTTOM | CLEAR_RIGHT | CLEAR_TOP;

	private void clearArea() {
		boolean change = false;
		int left = x;
		int right = x + w;
		int top = y;
		int bottom = y + h;

		boolean isFirstConsume;
		int lX, lY, lW, lH;
		int modifier;
		SpawnLocation l, toChange;
		List<SpawnLocation> locations = config.getMap().getSpawnLocations();

		for (int i = 0; i < locations.size(); i++) {
			l = locations.get(i);
			modifier = 0;

			// left side
			if (left < l.getX()) {
				if (right < l.getX())
					continue;
				modifier = CLEAR_LEFT;
			}
			// top side
			if (top < l.getY()) {
				if (bottom < l.getY())
					continue;
				modifier |= CLEAR_TOP;
			}
			// right side
			if (right > l.getX() + l.getWidth()) {
				if (left > l.getX() + l.getWidth())
					continue;
				modifier |= CLEAR_RIGHT;
			}
			// bottom side
			if (bottom > l.getY() + l.getHeight()) {
				if (top > l.getY() + l.getHeight())
					continue;
				modifier |= CLEAR_BOTTOM;
			}

			change = true;

			if (modifier == CLEAR_ALL) {
				// the box cover all the spawn, delete it
				locations.remove(i);
				i--;
			} else {
				isFirstConsume = false;
				lX = l.getX();
				lY = l.getY();
				lW = l.getWidth();
				lH = l.getHeight();

				// create the left block
				if ((modifier & CLEAR_LEFT) == 0) {
					isFirstConsume = true;
					l.setWidth(left - lX);
					lW -= l.getWidth();
					lX += l.getWidth();
				}

				// create the right block
				if ((modifier & CLEAR_RIGHT) == 0) {
					if (!isFirstConsume) {
						isFirstConsume = true;
						toChange = l;
					} else {
						// create a new spawn location for this block
						locations.add(i, toChange = new SpawnLocation(l.isOutside(), lX, lY, lW, lH));
						i++;
					}

					toChange.setX(right);
					toChange.setWidth(lX + lW - right);
					lW -= toChange.getWidth();
				}

				// create the top block
				if ((modifier & CLEAR_TOP) == 0) {
					if (!isFirstConsume) {
						isFirstConsume = true;
						toChange = l;
					} else {
						// create a new spawn location for this block
						locations.add(i, toChange = new SpawnLocation(l.isOutside(), lX, lY, lW, lH));
						i++;
					}
					toChange.setHeight(top - lY);
					lH -= toChange.getHeight();
					lY += toChange.getHeight();
				}
				
				// create the bottom block
				if ((modifier & CLEAR_BOTTOM) == 0) {
					if (!isFirstConsume) {
						isFirstConsume = true;
						toChange = l;
					} else {
						// create a new spawn location for this block
						locations.add(i, toChange = new SpawnLocation(l.isOutside(), lX, lY, lW, lH));
						i++;
					}

					toChange.setY(bottom);
					toChange.setHeight(lY + lH - bottom);
				}
			}
		}

		if (change)
			config.needToBeSaved();
	}

	@Override
	public boolean onStopDrag(int mouseX, int mouseY, int newMouseX, int newMouseY, int mouseButton) {
		if (!dragging)
			return false;

		dragging = false;
		buildBox(mouseX, mouseY, newMouseX, newMouseY);

		clearArea();
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
