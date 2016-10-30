package me.ranol.serverisalive.utils;

import java.awt.Color;

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

	public static enum Colors {
		BLACK("§0", new Color(0, 0, 0)),

		DARK_BLUE("§1", new Color(0, 0, 170)),

		DARK_GREEN("§2", new Color(0, 170, 0)),

		DARK_AQUA("§3", new Color(0, 170, 170)),

		DARK_RED("§4", new Color(170, 0, 0)),

		DARK_PURPLE("§5", new Color(170, 0, 170)),

		GOLD("§6", new Color(255, 170, 0)),

		GRAY("§7", new Color(170, 170, 170)),

		DARK_GRAY("§8", new Color(85, 85, 85)),

		BLUE("§9", new Color(85, 85, 255)),

		GREEN("§a", new Color(85, 255, 85)),

		AQUA("§b", new Color(85, 255, 255)),

		RED("§c", new Color(255, 85, 85)),

		LIGHT_PURPLE("§d", new Color(255, 85, 255)),

		YELLOW("§e", new Color(255, 255, 85)),

		WHITE("§f", new Color(255, 255, 255));
		String value;
		Color awt;

		Colors(String value, Color awt) {
			this.value = value;
			this.awt = awt;
		}

		public String getValue() {
			return value;
		}

		public Color getAWTColor() {
			return awt;
		}

		public static Colors getByCode(String code) {
			for (Colors c : values()) {
				if (c.getValue().equals(code))
					return c;
			}
			return null;
		}
	}
}
