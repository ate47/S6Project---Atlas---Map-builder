package ssixprojet.builder;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ssixprojet.common.GameMap;

public class BuilderLoaderFrame extends JFrame {
	@AllArgsConstructor
	private static class FileLoaderAction implements ActionListener {
		private JTextField field;
		private String name;
		private String description;
		private String endOfFile;

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileFilter(new FileFilter() {
				@Override
				public boolean accept(File f) {
					return f.isDirectory() || f.getName().endsWith(endOfFile);
				}

				@Override
				public String getDescription() {
					return description + " (*" + endOfFile + ")";
				}
			});
			fileChooser.setSelectedFile(new File(field.getText()));
			if (fileChooser.showDialog(null, name) == JFileChooser.APPROVE_OPTION) {
				field.setText(fileChooser.getSelectedFile().getAbsolutePath());
			}
		}

	}

	@AllArgsConstructor
	@NoArgsConstructor
	private static class PreBuilderData {
		String lastImage = "";
		String lastMap = "";
	}

	private static final long serialVersionUID = 7811425860342328266L;

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final File CONFIG_FILE = new File("builder.json");

	private JLabel imageLabel, mapLabel;

	private JTextField imageField, mapField;

	public BuilderLoaderFrame() {
		super("Atlas map builder - Load files");
		setSize(600, 350);
		JPanel panel = new JPanel(null);

		PreBuilderData data = loadData();

		imageLabel = new JLabel("");
		imageLabel.setBounds(20, 40, 400, 30);
		imageLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		setImageLabel("");
		panel.add(imageLabel);

		mapLabel = new JLabel();
		mapLabel.setBounds(20, 130, 400, 30);
		mapLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		setMapLabel("");
		panel.add(mapLabel);

		imageField = new JTextField(data.lastImage);
		imageField.setBounds(20, 80, 380, 30);
		imageField.setAlignmentY(CENTER_ALIGNMENT);
		imageField.setAlignmentX(LEFT_ALIGNMENT);
		panel.add(imageField);

		JButton imageSelector = new JButton("Open file...");
		imageSelector.setBounds(420, 80, 160, 30);
		imageSelector
				.addActionListener(new FileLoaderAction(imageField, "Open background file...", "png image", ".png"));
		panel.add(imageSelector);

		mapField = new JTextField(data.lastMap);
		mapField.setBounds(20, 170, 380, 30);
		mapField.setAlignmentY(CENTER_ALIGNMENT);
		mapField.setAlignmentX(LEFT_ALIGNMENT);
		panel.add(mapField);

		JButton mapSelector = new JButton("Open file...");
		mapSelector.setBounds(420, 170, 160, 30);
		mapSelector.addActionListener(new FileLoaderAction(mapField, "Open map file...", "map file", ".json"));
		panel.add(mapSelector);

		JButton openLoader = new JButton("Launch");
		openLoader.setBounds(20, 250, getWidth() - 40, 30);
		openLoader.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				showBuilderFrame();
			}
		});
		panel.add(openLoader);

		setResizable(false);
		setLocationRelativeTo(null);
		setIconImage(BuilderConfig.ICO);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setContentPane(panel);
	}

	/**
	 * load the image and the map file to create a {@link BuilderConfig}
	 */
	public void showBuilderFrame() {
		boolean error = false;

		if (imageField.getText().isEmpty()) {
			setImageLabel("No file specified");
			error = true;
		}

		if (mapField.getText().isEmpty()) {
			setMapLabel("No file specified");
			error = true;
		}

		if (error)
			return;

		File image = new File(imageField.getText());
		File mapJson = new File(mapField.getText());

		if (!image.exists() || !image.isFile()) {
			setImageLabel("The file does not exists");
			error = true;
		}

		if (!mapJson.exists() || !mapJson.isFile()) {
			setMapLabel("The file does not exists");
			error = true;
		}

		if (error)
			return;
		Image img;

		try {
			img = ImageIO.read(image);
		} catch (IOException e) {
			setImageLabel(e.getMessage());
			return;
		}

		GameMap map;

		try {
			map = GameMap.readMap(mapJson);
		} catch (IOException e) {
			setMapLabel(e.getMessage());
			return;
		}
		saveData();
		setVisible(false);
		new BuilderConfig(map, img);
	}

	/**
	 * @return files names from the config file or null if an error occured
	 */
	private PreBuilderData loadData() {
		try (FileReader r = new FileReader(CONFIG_FILE)) {
			return GSON.fromJson(r, PreBuilderData.class);
		} catch (IOException e) {
			return new PreBuilderData();
		}
	}

	/**
	 * save current files names to the config file
	 */
	private void saveData() {
		try (FileWriter w = new FileWriter(CONFIG_FILE)) {
			GSON.toJson(new PreBuilderData(imageField.getText(), mapField.getText()), w);
		} catch (IOException e) {
		}
	}

	private void setImageLabel(String error) {
		if (!error.isEmpty()) {
			imageLabel.setText("Image file, error : " + error);
			imageLabel.setForeground(Color.RED);
		} else {
			imageLabel.setText("Image file");
			imageLabel.setForeground(Color.BLACK);
		}
	}

	private void setMapLabel(String error) {
		if (!error.isEmpty()) {
			mapLabel.setText("Map file, error : " + error);
			mapLabel.setForeground(Color.RED);
		} else {
			mapLabel.setText("Map file");
			mapLabel.setForeground(Color.BLACK);
		}
	}

}
