package hlaaftana.wobby.things

import groovy.transform.CompileStatic
import hlaaftana.wobby.level.PlacedThing

import java.awt.image.BufferedImage

@CompileStatic
class FixedSizeBasicThing extends BasicThing {
	int width, height

	FixedSizeBasicThing(String id, int w, int h, img) {
		super(id, img)
		resize(w, h)
	}

	void resize(int w, int h) {
		width = w
		height = h
		def old = texture
		texture = new BufferedImage(w, h, old.type)
		final g = texture.createGraphics()
		g.drawImage(old, w, h, null)
		g.dispose()
	}

	@Override
	int getWidth(PlacedThing pt) {
		width
	}

	@Override
	int getHeight(PlacedThing pt) {
		height
	}
}
