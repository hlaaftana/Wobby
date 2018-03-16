package hlaaftana.wobby.level

import groovy.transform.CompileStatic
import hlaaftana.wobby.ui.LevelPlayPanel

@CompileStatic
class ActiveLevel extends Level {
	List<ActiveThing> things = new ArrayList<>()
	LevelPlayPanel panel

	List<ActiveThing> thingsIn(int x, int y) {
		def l = new ArrayList<ActiveThing>()
		for (it in things) if (it.contains(x, y)) l.add(it)
		l
	}

	void initialize(LevelPlayPanel p) {
		panel = p
		for (it in things) it.thing.initialize(it)
	}

	void tick() {
		def remove = []
		for (it in things) {
			try {
				it.thing.tick(it)
			} catch (ex) {
				ex.printStackTrace()
				remove << it
			}
		}
		things.removeAll(remove)
	}
}
