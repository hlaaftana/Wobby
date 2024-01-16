package metagn.wobby.things

import groovy.transform.CompileStatic
import metagn.wobby.Util
import metagn.wobby.level.*

import javax.swing.event.MouseInputAdapter
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage

@CompileStatic
class LevelSelectorThing extends BasicThing {
	BufferedImage normalTexture, hoverTexture
	InactiveLevel destination

	LevelSelectorThing(String id, img, hoverimg, InactiveLevel l) {
		super(id, img)
		staticTexture = false
		normalTexture = Util.toImage(img)
		hoverTexture = Util.toImage(hoverimg)
		destination = l
	}

	BufferedImage getTexture(PlacedThing pt) {
		if (pt instanceof LevelSelector && ((LevelSelector) pt).hovering) hoverTexture
		else normalTexture
	}

	ActiveThing activate(ActiveLevel level, InactiveThing it) {
		new LevelSelector(x: it.x, y: it.x, level: level, thing: this)
	}

	void initialize(ActiveThing at) {
		def ls = (LevelSelector) at
		ls.level.panel.addMouseListener new MouseInputAdapter() {
			void mouseMoved(MouseEvent e) {
				if (!ls.dead) ls.hovering = at.contains(e.x, e.y)
			}

			void mouseClicked(MouseEvent e) {
				if (ls.contains(e.x, e.y) && !ls.dead) {
					ls.level.panel.tickingThread.interrupt()
					ls.level.panel.level = destination.activate()
					ls.level.panel.level.data.world_map = at.level
					ls.level.panel.initialize()
					ls.dead = true
				}
			}
		}
	}
}

@CompileStatic
class LevelSelector extends ActiveThing {
	boolean hovering, dead
}
