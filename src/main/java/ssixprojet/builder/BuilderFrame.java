package ssixprojet.builder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToolBar;

import ssixprojet.builder.tool.Tool;
import ssixprojet.builder.tool.ToolSave;
import ssixprojet.builder.tool.ToolSpawn;
import ssixprojet.builder.tool.ToolWall;
import ssixprojet.utils.ListenerAdaptater;

public class BuilderFrame extends JFrame {
	private static final long serialVersionUID = -5711038942503549478L;
	private JToolBar bar;

	private void registerTool(Tool... tools) {
		for (Tool tool : tools) {
			bar.add(tool.getButton());
		}
	}

	public BuilderFrame(BuilderConfig cfg) {
		super("Atlas map builder - Editor");
		JPanel contentPane = new JPanel(new BorderLayout());
		bar = new JToolBar(JToolBar.HORIZONTAL);
		bar.setBackground(Color.WHITE);

		registerTool(new ToolSave(cfg), new ToolWall(cfg), new ToolSpawn(cfg));

		bar.add(new JSeparator(JSeparator.HORIZONTAL));
		contentPane.add(bar, BorderLayout.NORTH);
		contentPane.add(new MapPanel(cfg), BorderLayout.CENTER);

		contentPane.setSize(cfg.getBackground().getWidth(), cfg.getBackground().getHeight() + bar.getWidth());
		setContentPane(contentPane);
		setSize(getContentPane().getSize());
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new ListenerAdaptater() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (cfg.isNeedToBeSaved()) {
					final String SAVE = "Save";
					switch (JOptionPane.showOptionDialog(BuilderFrame.this, "The map has beed modified. Save changes?",
							"Save map", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null,
							new Object[] { SAVE, "Don't Save", "Cancel" }, SAVE)) {
					case 0: // Save
						cfg.saveMap();
						break;
					case 1: // Don't save
						break;
					default: // cancel
						return;
					}
				}
				System.exit(0);
			}
		});
		setIconImage(BuilderConfig.ICO);
		setLocationRelativeTo(null);
		setVisible(true);
	}
}
