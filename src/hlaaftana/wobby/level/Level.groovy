package hlaaftana.wobby.level

import groovy.transform.CompileStatic

@CompileStatic
abstract class Level<T extends PlacedThing> {
	Map data = [:]
	int maxX
	int maxY
	abstract List<T> thingsIn(int x, int y)
}
