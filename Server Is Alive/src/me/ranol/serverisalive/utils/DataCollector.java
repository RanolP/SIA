package me.ranol.serverisalive.utils;

import java.util.LinkedHashMap;
import java.util.Set;

public class DataCollector<T> {
	LinkedHashMap<T, Integer> collected = new LinkedHashMap<>();

	public void collect(T item) {
		if (collected.containsKey(item))
			collected.put(item, collected.get(item) + 1);
		else
			collected.put(item, 1);
	}

	public T max() {
		if (collected.keySet().size() == 0)
			return null;
		return collected.keySet().stream().max((s1, s2) -> {
			if (collected.get(s1) > collected.get(s2))
				return 1;
			else if (collected.get(s1) < collected.get(s2))
				return -1;
			return 0;
		}).get();
	}

	public Set<T> values() {
		return collected.keySet();
	}

	public void clear() {
		collected.clear();
	}

	public T min() {
		if (collected.keySet().size() == 0)
			return null;
		return collected.keySet().stream().min((s1, s2) -> {
			if (collected.get(s1) > collected.get(s2))
				return 1;
			else if (collected.get(s1) < collected.get(s2))
				return -1;
			return 0;
		}).get();
	}
}
