package ssixprojet.builder.tool;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ssixprojet.utils.ListenerAdaptater;

/**
 * an abstract tool
 */
@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class Tool {
	private static final Set<Tool> TOOLS = new HashSet<>();
	@EqualsAndHashCode.Include
	private String name;
	private boolean enabled = false;
	private JButton button;

	protected Tool(String name, String image) {
		TOOLS.add(this);
		this.name = name;
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

	public void onClick(int mouseX, int mouseY, int mouseButton) {}

	protected void onDisabled() {}

	public void onDrag(int newMouseX, int newMouseY, int mouseButton) {}

	public void onDraw(int mouseX, int mouseY) {}

	protected void onEnabled() {}

	public void toggle() {
		toggle(!isEnabled());
	}

	public void toggle(boolean value) {
		if (value && !isToggleTool()) {
			onEnabled();
			return;
		}
		if (enabled == value)
			return;

		if (value) {
			onDisabled();
			enabled = false;
			button.setBackground(Color.LIGHT_GRAY);
			button.setForeground(Color.BLACK);
		} else {
			TOOLS.forEach(t -> t.toggle(false));
			onEnabled();
			enabled = true;
			button.setBackground(Color.DARK_GRAY);
			button.setForeground(Color.WHITE);
		}
	}
}
