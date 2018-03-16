package hlaaftana.wobby.things

import groovy.transform.CompileStatic
import groovy.transform.Memoized
import hlaaftana.wobby.Util
import hlaaftana.wobby.level.ActiveThing
import hlaaftana.wobby.level.PlacedThing

import java.awt.image.BufferedImage

@CompileStatic
class BasicThing extends Thing implements Solidable {
	boolean staticTexture = true
	String identifier
	BufferedImage texture
	Animation animation
	int animationFrame

	BasicThing(String id, img) { identifier = id; texture = Util.toImage(img) }

	BufferedImage getTexture(PlacedThing pt) { texture }

	int getWidth(PlacedThing pt) {
		if (staticTexture) getMemoizedWidth(pt)
		else getTexture(pt).width
	}

	@Memoized private int getMemoizedWidth(PlacedThing pt) { getTexture(pt).width }

	int getHeight(PlacedThing pt) {
		if (staticTexture) getMemoizedHeight(pt)
		else getTexture(pt).height
	}

	@Memoized private int getMemoizedHeight(PlacedThing pt) { getTexture(pt).height }

	boolean isXYSolid(ActiveThing at, int x, int y) {
		def t = at.thing.getTexture(at)
		t.colorModel.hasAlpha() ? t.getRGB(x, y) << 24 == 0 : true
	}

	void tick(ActiveThing at){
		super.tick(at)
		if (null != animation && (animationFrame != animation.frameCount || animation.loop)) {
			texture = animation.frames[animationFrame++] ?: texture
			if (animationFrame >= animation.frameCount && animation.loop) animationFrame = 0
		}
	}
}

@CompileStatic
class Animation {
	List<BufferedImage> frames = []
	boolean loop = true
	int getFrameCount() { frames.size() }
}