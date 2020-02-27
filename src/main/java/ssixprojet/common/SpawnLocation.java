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

	public void updateMapDimension(int width, int height, int oldWidth, int oldHeight) {
		x = (x * width) / oldWidth;
		y = (y * height) / oldHeight;
		this.width = (this.width * width) / oldWidth;
		this.height = (this.height * height) / oldHeight;
	}
}
