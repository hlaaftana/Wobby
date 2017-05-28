package hlaaftana.wobby.level

import hlaaftana.wobby.things.Thing

abstract class PlacedThing {
	Level level
	Thing thing
	int x, y

	boolean contains(int a, int b){
		(a >= x) && (a < (x + thing.getWidth(this))) && (b >= y) && (b < (y + thing.getHeight(this)))
	}
}
