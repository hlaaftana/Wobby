package hlaaftana.wobby

import hlaaftana.wobby.level.InactiveLevel
import hlaaftana.wobby.things.BasicThing
import hlaaftana.wobby.things.PlayerThing
import hlaaftana.wobby.ui.LevelBuildFrame

import javax.swing.JFrame

class Main {
	static main(args){
		new File("tiles").list().each { t ->
			GameData.register(new BasicThing(t, new File("tiles/$t")))
		}
		GameData.register(new PlayerThing('player', new File('tiles/blue_crystal_block.png')))
		InactiveLevel l = new InactiveLevel(maxX: 750, maxY: 450)
		JFrame frame = new LevelBuildFrame(l, 450, 450)
		frame.setVisible(true)
	}
}
