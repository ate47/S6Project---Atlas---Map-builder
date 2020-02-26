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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameMap {
	private static final Gson GSON = new Gson();
	private List<MapEdge> edges = new ArrayList<>();
	private List<SpawnLocation> spawnLocations = new ArrayList<>();
	private int width, height;

	/**
	 * read the map from a Json file
	 * 
	 * @param file the Json file
	 * @return a {@link GameMap} object represented in the file
	 * @throws IOException
	 */
	public static GameMap readMap(File file) throws IOException {
		try (Reader r = new FileReader(file)) {
			return GSON.fromJson(r, GameMap.class);
		}
	}

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

	public static GameMap emptyGameMap(int width, int height) {
		GameMap map = new GameMap();
		map.width = width;
		map.height = height;
		return map;
	}
}
