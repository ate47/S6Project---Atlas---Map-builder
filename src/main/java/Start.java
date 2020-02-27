import javax.swing.JOptionPane;

import ssixprojet.builder.BuilderLoaderFrame;

public class Start {

	public static void main(String[] args) {
		try {
			new BuilderLoaderFrame().setVisible(true);
		} catch (Throwable e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getClass().getName() + ": " + e.getMessage(), "An error occurred",
					JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		}
	}

}
