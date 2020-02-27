package ssixprojet.builder;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import ssixprojet.common.MapEdge;
import ssixprojet.common.MapEdge.Orientation;
import ssixprojet.utils.ListenerAdaptater;

public class MapPanel extends JPanel implements ListenerAdaptater {
	public static final Color WALL_COLOR = new Color(0x80ff0000, true);
	private static final long serialVersionUID = -3530646458565447203L;
	private int mouseX, mouseY, dragOriginMouseX, dragOriginMouseY, mouseButton;
	private boolean drag;
	private BuilderConfig cfg;
	private JPanel contextMenu;

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

	public void hideContextMenu() {
		showContextMenu(null);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (contextMenu != null) {
			if (!(e.getX() > contextMenu.getX() && e.getY() > contextMenu.getY()
					&& e.getX() < contextMenu.getX() + contextMenu.getWidth()
					&& e.getY() < contextMenu.getY() + contextMenu.getHeight())) {
				hideContextMenu();
			}
			return;
		}
		cfg.sendToSelectedTool(t -> {
			if (t.onClick(getRelativeX(e.getX()), getRelativeY(e.getY()), e.getButton()))
				repaint();
		});
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (contextMenu != null) {
			return;
		}
		mouseX = e.getX();
		mouseY = e.getY();
		if (!drag) {
			dragOriginMouseX = getRelativeX(e.getX());
			dragOriginMouseY = getRelativeY(e.getY());
			drag = true;
			cfg.sendToSelectedTool(t -> {
				if (t.onDrag(dragOriginMouseX, dragOriginMouseY, dragOriginMouseX, dragOriginMouseY, mouseButton))
					repaint();
			});
		} else {
			cfg.sendToSelectedTool(t -> {
				if (t.onDrag(dragOriginMouseX, dragOriginMouseY, getRelativeX(e.getX()), getRelativeY(e.getY()),
						mouseButton))
					repaint();
			});
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mouseButton = e.getButton();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (drag) {
			cfg.sendToSelectedTool(t -> {
				if (t.onStopDrag(dragOriginMouseX, dragOriginMouseY, getRelativeX(e.getX()), getRelativeY(e.getY()),
						e.getButton()))
					repaint();
			});
			drag = false;
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
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
		cfg.sendToSelectedTool(t -> t.onDraw(g, getRelativeX(mouseX), getRelativeY(mouseY), factorX, factorY));
		super.paintComponent(g);
	}

	public void showContextMenu(ContextMenu menu) {
		if (menu == null) {
			if (this.contextMenu != null) {
				this.remove(contextMenu);
				this.contextMenu = null;
				repaint();
			}
		} else {
			JPanel panel = menu.getPanel(mouseX, mouseY, getWidth(), getHeight());
			menu.closeAction = this::hideContextMenu;
			if (this.contextMenu != panel) {
				if (this.contextMenu != null) {
					this.remove(contextMenu);
				}
				this.add(contextMenu = panel);
			}
			repaint();
		}
	}
}
