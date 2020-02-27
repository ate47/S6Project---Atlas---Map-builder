package ssixprojet.builder;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import ssixprojet.builder.tool.Tool;
import ssixprojet.common.MapEdge;
import ssixprojet.common.MapEdge.Orientation;
import ssixprojet.utils.ListenerAdaptater;

public class MapPanel extends JPanel implements ListenerAdaptater {
	public static final Color WALL_COLOR = new Color(0x80ff0000, true);
	private int mouseX, mouseY, dragOriginMouseX, dragOriginMouseY;
	private boolean drag;
	private static final long serialVersionUID = -3530646458565447203L;
	private BuilderConfig cfg;

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		double factorX = (double) getWidth() / cfg.getMap().getWidth();
		double factorY = (double) getHeight() / cfg.getMap().getHeight();
		if (cfg.getBackground() != null)
			g.drawImage(cfg.getBackground(), 0, 0, getWidth(), getHeight(), this);
		else {
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, getWidth(), getHeight());
		}

		// draw walls
		g.setColor(WALL_COLOR);
		for (MapEdge e : cfg.getMap().getEdges()) {
			if (e.getOrientation() == Orientation.RIGHT) {
				g.fillRect((int) (e.getX() * factorX), (int) (e.getY() * factorY), (int) (e.getLength() * factorX),
						Math.max(2, (int) factorY));
			} else { // BOTTOM
				g.fillRect((int) (e.getX() * factorX), (int) (e.getY() * factorY), Math.max(2, (int) factorX),
						(int) (e.getLength() * factorY));
			}
		}
		Tool.sendToSelectedTool(t -> t.onDraw(g, getRelativeX(mouseX), getRelativeY(mouseY), factorX, factorY));
	}

	public MapPanel(BuilderConfig cfg) {
		this.cfg = cfg;
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);
		setOpaque(false);
	}

	public int getRelativeX(int x) {
		return x * cfg.getMap().getWidth() / getWidth();
	}

	public int getRelativeY(int y) {
		return y * cfg.getMap().getHeight() / getHeight();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Tool.sendToSelectedTool(t -> {
			if (t.onClick(getRelativeX(e.getX()), getRelativeY(e.getY()), e.getButton()))
				repaint();
		});
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (drag) {
			Tool.sendToSelectedTool(t -> {
				if (t.onStopDrag(dragOriginMouseX, dragOriginMouseY, getRelativeX(e.getX()), getRelativeY(e.getY()),
						e.getButton()))
					repaint();
			});
			drag = false;
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
		if (!drag) {
			dragOriginMouseX = getRelativeX(e.getX());
			dragOriginMouseY = getRelativeY(e.getY());
			drag = true;
			Tool.sendToSelectedTool(t -> {
				if (t.onDrag(dragOriginMouseX, dragOriginMouseY, dragOriginMouseX, dragOriginMouseY, e.getButton()))
					repaint();
			});
		} else {
			Tool.sendToSelectedTool(t -> {
				if (t.onDrag(dragOriginMouseX, dragOriginMouseY, getRelativeX(e.getX()), getRelativeY(e.getY()),
						e.getButton()))
					repaint();
			});
		}
	}
}
