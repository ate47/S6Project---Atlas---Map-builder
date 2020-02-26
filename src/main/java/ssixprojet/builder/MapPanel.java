package ssixprojet.builder;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class MapPanel extends JPanel {

	private static final long serialVersionUID = -3530646458565447203L;
	private BuilderConfig cfg;

	
	@Override
	protected void paintComponent(Graphics g) {
		if (cfg.getBackground() != null)
			g.drawImage(cfg.getBackground(), 0, 0, getWidth(), getHeight(), this);
		else {
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, getWidth(), getHeight());
		}
		super.paintComponent(g);
	}


	public MapPanel(BuilderConfig cfg) {
		this.cfg = cfg;
		setOpaque(false);
	}
}
