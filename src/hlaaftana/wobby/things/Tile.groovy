package hlaaftana.wobby.things

import groovy.transform.CompileStatic
import hlaaftana.wobby.Util
import hlaaftana.wobby.level.ActiveThing
import hlaaftana.wobby.level.PlacedThing

import java.awt.image.BufferedImage

@CompileStatic
class Tile extends Thing implements Solidable {
	final String identifier
	final BufferedImage texture
	protected boolean[][] notSolid

	Tile(String id, img) {
		identifier = id
		texture = Util.toImage(img)
		notSolid = new boolean[texture.height][texture.width]
		if (texture.colorModel.hasAlpha()) {
			for (int x = 0; x < notSolid.length; x++) {
				def a = notSolid[x]
				for (int y = 0; y < a.length; y++) {
					a[y] = texture.getRGB(x, y) << 24 != 0
				}
				notSolid[x] = a
			}
		}
	}

	@Override
	boolean isXYSolid(ActiveThing at, int x, int y) {
		!notSolid[x][y]
	}

	BufferedImage getTexture(PlacedThing pt) { texture }

	int getWidth(PlacedThing pt) { texture.width }

	int getHeight(PlacedThing pt) { texture.height }
}
