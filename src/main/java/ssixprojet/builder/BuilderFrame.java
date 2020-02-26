package ssixprojet.builder;

import javax.swing.JFrame;

public class BuilderFrame extends JFrame {
	private static final long serialVersionUID = -5711038942503549478L;

	public BuilderFrame(BuilderConfig cfg) {
		super("Atlas builder");
		setContentPane(new MapPanel(cfg));
		setSize(getContentPane().getSize());
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setIconImage(BuilderConfig.ICO);
		setVisible(true);
	}
}
