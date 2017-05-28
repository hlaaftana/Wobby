package hlaaftana.wobby

import hlaaftana.wobby.things.Thing

class GameData {
	private static Map<String, Thing> things = [:]

	static Map<String, Thing> getThings(){
		(Map<String, Thing>) things.clone()
	}

	static register(Thing thing){
		if (!thing.identifier)
			throw new IllegalArgumentException('Thing identifier has to exist')
		if (things.containsKey(thing.identifier))
			throw new IllegalArgumentException('Thing identifier in use')
		things[thing.identifier] = thing
	}

	static thing(String identifier){
		things[identifier]
	}
}
