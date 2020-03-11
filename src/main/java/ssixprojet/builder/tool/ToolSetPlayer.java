package ssixprojet.builder.tool;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;

import ssixprojet.builder.BuilderConfig;
import ssixprojet.utils.ImageLoader;

public class ToolSetPlayer extends Tool {
	public static final Image PLAYER_IMAGE = ImageLoader.loadImage("/player.png");
	private static final int MOVE_NONE = 0;
	private static final int MOVE_X_LEFT = 1;
	private static final int MOVE_Y_TOP = 2;
	private static final int MOVE_Y_BOTTOM = 4;
	private static final int MOVE_X_RIGHT = 8;
	private static final int MOVE_ALL = MOVE_X_LEFT | MOVE_X_RIGHT | MOVE_Y_BOTTOM | MOVE_Y_TOP;
	private int x, y;

	private boolean drag = false;
	private int oldx, oldy;
	private int modifier = MOVE_NONE;

	public ToolSetPlayer(BuilderConfig config) {
		super("Player Size", "/tool_player.png", config);
	}

	@Override
	public boolean onDrag(int mouseX, int mouseY, int newMouseX, int newMouseY, int mouseButton) {
		if (mouseButton != MouseEvent.BUTTON1)
			return false;

		if (drag) {
			int dx = newMouseX - oldx;
			int dy = newMouseY - oldy;
			oldx = newMouseX;
			oldy = newMouseY;
			int size = config.getMap().getPlayerSize();

			if (modifier == MOVE_ALL) {
				x += dx;
				y += dy;
				return true;
			} else if (modifier == MOVE_X_LEFT) {
				y += size / 2;
				if (dx < size) {
					x += dx;
					size -= dx;
				} else {
					x += size;
					modifier = MOVE_X_RIGHT;
					size = dx - size;
				}
				y -= size / 2;
			} else if (modifier == MOVE_X_RIGHT) {
				y += size / 2;
				if (-dx < size) {
					size += dx;
				} else {
					size = -dx - size;
					x -= size;
					modifier = MOVE_X_LEFT;
				}
				y -= size / 2;
			} else if (modifier == MOVE_Y_TOP) {
				x += size / 2;
				if (dy < size) {
					y += dy;
					size -= dy;
				} else {
					y += size;
					modifier = MOVE_Y_BOTTOM;
					size = dy - size;
				}
				x -= size / 2;
			} else if (modifier == MOVE_Y_BOTTOM) {
				x += size / 2;
				if (-dy < size) {
					size += dy;
				} else {
					size = -dy - size;
					y -= size;
					modifier = MOVE_Y_TOP;
				}
				x -= size / 2;
			} else
				return false;
			config.getMap().setPlayerSize(size);
			config.needToBeSaved();
			return true;
		} else {
			int size = config.getMap().getPlayerSize();
			int left = x;
			int right = x + size;
			int top = y;
			int bottom = y + size;
			int center = (left + right) / 2;
			int middle = (bottom + top) / 2;

			if (isIn(mouseX, mouseY, center, top, 5)) {
				modifier = MOVE_Y_TOP;
			} else if (isIn(mouseX, mouseY, left, middle, 5)) {
				modifier = MOVE_X_LEFT;
			} else if (isIn(mouseX, mouseY, right, middle, 5)) {
				modifier = MOVE_X_RIGHT;
			} else if (isIn(mouseX, mouseY, center, bottom, 5)) {
				modifier = MOVE_Y_BOTTOM;
			} else if (isIn(mouseX, mouseY)) {
				modifier = MOVE_ALL;
			} else
				modifier = MOVE_NONE;

			oldx = mouseX;
			oldy = mouseY;
			drag = true;
		}
		return false;

	}

	@Override
	public void onDraw(Graphics g, int mouseX, int mouseY, double factorX, double factorY) {
		g.setColor(Color.GREEN);
		int size = config.getMap().getPlayerSize();
		g.drawRect((int) (x * factorX), (int) (y * factorY), (int) (size * factorX), (int) (size * factorY));
		g.drawImage(PLAYER_IMAGE, (int) (x * factorX), (int) (y * factorY), (int) (size * factorX),
				(int) (size * factorY), null);

		int left = (int) (factorX * x);
		int right = (int) (factorX * (x + size));
		int top = (int) (factorY * y);
		int bottom = (int) (factorY * (y + size));
		int center = (left + right) / 2;
		int middle = (bottom + top) / 2;

		g.fillOval(center - 5, top - 5, 10, 10);
		g.fillOval(left - 5, middle - 5, 10, 10);
		g.fillOval(right - 5, middle - 5, 10, 10);
		g.fillOval(center - 5, bottom - 5, 10, 10);
	}

	@Override
	public boolean onStopDrag(int mouseX, int mouseY, int newMouseX, int newMouseY, int mouseButton) {
		drag = false;
		return false;
	}

	private boolean isIn(int mouseX, int mouseY) {
		return mouseX > x && mouseY > y && mouseX < x + config.getMap().getPlayerSize()
				&& mouseY < y + config.getMap().getPlayerSize();
	}
}
