package ssixprojet.common;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameMap {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public static GameMap emptyGameMap(int width, int height) {
		GameMap map = new GameMap();
		map.width = width;
		map.height = height;
		map.playerSize = Math.max(width, height) / 6;
		return map;
	}

	/**
	 * read the map from a Json file
	 * 
	 * @param file
	 *            the Json file
	 * @return a {@link GameMap} object represented in the file
	 * @throws IOException
	 */
	public static GameMap readMap(File file) throws IOException {
		try (Reader r = new FileReader(file)) {
			GameMap map = GSON.fromJson(r, GameMap.class);
			if (map.playerSize == 0)
				map.playerSize = Math.max(map.width, map.height) / 6;
			return map;
				
		}
	}

	private List<MapEdge> edges = new ArrayList<>();

	private List<SpawnLocation> spawnLocations = new ArrayList<>();

	private int width, height;
	@Setter
	private int playerSize;

	/**
	 * save the to a json file
	 * 
	 * @param file
	 */
	public void saveMap(File file) {
		try (Writer w = new FileWriter(file)) {
			GSON.toJson(this, w);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void updateMapDimension(int width, int height, boolean relocateRelative) {
		if (relocateRelative) {
			edges.forEach(e -> e.updateMapDimension(width, height, this.width, this.height));
			spawnLocations.forEach(s -> s.updateMapDimension(width, height, this.width, this.height));
		}
		this.width = width;
		this.height = height;
	}
}
