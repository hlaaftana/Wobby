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
		byte[] j = JsonOutput.toJson(data).getBytes('UTF-8')
		final maxXBytes = BigInteger.valueOf(maxX).toByteArray(), maxYBytes = BigInteger.valueOf(maxY).toByteArray()
		short a = (short) maxXBytes.length
		short b = (short) maxYBytes.length
		int largestIdentifier = 0, sumIdentifier = 0, lenIdentifier = 0
		List<String> extraIdentifiers = new ArrayList<>()
		for (thing in all) {
			final it = thing.thing.identifier
			if (extraIdentifiers.contains(it)) continue
			++lenIdentifier
			final len = it.length()
			sumIdentifier += len
			if (len > largestIdentifier) largestIdentifier = len
			extraIdentifiers.add(it)
		}
		final identifierSizeBytes = BigInteger.valueOf(lenIdentifier).toByteArray()
		final largestIdentifierBytes = BigInteger.valueOf(largestIdentifier).toByteArray()
		short c = (short) identifierSizeBytes.length
		short d = (short) largestIdentifierBytes.length
		byte[] x = new byte[j.length + 8 + a + b + c + lenIdentifier * (c + d) + sumIdentifier + all.size() * (a + b + c)]
		int i = 0
		System.arraycopy(j, 0, x, i, j.length)
		i += j.length
		x[i++] = (byte) (a >> 8)
		x[i++] = (byte) a
		x[i++] = (byte) (b >> 8)
		x[i++] = (byte) b
		x[i++] = (byte) (c >> 8)
		x[i++] = (byte) c
		x[i++] = (byte) (d >> 8)
		x[i++] = (byte) d
		System.arraycopy(maxXBytes, 0, x, i, a); i += a
		System.arraycopy(maxYBytes, 0, x, i, b); i += b
		System.arraycopy(identifierSizeBytes, 0, x, i, identifierSizeBytes.length); i += identifierSizeBytes.length
		Map<String, Integer> twi = new HashMap<>()
		for (int ii = 0; ii < lenIdentifier; ++ii) {
			final t = extraIdentifiers.get(ii)
			def co = t.getBytes('UTF-8')
			def ab = BigInteger.valueOf(co.length).toByteArray()
			final index = BigInteger.valueOf(ii + 1).toByteArray()
			System.arraycopy(index, 0, x, i + c - index.length, index.length); i += c
			System.arraycopy(ab, 0, x, i + d - ab.length, ab.length); i += d
			System.arraycopy(co, 0, x, i, co.length); i += co.length
			twi[t] = ii + 1
		}
		for (it in all) {
			def ab = BigInteger.valueOf(it.x).toByteArray()
			System.arraycopy(ab, 0, x, i + a - ab.length, ab.length); i += a
			def cd = BigInteger.valueOf(it.y).toByteArray()
			System.arraycopy(cd, 0, x, i + b - cd.length, cd.length); i += b
			def ef = BigInteger.valueOf(twi[it.thing.identifier]).toByteArray()
			System.arraycopy(ef, 0, x, i + c - ef.length, ef.length); i += c
		}
		x
		/*List<Byte> x = new ArrayList<>()
		x.addAll(j)
		x.add((byte) (a >> 8))
		x.add((byte) a)
		x.add((byte) (b >> 8))
		x.add((byte) b)
		x.add((byte) (c >> 8))
		x.add((byte) c)
		x.add((byte) (d >> 8))
		x.add((byte) d)
		x.addAll(maxXBytes)
		x.addAll(maxYBytes)
		x.addAll(identifierSizeBytes)
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
		x as byte[]*/
	}

	static InactiveLevel decode(byte[] bytes) {
		InactiveLevel level = new InactiveLevel()
		def string = new String(bytes, 'UTF-8')
		int bracks = 0
		char currentQuote = 0
		boolean escaped = false
		for (int iaa = 0; iaa < string.size(); ++iaa) {
			final ch = string.charAt(iaa)
			if (currentQuote != ((char) 0)) {
				if (ch == ((char) '\\')) escaped = true
				if (!escaped && ch == currentQuote) currentQuote = 0
			} else {
				if (ch == ((char) '"') || ch == ((char) '\'')) {
					currentQuote = ch; continue
				} else if (ch == ((char) '{')) ++bracks
				else if (ch == ((char) '}')) --bracks
				if (bracks == 0) {
					++iaa
					level.data = (Map<String, Object>) new JsonSlurper().parseText(string.substring(0, iaa))
					def oldBytes = bytes
					bytes = new byte[oldBytes.length - iaa]
					System.arraycopy(oldBytes, iaa, bytes, 0, bytes.length)
					break
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
		level.maxX = new BigInteger(ğ(i, x, xSize)).intValue()
		level.maxY = new BigInteger(ğ(i, x, ySize)).intValue()
		def tnum = new BigInteger(ğ(i, x, idSize)).intValue()
		def itt = new HashMap<BigInteger, String>(tnum)
		for (int it = 0; it < tnum; ++it) {
			def id = new BigInteger(ğ(i, x, idSize))
			def identifierSize = new BigInteger(ğ(i, x, identifierSizeSize))
			def iden = new String(ğ(i, x, identifierSize), 'UTF-8')
			itt[id] = iden
		}
		def ittt = new HashMap<BigInteger, Thing>(itt.size(), 1)
		for (e in itt.entrySet()) {
			def p = GameData.thing(e.value)
			if (null == p) throw new IllegalArgumentException('No tile such as ' + e.value)
			else ittt.put(e.key, p)
		}
		while (i.get() <= x.capacity() - xSize - ySize - idSize) {
			int tx = new BigInteger(ğ(i, x, xSize)).intValue()
			int ty = new BigInteger(ğ(i, x, ySize)).intValue()
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

	InactiveThing place(int x, int y, Thing thing) {
		InactiveThing pt = thing.inactive(this, x, y)
		if (placements.containsKey(x, y)) placements[x, y].add(pt)
		else placements[x, y] = [pt]
		all.add(pt)
		pt
	}

	void remove(InactiveThing pt) {
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
