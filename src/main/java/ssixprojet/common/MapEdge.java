package ssixprojet.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ssixprojet.utils.Vector;

/**
 * represent a edge of the map
 */
@NoArgsConstructor
@AllArgsConstructor
public @Data class MapEdge {
	/**
	 * the orientation
	 */
	public enum Orientation {
		RIGHT(new Vector(1, 0)), BOTTOM(new Vector(0, 1));

		public final Vector desc;

		Orientation(Vector desc) {
			this.desc = desc;
		}

		public Orientation next() {
			return Orientation.values()[(ordinal() + 1) % Orientation.values().length];
		}
	}

	private int x, y, length;
	private Orientation orientation;

	public void updateMapDimension(int width, int height, int oldWidth, int oldHeight) {
		x = (x * width) / oldWidth;
		y = (y * height) / oldHeight;
		length = (length * width) / oldWidth;
	}
}
