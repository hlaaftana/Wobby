package hlaaftana.wobby.level

import groovy.transform.CompileStatic

@CompileStatic
class XYMap<V> implements Map<Long, V> {
	Map<Long, V> inner = new HashMap<>()

	static long pack(int x, int y) { (((long) x) << 32) | (y & 0xffffffffL) }

	int size() { inner.size() }

	boolean isEmpty() { size() == 0 }

	boolean containsKey(key) { inner.containsKey(key) }

	boolean containsKey(int x, int y) { containsKey(pack(x, y)) }

	boolean containsValue(value) { inner.containsValue(value) }

	V get(key) { inner.get(key) }

	V get(int x, int y) { get(pack(x, y)) }

	V put(Long key, V value) { inner.put(key, value) }

	V put(int x, int y, V value) { put(pack(x, y), value) }

	V getAt(List a) { get((int) a[0], (int) a[1]) }

	V putAt(List a, V value) { put((int) a[0], (int) a[1], value) }

	V remove(key) { inner.remove(key) }

	V remove(int x, int y) { remove(pack(x, y)) }

	void putAll(Map m) { inner.putAll(m) }

	void clear() { inner.clear() }

	Set keySet() { inner.keySet() }

	Collection values() { inner.values() }

	Set<Entry<Long, V>> entrySet() { inner.entrySet() }
}
