package ssixprojet.common;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Data
public class SpawnLocation {
	private boolean outside;
	private int x, y, width, height;

	/**
	 * test if a point is in
	 * 
	 * @param x
	 *            the x location to try
	 * @param y
	 *            the y location to try
	 * @return true if the location is in the spawn location, false otherwise
	 */
	public boolean isIn(int x, int y) {
		return x >= this.x && x <= this.x + width && y >= this.y && y <= this.y + height;
	}

	public void updateMapDimension(int width, int height, int oldWidth, int oldHeight) {
		x = (x * width) / oldWidth;
		y = (y * height) / oldHeight;
		this.width = (this.width * width) / oldWidth;
		this.height = (this.height * height) / oldHeight;
	}

}
