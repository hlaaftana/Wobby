package hlaaftana.wobby.things

import groovy.transform.CompileStatic
import hlaaftana.wobby.level.ActiveThing

@CompileStatic
interface Solidable {
	boolean isXYSolid(ActiveThing at, int x, int y)
}
