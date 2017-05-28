package hlaaftana.wobby.things

import hlaaftana.wobby.level.ActiveThing
import hlaaftana.wobby.level.PlacedThing

import java.awt.image.BufferedImage

abstract class Thing {
	Map universalInterfaces = [:]
	abstract String getIdentifier()
	abstract BufferedImage getTexture(PlacedThing pt)
	abstract int getWidth(PlacedThing pt)
	abstract int getHeight(PlacedThing pt)
	void initialize(ActiveThing at){}
	void tick(ActiveThing at){}
}
