package metagn.wobby

import groovy.transform.CompileStatic
import metagn.wobby.level.InactiveLevel
import metagn.wobby.things.PlayerThing
import metagn.wobby.things.Tile
import metagn.wobby.ui.LevelBuildFrame

import javax.swing.*

@CompileStatic
class Main {
	static main(args) {
		for (final t : new File("tiles").list()) if (t.endsWith('.png')) {
			GameData.register(new Tile(t.substring(0, t.length() - 4), new File("tiles/".concat(t))))
		}
		GameData.register(new PlayerThing('player', new File('tiles/blue_crystal_block.png')))
		InactiveLevel l = new InactiveLevel(maxX: 750i, maxY: 450i)
		JFrame frame = new LevelBuildFrame(l, 450, 450)
		frame.visible = true
	}
}
