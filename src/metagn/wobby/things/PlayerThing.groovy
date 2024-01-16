package metagn.wobby.things

import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import metagn.wobby.level.ActiveLevel
import metagn.wobby.level.ActiveThing
import metagn.wobby.level.InactiveThing
import metagn.wobby.level.Level

import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent

@InheritConstructors
@CompileStatic
class PlayerThing extends BasicThing {
	synchronized void startRight(Player p, KeyEvent e) {
		p.direction = HorDir.RIGHT
		p.accel = HorDir.RIGHT
		if (p.decel == HorDir.RIGHT) p.decel = HorDir.NONE
	}

	synchronized void startLeft(Player p, KeyEvent e) {
		p.direction = HorDir.LEFT
		p.accel = HorDir.LEFT
		if (p.decel == HorDir.LEFT) p.decel = HorDir.NONE
	}

	synchronized void stopRight(Player p, KeyEvent e) {
		p.accel = HorDir.NONE
		p.decel = HorDir.RIGHT
	}

	synchronized void stopLeft(Player p, KeyEvent e) {
		p.accel = HorDir.NONE
		p.decel = HorDir.LEFT
	}

	synchronized void jump(Player p, KeyEvent e) {
		p.jumping = true
	}

	synchronized void interruptJump(Player p, KeyEvent e) {
		//p.jumping = false
	}

	ActiveThing activate(ActiveLevel level, InactiveThing it) {
		new Player(x: it.x, y: it.y, level: level, thing: it.thing)
	}

	synchronized void initialize(ActiveThing at) {
		super.initialize(at)
		final Player p = (Player) at
		p.level.panel.addKeyListener new KeyAdapter() {
			@Override
			void keyPressed(KeyEvent e) {
				if (e.keyCode == KeyEvent.VK_RIGHT)
					startRight(p, e)
				else if (e.keyCode == KeyEvent.VK_LEFT)
					startLeft(p, e)
				else if (e.keyCode == KeyEvent.VK_UP)
					jump(p, e)
			}

			@Override
			void keyReleased(KeyEvent e) {
				if (e.keyCode == KeyEvent.VK_RIGHT)
					stopRight(p, e)
				else if (e.keyCode == KeyEvent.VK_LEFT)
					stopLeft(p, e)
				else if (e.keyCode == KeyEvent.VK_UP)
					interruptJump(p, e)
			}
		}
	}

	synchronized void tick(ActiveThing at) {
		super.tick(at)
		final Player p = (Player) at

		if (p.decel != HorDir.NONE) {
			final o = p.decel
			if (p.speeds[o] != 0) p.speeds[o] = clamp(p.speeds[o] - 2.5d, 10, 0)
		}

		if (p.accel != HorDir.NONE) {
			final o = p.accel
			p.speeds[o] = clamp(p.speeds[o] + 2.5d, 10, 0)
		}

		final hzm = p.speeds[0] - p.speeds[1]

		if (hzm != 0) {
			final hzmax = (int) Math.round(Math.abs(hzm))
			p.x = (hzm > 0 ? p.x + lookLeft(at, hzmax) : p.x - lookRight(at, hzmax))
		}

		if (p.jumping) {
			p.jumpSpeed = p.jumpSpeed != 0 ? Math.max(0, p.jumpSpeed - 0.5d) : 10
			int ado = lookUp(p, (int) Math.ceil(p.jumpSpeed))
			if (ado) p.y -= ado
			else {
				p.jumping = false
				p.jumpSpeed = 0
				p.fallSpeed = 0
			}
		} else if (getLevelGravity(p.level) && p.y < p.level.maxY &&
				(p.fallSpeed += getLevelGravity(p.level)) != 0) {
			final fallMax = (int) Math.abs(Math.round(p.fallSpeed))
			final oldY = p.y
			if (p.fallSpeed > 0) p.y += lookDown(p, fallMax)
			else p.y -= lookUp(p, fallMax)
			//if (p.y - oldY >)
		}
	}

	static double clamp(double x, double y, double z) {
		if (x > y) return y
		if (x < z) return z
		x
	}

	static int lookLeft(ActiveThing at, int max) {
		final width = at.thing.getWidth(at), height = at.thing.getHeight(at)
		int mj = max
		for (int y = 0; y < height; ++y) {
			def ş = widel(at, width, y)
			if (ş == 0) continue
			int a = (width - ş) + at.x
			int m = 1
			for (; m <= (max + 1); ++m)
				if (isSolidAt(at.level, a - m, at.y + y))
					break
			if (mj > --m) mj = m
		}
		mj
	}

	static int lookRight(ActiveThing at, int max) {
		final width = at.thing.getWidth(at), height = at.thing.getHeight(at)
		int mj = max
		for (int y = 0; y < height; ++y) {
			def ş = wider(at, width, y)
			if (ş == 0) continue
			int a = ş + at.x
			int m = 1
			for (; m <= (max + 1); ++m)
				if (isSolidAt(at.level, a + m, at.y + y))
					break
			if (mj > --m) mj = m
		}
		mj
	}

	static int lookDown(ActiveThing at, int max) {
		final width = at.thing.getWidth(at), height = at.thing.getHeight(at)
		int mj = max
		for (int x = 0; x < width; ++x) {
			def ş = highr(at, height, x)
			if (ş == 0) continue
			int a = ş + at.y
			int m = 1
			for (; m <= (max + 1); ++m)
				if (isSolidAt(at.level, at.x + x, a + m))
					break
			if (mj > --m) mj = m
		}
		mj
	}

	static int lookUp(ActiveThing at, int max) {
		final width = at.thing.getWidth(at), height = at.thing.getHeight(at)
		int mj = max
		for (int x = 0; x < width; ++x) {
			def ş = highl(at, height, x)
			if (ş == 0) continue
			int a = (height - ş) + at.y
			int m = 1
			for (; m <= (max + 1); ++m)
				if (isSolidAt(at.level, at.x + x, a - m))
					break
			if (mj > --m) mj = m
		}
		mj
	}

	private static int highl(ActiveThing at, int h, int x) {
		int i = 0
		while (i < h && !InterfaceWrappers.isXYSolid(at, x, i)) ++i
		h - i
	}

	private static int highr(ActiveThing at, int h, int x) {
		int i = h
		while (i > 0 && !InterfaceWrappers.isXYSolid(at, x, i - 1)) --i
		i
	}

	private static int widel(ActiveThing at, int w, int y) {
		int i = 0
		while (i < w && !InterfaceWrappers.isXYSolid(at, i, y)) ++i
		w - i
	}

	private static int wider(ActiveThing at, int w, int y) {
		int i = w
		while (i > 0 && !InterfaceWrappers.isXYSolid(at, i - 1, y)) --i
		i
	}

	static boolean isSolidAt(ActiveLevel level, int a, int b) {
		def th = level.thingsIn(a, b)
		for (at in th) {
			if (InterfaceWrappers.isXYSolid(at, a - at.x, b - at.y)) return true
		}
		false
	}

	static double getLevelGravity(Level level, double defaul = 0.2d) {
		def x = level.data.gravity
		null == x ? defaul : (double) x
	}
}

@CompileStatic
interface HorDir {
	final int NONE = -1
	final int RIGHT = 0
	final int LEFT = 1
}

@CompileStatic
class Player extends ActiveThing {
	double[] speeds = new double[2]
	int direction = HorDir.RIGHT
	double jumpSpeed
	double fallSpeed
	int accel = HorDir.NONE
	int decel = HorDir.NONE
	boolean jumping
}
