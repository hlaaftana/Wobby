package hlaaftana.wobby

import hlaaftana.wobby.level.InactiveLevel
import hlaaftana.wobby.things.BasicThing
import hlaaftana.wobby.things.PlayerThing

class TestMain {
	static main(args){
		new File("tiles").list().each { t ->
			GameData.register(new BasicThing(t, new File("tiles/$t")))
		}
		GameData.register(new PlayerThing('player', new File('tiles/blue_crystal_block.png')))
		println InactiveLevel.decode(new File('D:/AtaDoruk/Documents/axe4.lvl').bytes).activate().things[0].with {
			(0..<thing.getHeight(it)).collect { b ->
				(0..<thing.getWidth(it)).collect { a ->
					thing.universalInterfaces.isXYSolid(it, a, b)
				}
			}
		}.join('\n')
	}
}
