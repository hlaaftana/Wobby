package hlaaftana.wobby.level

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import hlaaftana.wobby.GameData
import hlaaftana.wobby.things.Thing

import java.nio.ByteBuffer

class InactiveLevel extends Level {
	XYMap<LinkedList<InactiveThing>> placements = new XYMap<LinkedList<InactiveThing>>()
	LinkedList<InactiveThing> all = []

	byte[] encode(){
		byte[] j = JsonOutput.toJson(data).getBytes('UTF-8')
		short a = (maxX as BigInteger).toByteArray().length
		short b = (maxY as BigInteger).toByteArray().length
		List<Thing> thingsaaa = all*.thing.groupBy { it.identifier }.values().collect { it[0] }
		short c = (thingsaaa.size() as BigInteger).toByteArray().length
		short d = (thingsaaa*.identifier.max { it.size() }.size() as BigInteger).toByteArray().length
		println([a, b, c, d])
		List x = []
		x.addAll(j)
		x.add((byte) (a >> 8))
		x.add((byte) a)
		x.add((byte) (b >> 8))
		x.add((byte) b)
		x.add((byte) (c >> 8))
		x.add((byte) c)
		x.add((byte) (d >> 8))
		x.add((byte) d)
		x.addAll((maxX as BigInteger).toByteArray())
		x.addAll((maxY as BigInteger).toByteArray())
		x.addAll((thingsaaa.size() as BigInteger).toByteArray())
		Map twi = [:]
		thingsaaa.eachWithIndex { t, Integer i ->
			def co = t.identifier.getBytes('UTF-8')
			def ab = (co.length as BigInteger).toByteArray()
			ab = (([0] * (d - ab.length)) + ab.toList()) as byte[]
			x.addAll(((i + 1) as BigInteger).toByteArray())
			x.addAll(ab)
			x.addAll(co)
			twi[t.identifier] = i + 1
		}
		all.each {
			def ab = (it.x as BigInteger).toByteArray()
			ab = (([0] * (a - ab.length)) + ab.toList()) as byte[]
			def cd = (it.y as BigInteger).toByteArray()
			cd = (([0] * (b - cd.length)) + cd.toList()) as byte[]
			def ef = (twi[it.thing.identifier] as BigInteger).toByteArray()
			ef = (([0] * (c - ef.length)) + ef.toList()) as byte[]
			x.addAll(ab)
			x.addAll(cd)
			x.addAll(ef)
		}
		x as byte[]
	}

	static InactiveLevel decode(byte[] bytes){
		InactiveLevel level = new InactiveLevel()
		def string = new String(bytes, 'UTF-8')
		int bracks = 0
		def currentQuote
		boolean escaped = false
		for (int iaa = 0; iaa < string.size(); ++iaa){
			if (currentQuote){
				if (string[iaa] == '\\') escaped = true
				if (!escaped && string[iaa] == currentQuote) currentQuote = null
			}else{
				if (string[iaa] in ['"', '\'']){ currentQuote = string[iaa]; continue }
				if (string[iaa] == '{') ++bracks
				if (string[iaa] == '}') --bracks
				if (bracks == 0) {
					level.data = new JsonSlurper().parseText(string.substring(0, iaa + 1))
					bytes = bytes.toList().drop(iaa + 1) as byte[]; break
				}
			}
		}
		ByteBuffer x = ByteBuffer.wrap(bytes)
		int i = 0
		short a = x.getShort(i)
		i += 2
		short b = x.getShort(i)
		i += 2
		short c = x.getShort(i)
		i += 2
		short d = x.getShort(i)
		i += 2
		level.maxX = new BigInteger((1..a).collect { x.get(i++) } as byte[])
		level.maxY = new BigInteger((1..b).collect { x.get(i++) } as byte[])
		def tnum = new BigInteger((1..c).collect { x.get(i++) } as byte[])
		Map itt = [:]
		for (int it = 0; it < tnum; ++it){
			def id = new BigInteger((1..c).collect { x.get(i++) } as byte[])
			def e = new BigInteger((1..d).collect { x.get(i++) } as byte[])
			def iden = new String((1..e).collect { x.get(i++) } as byte[], 'UTF-8')
			itt[id] = iden
		}
		itt = itt.collectEntries { k, v ->
			def p = GameData.thing(v)
			if (!p) throw new IllegalArgumentException('No tile ' + v)
			else [(k): p]
		}
		while (i != x.capacity()){
			int tx = new BigInteger((1..a).collect { x.get(i++) } as byte[]) as int
			int ty = new BigInteger((1..b).collect { x.get(i++) } as byte[]) as int
			def ti = new BigInteger((1..c).collect { x.get(i++) } as byte[])
			level.place(tx, ty, itt[ti])
		}
		level
	}

	def place(int x, int y, Thing thing){
		InactiveThing pt = new InactiveThing(level: this,
			x: x, y: y, thing: thing)
		if (placements.containsKey(x, y)) placements[x, y] << pt
		else placements[x, y] = [pt] as LinkedList<InactiveThing>
		all << pt
		pt
	}

	def remove(InactiveThing pt){
		if (null == pt) return
		placements[pt.x, pt.y]?.removeElement(pt)
		all.removeElement(pt)
	}

	def removeTopIn(int x, int y){
		remove(thingsIn(x, y).with { size() == 0 ? null : last() })
	}

	LinkedList<InactiveThing> thingsIn(int x, int y){
		all.findAll { it.contains(x, y) } as LinkedList<InactiveThing>
	}

	ActiveLevel activate(){
		def x = new ActiveLevel(maxX: maxX, maxY: maxY, data: data)
		x.things = all.collect {
			new ActiveThing(level: x, x: it.x, y: it.y, thing: it.thing)
		} as LinkedList<ActiveThing>
		x
	}
}
