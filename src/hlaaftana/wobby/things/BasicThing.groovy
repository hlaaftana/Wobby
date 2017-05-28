package hlaaftana.wobby.things

import groovy.transform.Memoized
import hlaaftana.wobby.Util
import hlaaftana.wobby.level.ActiveThing
import hlaaftana.wobby.level.PlacedThing

import java.awt.image.BufferedImage

class BasicThing extends Thing {
	boolean staticTexture = true
	String identifier
	BufferedImage texture
	Animation animation
	int animationFrame

	{
		universalInterfaces << [isXYSolid: { ActiveThing at, int x, int y ->
			at.thing.getTexture(at).colorModel.hasAlpha() ?
				(at.thing.getTexture(at).getRGB(x, y) << 24) == 0 : true
		}, getFriction: { ActiveThing at -> 0.2 }]
	}

	BasicThing(String id, img){ identifier = id; texture = Util.toImage(img) }

	BufferedImage getTexture(PlacedThing pt){ texture }
	private __wmc = { getTexture(it).width }.memoize()
	private __hmc = { getTexture(it).height }.memoize()
	int getWidth(PlacedThing pt){
		if (staticTexture) __wmc(pt)
		else getTexture(pt).width
	}
	int getHeight(PlacedThing pt){
		if (staticTexture) __hmc(pt)
		else getTexture(pt).height
	}

	void tick(ActiveThing at){
		super.tick(at)
		if (animation && !(animationFrame == animation.frameCount && !animation.loop)){
			texture = animation.frames[animationFrame++] ?: texture
			if (animationFrame >= animation.frameCount && animation.loop) animationFrame = 0
		}
	}
}

class Animation {
	List<BufferedImage> frames = []
	boolean loop = true
	int getFrameCount(){ frames.size() }
}