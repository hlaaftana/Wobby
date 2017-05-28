package hlaaftana.wobby.level

import hlaaftana.wobby.ui.LevelPlayPanel

class ActiveLevel extends Level {
	LinkedList<ActiveThing> things = []
	LevelPlayPanel panel

	LinkedList<ActiveThing> thingsIn(int x, int y){
		things.findAll { it.contains(x, y) } as LinkedList<ActiveThing>
	}

	void initialize(LevelPlayPanel p){
		panel = p
		things.each { it.thing.initialize(it) }
	}

	void tick(){
		things.each { it.thing.tick(it) }
	}
}
