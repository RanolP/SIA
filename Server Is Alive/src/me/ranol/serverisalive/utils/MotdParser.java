package me.ranol.serverisalive.utils;

import com.google.gson.JsonElement;

public class MotdParser {
	public static void parse(StringBuilder b, JsonElement e) {
		if (e == null || e.isJsonNull())
			return;
		if (e.isJsonArray()) {
			e.getAsJsonArray().forEach(e2 -> parse(b, e2));
		}
		if (e.isJsonObject()) {
			e.getAsJsonObject().entrySet().forEach(entry -> {
				switch (entry.getKey()) {
				case "text":
				case "extra":
					parse(b, entry.getValue());
					break;
				case "color":
					parseColor(b, entry.getValue().getAsString());
					break;
				case "bold":
					b.append("§l");
					break;
				case "italic":
					b.append("§o");
					break;
				}
			});
		}
		if (e.isJsonPrimitive()) {
			b.append(e.getAsString());
		}
	}

	public static void parseColor(StringBuilder b, String c) {
		b.append(Colors.valueOf(c.toUpperCase()).getValue());
	}

	enum Colors {
		BLACK("§0"),

		DARK_BLUE("§1"),

		DARK_GREEN("§2"),

		DARK_AQUA("§3"),

		DARK_RED("§4"),

		DARK_PURPLE("§5"),

		GOLD("§6"),

		GRAY("§7"),

		DARK_GRAY("§8"),

		BLUE("§9"),

		GREEN("§a"),

		AQUA("§b"),

		RED("§c"),

		LIGHT_PURPLE("§d"),

		YELLOW("§e"),

		white("§f");
		String value;

		Colors(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}
}
