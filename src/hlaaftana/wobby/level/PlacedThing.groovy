package hlaaftana.wobby.level

import groovy.transform.CompileStatic
import hlaaftana.wobby.things.Thing

@CompileStatic
abstract class PlacedThing<L extends Level> {
	L level
	Thing thing
	int x, y

	boolean contains(int a, int b){
		(a >= x) && (a < (x + thing.getWidth(this))) && (b >= y) && (b < (y + thing.getHeight(this)))
	}
}
