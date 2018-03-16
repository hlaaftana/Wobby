package hlaaftana.wobby.things

import groovy.transform.CompileStatic
import hlaaftana.wobby.level.ActiveThing

@CompileStatic
class InterfaceWrappers {
	static boolean isXYSolid(ActiveThing at, int x, int y) {
		if (at.thing instanceof Solidable) ((Solidable) at.thing).isXYSolid(at, x, y)
		else true
	}
}
