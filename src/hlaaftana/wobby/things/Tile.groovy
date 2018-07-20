package hlaaftana.wobby.things

import groovy.transform.CompileStatic
import hlaaftana.wobby.Util
import hlaaftana.wobby.level.ActiveThing
import hlaaftana.wobby.level.PlacedThing

import java.awt.image.BufferedImage

@CompileStatic
class Tile implements Thing, Solidable {
	final String identifier
	final BufferedImage texture
	final int width, height
	protected BitSet solid

	Tile(String id, img) {
		identifier = id
		texture = Util.toImage(img)
		width = texture.width
		height = texture.height
		solid = new BitSet(height * width)
		if (texture.colorModel.hasAlpha()) {
			for (int i = 0; i < solid.size(); ++i) {
				final x = i % width, y = Util.intdiv(i, width)
				final val = texture.getRGB(x, y) << 24 == 0
				solid.set(i, val)
			}
		} else solid.set(0, solid.size() - 1, true)
	}

	@Override
	boolean isXYSolid(ActiveThing at, int x, int y) {
		solid.get(x + y * width)
	}

	BufferedImage getTexture(PlacedThing pt) { texture }

	int getWidth(PlacedThing pt) { width }

	int getHeight(PlacedThing pt) { height }
}
