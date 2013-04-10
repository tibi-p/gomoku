package gomoku.util;

public class Pair<K extends Comparable<K>, V extends Comparable<V>> implements
		Comparable<Pair<K, V>> {

	private final K key;
	private final V value;

	public Pair(K key, V value) {
		this.key = key;
		this.value = value;
	}

	public K getKey() {
		return key;
	}

	public V getValue() {
		return value;
	}

	public int compareTo(Pair<K, V> o) {
		int result = key.compareTo(o.key);
		if (result != 0)
			return result;
		else
			return value.compareTo(o.value);
	}

	public String toString() {
		return String.format("(%s, %s)", key, value);
	}

}
