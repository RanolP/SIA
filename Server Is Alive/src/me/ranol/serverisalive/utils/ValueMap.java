package me.ranol.serverisalive.utils;

import java.util.HashMap;
import java.util.Set;

public class ValueMap {
	private HashMap<String, Object> real = new HashMap<>();

	public static ValueMap empty() {
		return new ValueMap();
	}

	public <T> ValueMap set(String key, T value) {
		real.put(key, value);
		return this;
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		if (!real.containsKey(key))
			return null;
		return (T) real.get(key);
	}

	public Set<String> keys() {
		return real.keySet();
	}

	public boolean containsKey(String key) {
		return real.containsKey(key);
	}

	@Override
	public String toString() {
		return real.toString();
	}
}
