package ssixprojet.builder.tool;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.List;

import ssixprojet.builder.BuilderConfig;
import ssixprojet.builder.MapPanel;
import ssixprojet.common.MapEdge;
import ssixprojet.common.MapEdge.Orientation;

public class ToolDeleteWall extends Tool {

	private int x, y, w, h;

	private boolean dragging = false;
	public ToolDeleteWall(BuilderConfig config) {
		super("Wall spliter", "/tool_deletewall.png", config);
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
			g.setColor(MapPanel.WALL_COLOR);
			g.fillRect((int) (x * factorX), (int) (y * factorY), (int) (w * factorX), (int) (h * factorY));
		}
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

	private void clearArea() {
		List<MapEdge> edges = config.getMap().getEdges();
		boolean inA, inB, betweenX, betweenY;
		MapEdge e;
		int delta;
		boolean change = false;
		for (int i = 0; i < edges.size(); i++) {
			e = edges.get(i);

			betweenX = e.getX() >= x && e.getX() <= x + w;
			betweenY = e.getY() >= y && e.getY() <= y + h;
			// wall = (A,B) A=(x,y) B=(x+l,y) or (x,y+l)
			inA = betweenX && betweenY;
			inB = (e.getOrientation() == Orientation.BOTTOM)
					? betweenX && e.getY() + e.getLength() >= y && e.getY() + e.getLength() <= y + h
					: betweenY && e.getX() + e.getLength() >= x && e.getX() + e.getLength() <= x + w;

			if (inA) {
				if (inB) {
					// all the edge is in the box so we delete it
					edges.remove(i);
					i--; // replace on the same index
				} else {
					// reducing the length to be on the edge of the box
					if (e.getOrientation() == Orientation.BOTTOM) {
						delta = h - (e.getY() - y);
						e.setY(e.getY() + delta);
						e.setLength(e.getLength() - delta);
					} else {
						delta = w - (e.getX() - x);
						e.setX(e.getX() + delta);
						e.setLength(e.getLength() - delta);
					}
				}
			} else if (inB) {
				// reducing the length to be on the edge of the box
				if (e.getOrientation() == Orientation.BOTTOM) {
					e.setLength(y - e.getY());
				} else {
					e.setLength(x - e.getX());
				}
			} else {
				// between?
				if (e.getOrientation() == Orientation.BOTTOM) {
					if (betweenX && e.getY() <= y && e.getY() + e.getLength() >= y + h) {
						// create the top edge
						edges.add(i, new MapEdge(e.getX(), e.getY(), y - e.getY(), Orientation.BOTTOM));
						// reducing and translate the bottom edge
						delta = y + h - e.getY();
						e.setY(e.getY() + delta);
						e.setLength(e.getLength() - delta);
						i++; // pass the new element
					} else
						continue; // don't set change
				} else {
					if (betweenY && e.getX() <= x && e.getX() + e.getLength() >= x + w) {
						// create the top edge
						edges.add(i, new MapEdge(e.getX(), e.getY(), x - e.getX(), Orientation.RIGHT));
						// reducing and translate the bottom edge
						delta = x + w - e.getX();
						e.setX(e.getX() + delta);
						e.setLength(e.getLength() - delta);
						i++; // pass the new element
					} else
						continue; // don't set change
				}
			}
			change = true;
		}

		if (change)
			config.needToBeSaved();
	}
}
