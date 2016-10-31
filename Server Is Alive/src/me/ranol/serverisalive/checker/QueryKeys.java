package me.ranol.serverisalive.checker;

public class QueryKeys {
	// from Query.
	public static final String IP = "ip";
	public static final String PORT = "port";
	public static final String MOTD = "motd";
	public static final String MAX_PLAYERS = "max-players";
	public static final String PLAYERS = "players";

	// from Protocol Query.
	public static final String GAME_TYPE = "gametype";
	public static final String GAME_ID = "gameid";
	public static final String SERVER_PORT = "mcport";
	public static final String SERVER_IP = "mcip";
	public static final String PLUGINS = "plugins";
	public static final String MAP_NAME = "map";

	// from Ping Query.
	public static final String PING_VERSION = "ping";
	public static final String PROTOCOL_VERSION = "protocol";

	// Duplicated, from Protocol & Ping
	public static final String ONLINE_PLAYERS = "online";
	public static final String VERSION = "version";
	public static final String BUKKIT_NAME = "bukkit";
	public static final String SERVER_ICON = "icon";
}
