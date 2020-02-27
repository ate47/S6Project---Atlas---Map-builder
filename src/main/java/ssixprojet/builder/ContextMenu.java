package ssixprojet.builder;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;

import ssixprojet.utils.ListenerAdaptater;

public class ContextMenu {
	private class ContextButton extends JButton implements ActionListener, ListenerAdaptater {
		private static final long serialVersionUID = -6021073605274329264L;
		private Runnable action;

		public ContextButton(String text, Icon icon, Runnable action) {
			super(text, icon);
			setBounds(1, h - 1, w - 2, 30);
			h += 30;
			this.action = action;
			setFocusable(false);
			setBorderPainted(false);
			setBackground(Color.WHITE);
			setForeground(Color.BLACK);
			setHorizontalAlignment(LEFT);
			addActionListener(this);
			addMouseListener(this);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			setBackground(Color.LIGHT_GRAY);
			setForeground(Color.BLACK);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			setBackground(Color.WHITE);
			setForeground(Color.BLACK);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			action.run();
			ContextMenu.this.panel.setVisible(false);
			closeAction.run();
		}

	}

	private class ContextLabel extends JButton {
		private static final long serialVersionUID = -6021073605274329264L;

		public ContextLabel(String text, Icon icon) {
			super(text, icon);
			setBounds(1, h - 1, w - 2, 30);
			h += 30;
			setFocusable(false);
			setBorderPainted(false);
			setBackground(Color.WHITE);
			setForeground(Color.BLACK);
			setHorizontalAlignment(LEFT);
		}

	}

	private JPanel panel;
	Runnable closeAction;
	private int w, h = 2;

	/**
	 * get the panel located in (x, y) to
	 * 
	 * @param x
	 *            the x location to locate the panel
	 * @param y
	 *            the y location to locate the panel
	 * @param windowWidth
	 *            the max width
	 * @param windowHeight
	 *            the max height
	 * @return the panel
	 */
	JPanel getPanel(int x, int y, int windowWidth, int windowHeight) {
		int rx, ry;
		if (x + w > windowWidth) {
			if (x - w < 0)
				rx = 0;
			else
				rx = x - w;
		} else
			rx = x;

		if (y + h > windowHeight) {
			if (y - h < 0)
				ry = 0;
			else
				ry = y - h;
		} else
			ry = y;

		panel.setBounds(rx, ry, w, h);
		return panel;
	}

	public ContextMenu(int width) {
		this.w = width;
		this.panel = new JPanel() {
			private static final long serialVersionUID = -9152514883912026372L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.setColor(Color.BLACK);
				g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
			}
		};
	}

	public ContextMenu withText(Icon icon, String text) {
		panel.add(new ContextLabel(text, icon));
		return this;
	}

	public ContextMenu withAction(Icon icon, String text, Runnable action) {
		panel.add(new ContextButton(text, icon, action));
		return this;
	}

}
