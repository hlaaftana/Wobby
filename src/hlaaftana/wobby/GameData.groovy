package hlaaftana.wobby

import groovy.transform.CompileStatic
import hlaaftana.wobby.things.Thing

@CompileStatic
class GameData {
	private static Map<String, Thing> things = new HashMap<>()

	static Map<String, Thing> getThings() {
		new HashMap<>(things)
	}

	static Thing register(Thing thing) {
		if (!thing.identifier)
			throw new IllegalArgumentException('Thing identifier has to exist')
		if (things.containsKey(thing.identifier))
			throw new IllegalArgumentException('Thing identifier in use')
		things[thing.identifier] = thing
	}

	static Thing thing(String identifier) {
		things[identifier]
	}
}
