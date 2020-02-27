package ssixprojet.builder;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToolBar;

import ssixprojet.builder.tool.Tool;
import ssixprojet.builder.tool.ToolSave;
import ssixprojet.builder.tool.ToolWall;

public class BuilderFrame extends JFrame {
	private static final long serialVersionUID = -5711038942503549478L;
	private JToolBar bar;

	private void registerTool(Tool... tools) {
		for (Tool tool : tools) {
			bar.add(tool.getButton());
		}
	}

	public BuilderFrame(BuilderConfig cfg) {
		super("Atlas builder");
		JPanel contentPane = new JPanel(new BorderLayout());
		bar = new JToolBar(JToolBar.HORIZONTAL);
		bar.setBackground(Color.WHITE);

		registerTool(new ToolSave(cfg), new ToolWall());
		
		bar.add(new JSeparator(JSeparator.HORIZONTAL));
		contentPane.add(bar, BorderLayout.NORTH);
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
