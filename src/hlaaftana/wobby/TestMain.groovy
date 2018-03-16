package hlaaftana.wobby

import groovy.transform.CompileStatic
import hlaaftana.wobby.level.InactiveLevel
import hlaaftana.wobby.things.BasicThing
import hlaaftana.wobby.things.InterfaceWrappers
import hlaaftana.wobby.things.PlayerThing

@CompileStatic
class TestMain {
	static main(args){
		for (t in new File("tiles").list()) {
			GameData.register(new BasicThing(t, new File("tiles/$t")))
		}
		GameData.register(new PlayerThing('player', new File('tiles/blue_crystal_block.png')))
		def x = InactiveLevel.decode(new File('D:/AtaDoruk/Documents/axe4.lvl').bytes)
				.activate().things[0]
		def height = x.thing.getHeight(x)
		def l = new ArrayList<ArrayList<Boolean>>(height)
		for (int b = 0; b < height; ++b) {
			def width = x.thing.getWidth(x)
			def m = new ArrayList<Boolean>(width)
			for (int a = 0; a < width; ++a) {
				m.add(InterfaceWrappers.isXYSolid(x, a, b))
			}
			l.add(m)
		}
		println l.join('\n')
	}
}
