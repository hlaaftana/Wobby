package hlaaftana.wobby.level

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import hlaaftana.wobby.GameData
import hlaaftana.wobby.things.Thing

import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicInteger

@CompileStatic
class InactiveLevel extends Level<InactiveThing> {
	XYMap<List<InactiveThing>> placements = new XYMap<List<InactiveThing>>()
	List<InactiveThing> all = new ArrayList<>()

	byte[] encode() {
		Byte[] j = JsonOutput.toJson(data).getBytes('UTF-8')
		short a = (short) (maxX as BigInteger).toByteArray().length
		short b = (short) (maxY as BigInteger).toByteArray().length
		List<String> identifiers = new ArrayList<>()
		identifiers.addAll(all*.thing*.identifier.toSet())
		int largestIdentifier = 0
		for (it in identifiers)
			if (it.length() > largestIdentifier)
				largestIdentifier = it.length()
		short c = (short) (identifiers.size() as BigInteger).toByteArray().length
		short d = (short) (largestIdentifier as BigInteger).toByteArray().length
		List<Byte> x = new ArrayList<>()
		x.addAll(j)
		x.add((byte) (a >> 8))
		x.add((byte) a)
		x.add((byte) (b >> 8))
		x.add((byte) b)
		x.add((byte) (c >> 8))
		x.add((byte) c)
		x.add((byte) (d >> 8))
		x.add((byte) d)
		x.addAll((maxX as BigInteger).toByteArray() as Byte[])
		x.addAll((maxY as BigInteger).toByteArray() as Byte[])
		x.addAll((identifiers.size() as BigInteger).toByteArray() as Byte[])
		Map<String, Integer> twi = [:]
		for (int i = 0; i < identifiers.size(); ++i) {
			String t = identifiers[i]
			def co = t.getBytes('UTF-8') as Byte[]
			def ab = (co.length as BigInteger).toByteArray()
			def ab1 = new byte[d]
			System.arraycopy(ab, 0, ab1, 0, d)
			def identifierId = new byte[c]
			System.arraycopy(((i + 1) as BigInteger).toByteArray(), 0, identifierId, 0, c)
			x.addAll(identifierId as Byte[])
			x.addAll(ab1 as Byte[])
			x.addAll(co)
			twi[t] = i + 1
		}
		for (it in all) {
			def ab = (it.x as BigInteger).toByteArray()
			def ab1 = new byte[a]
			System.arraycopy(ab, 0, ab1, a - ab.length, ab.length)
			def cd = (it.y as BigInteger).toByteArray()
			def cd1 = new byte[b]
			System.arraycopy(cd, 0, cd1, b - cd.length, cd.length)
			def ef = (twi[it.thing.identifier] as BigInteger).toByteArray()
			def ef1 = new byte[c]
			System.arraycopy(ef, 0, ef1, c - ef.length, ef.length)
			x.addAll(ab1 as Byte[])
			x.addAll(cd1 as Byte[])
			x.addAll(ef1 as Byte[])
		}
		x as byte[]
	}

	static InactiveLevel decode(byte[] bytes){
		InactiveLevel level = new InactiveLevel()
		def string = new String(bytes, 'UTF-8')
		int bracks = 0
		def currentQuote
		boolean escaped = false
		for (int iaa = 0; iaa < string.size(); ++iaa) {
			if (currentQuote) {
				if (string[iaa] == '\\') escaped = true
				if (!escaped && string[iaa] == currentQuote) currentQuote = null
			} else {
				if (string[iaa] == '"' || string[iaa] == '\''){ currentQuote = string[iaa]; continue }
				if (string[iaa] == '{') ++bracks
				if (string[iaa] == '}') --bracks
				if (bracks == 0) {
					level.data = (Map<String, Object>) new JsonSlurper().parseText(string.substring(0, iaa + 1))
					bytes = bytes.toList().drop(iaa + 1) as byte[]; break
				}
			}
		}
		ByteBuffer x = ByteBuffer.wrap(bytes)
		AtomicInteger i = new AtomicInteger()
		short xSize = x.getShort(i.get())
		short ySize = x.getShort(i.addAndGet(2))
		short idSize = x.getShort(i.addAndGet(2))
		short identifierSizeSize = x.getShort(i.addAndGet(2))
		i.addAndGet(2)
		level.maxX = (int) new BigInteger(ğ(i, x, xSize))
		level.maxY = (int) new BigInteger(ğ(i, x, ySize))
		def tnum = new BigInteger(ğ(i, x, idSize))
		Map<BigInteger, String> itt = [:]
		for (int it = 0; it < tnum; ++it){
			def id = new BigInteger(ğ(i, x, idSize))
			def identifierSize = new BigInteger(ğ(i, x, identifierSizeSize))
			def iden = new String(ğ(i, x, identifierSize), 'UTF-8')
			itt[id] = iden
		}
		Map<BigInteger, Thing> ittt = new HashMap<>(itt.size(), 1)
		for (e in itt) {
			def p = GameData.thing(e.value)
			if (null == p) throw new IllegalArgumentException('No tile such as ' + e.value)
			else ittt.put(e.key, p)
		}
		while (i.get() <= x.capacity() - xSize - ySize - idSize) {
			int tx = new BigInteger(ğ(i, x, xSize)) as int
			int ty = new BigInteger(ğ(i, x, ySize)) as int
			def ti = new BigInteger(ğ(i, x, idSize))
			level.place(tx, ty, (Thing) ittt[ti])
		}
		level
	}

	private static byte[] ğ(AtomicInteger i, ByteBuffer x, short a) {
		def r = new byte[a]
		for (int j = 0; j < a; ++j) {
			r[j] = x.get(i.getAndIncrement())
		}
		r
	}

	private static byte[] ğ(AtomicInteger i, ByteBuffer x, BigInteger a) {
		def r = new byte[(int) a]
		for (int j = 0; j < a; ++j) {
			r[j] = x.get(i.getAndIncrement())
		}
		r
	}

	InactiveThing place(int x, int y, Thing thing){
		InactiveThing pt = thing.inactive(this, x, y)
		if (placements.containsKey(x, y)) placements[x, y] << pt
		else placements[x, y] = [pt]
		all << pt
		pt
	}

	void remove(InactiveThing pt){
		if (null == pt) return
		placements[pt.x, pt.y]?.removeElement(pt)
		all.removeElement(pt)
	}

	void removeTopIn(int x, int y) {
		def it = thingsIn(x, y)
		if (!it.empty) remove(it.last())
	}

	List<InactiveThing> thingsIn(int x, int y) {
		def r = new ArrayList<InactiveThing>()
		for (it in all) if (it.contains(x, y)) r.add(it)
		r
	}

	ActiveLevel activate() {
		def x = new ActiveLevel(maxX: maxX, maxY: maxY, data: data)
		def things = new ArrayList<>(all.size())
		for (it in all) things.add it.thing.activate(x, it)
		x.things = things
		x
	}
}
