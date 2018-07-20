package hlaaftana.wobby.things;

import hlaaftana.wobby.level.*;

import java.awt.image.BufferedImage;

public interface Thing {
	String getIdentifier();

	BufferedImage getTexture(PlacedThing pt);

	int getWidth(PlacedThing pt);

	int getHeight(PlacedThing pt);

	default ActiveThing activate(ActiveLevel level, InactiveThing it) {
		ActiveThing thing = new ActiveThing();
		thing.setLevel(level);
		thing.setX(it.getX());
		thing.setY(it.getY());
		thing.setThing(it.getThing());
		return thing;
	}

	default InactiveThing inactive(InactiveLevel level, int x, int y) {
		InactiveThing thing = new InactiveThing();
		thing.setLevel(level);
		thing.setX(x);
		thing.setY(y);
		thing.setThing(this);
		return thing;
	}

	default void initialize(ActiveThing at) {}
	default void tick(ActiveThing at) {}
}
