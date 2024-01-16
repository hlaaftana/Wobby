package metagn.wobby.things

import groovy.transform.CompileStatic
import metagn.wobby.level.ActiveThing

@CompileStatic
interface Solidable {
	boolean isXYSolid(ActiveThing at, int x, int y)
}
