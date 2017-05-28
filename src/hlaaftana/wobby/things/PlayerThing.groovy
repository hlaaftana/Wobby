package hlaaftana.wobby.things

import groovy.transform.InheritConstructors
import hlaaftana.wobby.level.ActiveLevel
import hlaaftana.wobby.level.ActiveThing
import hlaaftana.wobby.level.Level

import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent

@InheritConstructors
class PlayerThing extends BasicThing {
	static Map<ActiveThing, Map> datas = [:]

	synchronized startRight(ActiveThing at, KeyEvent e){
		datas[at].direction = HorDir.RIGHT
		datas[at].accel_dir = HorDir.RIGHT
		if (datas[at].decel_dir == HorDir.RIGHT) datas[at].decel_dir = null
	}

	synchronized startLeft(ActiveThing at, KeyEvent e){
		datas[at].direction = HorDir.LEFT
		datas[at].accel_dir = HorDir.LEFT
		if (datas[at].decel_dir == HorDir.LEFT) datas[at].decel_dir = null
	}

	synchronized stopRight(ActiveThing at, KeyEvent e){
		datas[at].accel_dir = null
		datas[at].decel_dir = HorDir.RIGHT
	}

	synchronized stopLeft(ActiveThing at, KeyEvent e){
		datas[at].accel_dir = null
		datas[at].decel_dir = HorDir.LEFT
	}

	synchronized jump(ActiveThing at, KeyEvent e){
		datas[at].jumping = true
	}

	synchronized interruptJump(ActiveThing at, KeyEvent e){
		datas[at].jumping = false
	}

	synchronized void initialize(ActiveThing at){
		super.initialize(at)
		datas[at] = [horizontal_speeds: [:], fall_speed: 0, direction: HorDir.RIGHT]
		((ActiveLevel) at.level).panel.addKeyListener(new KeyAdapter(){
			@Override
			void keyPressed(KeyEvent e){
				if (e.keyCode == KeyEvent.VK_RIGHT)
					startRight(at, e)
				if (e.keyCode == KeyEvent.VK_LEFT)
					startLeft(at, e)
				if (e.keyCode == KeyEvent.VK_UP)
					jump(at, e)
			}

			@Override
			void keyReleased(KeyEvent e){
				if (e.keyCode == KeyEvent.VK_RIGHT)
					stopRight(at, e)
				if (e.keyCode == KeyEvent.VK_LEFT)
					stopLeft(at, e)
				if (e.keyCode == KeyEvent.VK_UP)
					interruptJump(at, e)
			}
		})
	}

	synchronized void tick(ActiveThing at){
		super.tick(at)
		def d = datas[at]
		if (d.decel_dir){
			if (d.horizontal_speeds[d.decel_dir])
				d.horizontal_speeds[d.decel_dir] = betw(d.horizontal_speeds[d.decel_dir] - 2.5, 10, 0)
		}
		if (d.accel_dir){
			if (!d.horizontal_speeds[d.accel_dir]) d.horizontal_speeds[d.accel_dir] = 0
			d.horizontal_speeds[d.accel_dir] = betw(d.horizontal_speeds[d.accel_dir] + 2.5, 10, 0)
		}
		def hzm = d.horizontal_speeds.inject(0){ s, k, v -> s + k * v }
		if (hzm) at.x += Math.round((hzm <=> 0) * look(at, hzm > 0 ? 1 : 2,
			(int) Math.abs(Math.round(hzm))))
		if (datas[at].jumping){
			if (datas[at].jump_speed) datas[at].jump_speed = [0, datas[at].jump_speed - 0.5].max()
			else datas[at].jump_speed = 10
			int ado = look(at, 4, (int) Math.abs(Math.ceil(datas[at].jump_speed)))
			if (ado) at.y -= Math.round(ado)
			else { datas[at].jumping = false; datas[at].jump_speed = 0 }
		}else if (getLevelGravity(at.level) && at.y < at.level.maxY){
			d.fall_speed += getLevelGravity(at.level)
			if (d.fall_speed) at.y += Math.round((d.fall_speed <=> 0) *
				look(at, d.fall_speed > 0 ? 3 : 4, (int) Math.abs(Math.round(d.fall_speed))))
		}
	}

	static betw(x, y, z){
		if (x > y) return y
		if (x < z) return z
		x
	}

	/// Returns the amount you can move slide to the left slide to the right left look lets dance
	static int look(ActiveThing at, int dir, int max){
		if (dir == 1){ // right
			int mj = max
			for (y in (0..<at.thing.getTexture(at).height)){
				def ş = (0..<at.thing.getTexture(at).width).collect { x ->
					at.thing.universalInterfaces.isXYSolid.with { it ?
							it(at, x, y) : true }
				}.reverse().dropWhile { !it }.reverse()
				int a
				if (ş) a = ş.size() + at.x
				else continue
				int m = 1
				for (; m <= (max + 1); ++m){
					if (isSolidAt(at.level, a + m, at.y + y)) break
				}
				if (mj > --m) mj = m
			}
			return mj
		}
		if (dir == 2){ // left
			int mj = max
			for (y in (0..<at.thing.getTexture(at).height)){
				def ş = (0..<at.thing.getTexture(at).width).collect { x ->
					at.thing.universalInterfaces.isXYSolid.with { it ?
							it(at, x, y) : true }
				}.dropWhile { !it }
				int a
				if (ş) a = (at.thing.getTexture(at).width - ş.size()) + at.x
				else continue
				int m = 1
				for (; m <= (max + 1); ++m){
					if (isSolidAt(at.level, a - m, at.y + y)) break
				}
				if (mj > --m) mj = m
			}
			return mj
		}
		if (dir == 3){ // down
			int mj = max
			for (x in (0..<at.thing.getTexture(at).width)){
				def ş = (0..<at.thing.getTexture(at).height).collect { y ->
					at.thing.universalInterfaces.isXYSolid.with { it ?
							it(at, x, y) : true }
				}.reverse().dropWhile { !it }.reverse()
				int a
				if (ş) a = ş.size() + at.y
				else continue
				int m = 1
				for (; m <= (max + 1); ++m){
					if (isSolidAt(at.level, at.x + x, a + m)) break
				}
				if (mj > --m) mj = m
			}
			return mj
		}
		if (dir == 4){ // up
			int mj = max
			for (x in (0..<at.thing.getTexture(at).width)){
				def ş = (0..<at.thing.getTexture(at).height).collect { y ->
					at.thing.universalInterfaces.isXYSolid.with { it ?
							it(at, x, y) : true }
				}.dropWhile { !it }
				int a
				if (ş) a = (at.thing.getTexture(at).height - ş.size()) + at.y
				else continue
				int m = 1
				for (; m <= (max + 1); ++m){
					if (isSolidAt(at.level, at.x + x, a - m)) break
				}
				if (mj > --m) mj = m
			}
			return mj
		}
		throw new IllegalArgumentException('Invalid direction')
	}

	static isSolidAt(Level level, int a, int b){
		def th = level.thingsIn(a, b)
		for (at in th){
			def solid = at.thing.universalInterfaces.isXYSolid
			if (null == solid || solid(at, a - at.x, b - at.y)) return true
		}
		false
	}

	static getLevelGravity(Level level, defaul = 0.1){
		level.data.gravity ?: defaul
	}

	private static enum HorDir { RIGHT(1), LEFT(-1); int x; HorDir(a){ x = a };
		def multiply(a){ x * a } }
}
