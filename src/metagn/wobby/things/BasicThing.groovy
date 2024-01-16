package metagn.wobby.things

import groovy.transform.CompileStatic
import groovy.transform.Memoized
import metagn.wobby.Util
import metagn.wobby.level.ActiveThing
import metagn.wobby.level.PlacedThing

import java.awt.image.BufferedImage

@CompileStatic
class BasicThing implements Thing, Solidable {
	boolean staticTexture = true
	String identifier
	BufferedImage texture
	Animation animation
	int animationFrame

	BasicThing(String id, img) { identifier = id; texture = Util.toImage(img) }
	BasicThing(String id, List frames, boolean loop = true) {
		identifier = id
		animation = new Animation(loop: loop, frames: new ArrayList<>(frames.size()))
		for (f in frames) animation.frames.add(Util.toImage(f))
	}

	BufferedImage getTexture(PlacedThing pt) { texture }

	int getWidth(PlacedThing pt) {
		staticTexture ? getMemoizedWidth(pt) : getTexture(pt).width
	}

	@Memoized
	private int getMemoizedWidth(PlacedThing pt) { getTexture(pt).width }

	int getHeight(PlacedThing pt) {
		staticTexture ? getMemoizedHeight(pt) : getTexture(pt).height
	}

	@Memoized
	private int getMemoizedHeight(PlacedThing pt) { getTexture(pt).height }

	boolean isXYSolid(ActiveThing at, int x, int y) {
		def t = at.thing.getTexture(at)
		!t.colorModel.hasAlpha() || t.getRGB(x, y) << 24 == 0
	}

	void tick(ActiveThing at) {
		if (null != animation && (animationFrame != animation.frameCount || animation.loop)) {
			texture = animation.frames[animationFrame++] ?: texture
			if (animationFrame >= animation.frameCount && animation.loop) animationFrame = 0
		}
	}

	void initialize(ActiveThing at) {}
}

@CompileStatic
class Animation {
	List<BufferedImage> frames = []
	boolean loop = true

	int getFrameCount() { frames.size() }
}
