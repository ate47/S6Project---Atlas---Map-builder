package ssixprojet.builder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import ssixprojet.builder.tool.Tool;
import ssixprojet.builder.tool.ToolWall;

public class BuilderFrame extends JFrame {
	private static final long serialVersionUID = -5711038942503549478L;
	private JToolBar bar;
	private Set<Tool> tools = new HashSet<>();

	private void registerTool(Tool... tools) {
		for (Tool tool : tools) {
			this.tools.add(tool);
			bar.add(tool.getButton());
		}
	}

	public BuilderFrame(BuilderConfig cfg) {
		super("Atlas builder");
		JPanel contentPane = new JPanel(new BorderLayout());
		bar = new JToolBar(JToolBar.VERTICAL);
		bar.setBackground(Color.WHITE);

		registerTool(new ToolWall());

		contentPane.add(bar, BorderLayout.WEST);
		contentPane.add(new MapPanel(cfg), BorderLayout.CENTER);

		contentPane.setSize(1000, 600);
		setContentPane(contentPane);
		setSize(getContentPane().getSize());
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setIconImage(BuilderConfig.ICO);
		setLocationRelativeTo(null);
		setVisible(true);
	}
}
