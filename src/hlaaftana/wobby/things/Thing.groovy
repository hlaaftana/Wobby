package hlaaftana.wobby.things

import groovy.transform.CompileStatic
import hlaaftana.wobby.level.*

import java.awt.image.BufferedImage

@CompileStatic
abstract class Thing {
	Map universalInterfaces = [:]

	abstract String getIdentifier()

	abstract BufferedImage getTexture(PlacedThing pt)

	abstract int getWidth(PlacedThing pt)

	abstract int getHeight(PlacedThing pt)

	ActiveThing activate(ActiveLevel level, InactiveThing it) {
		new ActiveThing(level: level, x: it.x, y: it.y, thing: it.thing)
	}

	InactiveThing inactive(InactiveLevel level, int x, int y) {
		new InactiveThing(level: level, x: x, y: y, thing: this)
	}

	void initialize(ActiveThing at) {}

	void tick(ActiveThing at) {}
}
