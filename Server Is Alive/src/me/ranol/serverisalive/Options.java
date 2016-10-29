package me.ranol.serverisalive;

import me.ranol.serverisalive.utils.ValueMap;

public class Options {
	public static final String TIMEOUT = "timeout";

	private Options() {
	}

	private static ValueMap map = new ValueMap();

	public static void set(String key, Object value) {
		map.set(key, value);
	}

	public static <T> T get(String key) {
		return map.get(key);
	}
}
