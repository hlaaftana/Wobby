package metagn.wobby.things

import groovy.transform.CompileStatic
import metagn.wobby.level.ActiveThing

@CompileStatic
class InterfaceWrappers {
	static boolean isXYSolid(ActiveThing at, int x, int y) {
		!(at.thing instanceof Solidable) || ((Solidable) at.thing).isXYSolid(at, x, y)
	}
}
