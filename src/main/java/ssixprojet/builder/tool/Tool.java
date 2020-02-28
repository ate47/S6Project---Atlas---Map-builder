package ssixprojet.builder.tool;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ssixprojet.builder.BuilderConfig;
import ssixprojet.utils.ListenerAdaptater;

/**
 * an abstract tool
 */
@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class Tool {

	@EqualsAndHashCode.Include
	private String name;
	private boolean enabled = false;
	private JButton button;
	protected final BuilderConfig config;

	protected Tool(String name, String image, BuilderConfig config) {
		this.name = name;
		this.config = config;
		Image icon;
		try {
			icon = ImageIO.read(Tool.class.getResourceAsStream(image));
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
		button = new JButton(name, new ImageIcon(icon));
		button.setBorderPainted(false);
		button.setBackground(Color.LIGHT_GRAY);
		button.setFocusable(false);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				toggle();
				config.getFrame().repaint();
			}
		});
		button.addMouseListener(new ListenerAdaptater() {
			@Override
			public void mouseExited(MouseEvent e) {
				if (!isEnabled()) {
					button.setBackground(Color.LIGHT_GRAY);
					button.setForeground(Color.BLACK);
				} else {
					button.setBackground(Color.DARK_GRAY);
					button.setForeground(Color.WHITE);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				if (!isEnabled()) {
					button.setBackground(Color.GRAY);
					button.setForeground(Color.WHITE);
				} else {
					button.setBackground(Color.BLACK);
					button.setForeground(Color.WHITE);
				}
			}
		});
	}

	public boolean isToggleTool() {
		return true;
	}

	public boolean onClick(int mouseX, int mouseY, int mouseButton) {
		return false;
	}

	protected void onDisabled() {}

	public boolean onDrag(int mouseX, int mouseY, int newMouseX, int newMouseY, int mouseButton) {
		return false;
	}

	public boolean onStopDrag(int mouseX, int mouseY, int newMouseX, int newMouseY, int mouseButton) {
		return false;
	}

	public void onDraw(Graphics g, int mouseX, int mouseY, double factorX, double factorY) {}

	protected void onEnabled() {}

	public void toggle() {
		toggle(!isEnabled());
	}

	public void toggle(boolean value) {
		config.showContextMenu(null);
		if (value && !isToggleTool()) {
			onEnabled();
			return;
		}
		if (enabled == value) {
			return;
		}

		if (!value) {
			onDisabled();
			enabled = false;
			config.setSelectedTool(null);
			button.setBackground(Color.LIGHT_GRAY);
			button.setForeground(Color.BLACK);
		} else {
			config.disableAllTools();
			onEnabled();
			enabled = true;
			config.setSelectedTool(this);
			button.setBackground(Color.DARK_GRAY);
			button.setForeground(Color.WHITE);
		}
	}
}
