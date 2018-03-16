package hlaaftana.wobby

import groovy.transform.CompileStatic
import hlaaftana.wobby.level.InactiveLevel
import hlaaftana.wobby.things.BasicThing
import hlaaftana.wobby.things.PlayerThing
import hlaaftana.wobby.ui.LevelBuildFrame

import javax.swing.*

@CompileStatic
class Main {
	static main(args) {
		for (t in new File("tiles").list()) {
			GameData.register(new BasicThing(t - '.png', new File("tiles/$t")))
		}
		GameData.register(new PlayerThing('player', new File('tiles/blue_crystal_block.png')))
		InactiveLevel l = new InactiveLevel(maxX: 750, maxY: 450)
		JFrame frame = new LevelBuildFrame(l, 450, 450)
		frame.visible = true
	}
}
