package hlaaftana.wobby.level

abstract class Level {
	Map data = [:]
	int maxX
	int maxY
	abstract LinkedList<PlacedThing> thingsIn(int x, int y)
}
