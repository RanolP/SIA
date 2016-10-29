package me.ranol.serverisalive.checker;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Set;

import me.ranol.serverisalive.utils.ValueMap;

public abstract class Query {
	public static final String IP = "ip";
	public static final String PORT = "port";
	public static final String MOTD = "motd";
	public static final String MAX_PLAYERS = "max-players";
	public static final String PLAYERS = "players";
	private ValueMap values = ValueMap.empty();

	public Query(String ip, int port) {
		set(IP, ip);
		set(PORT, port);
		set("check-start", System.currentTimeMillis());
		setConnected(false);
		set(MAX_PLAYERS, Integer.MIN_VALUE);
		set(PLAYERS, Integer.MIN_VALUE);
		set(MOTD, "Not connected");
	}

	public String getIPAddress() {
		return get(IP);
	}

	public boolean isAlive(int checkDuration) {
		try (Socket s = new Socket()) {
			InetAddress address = InetAddress.getByName(getIPAddress());
			s.connect(new InetSocketAddress(address, getPort()), checkDuration);
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	public int getPort() {
		return get(PORT);
	}

	public String getMotd() {
		return get(MOTD);
	}

	public int getMaxPlayers() {
		return get(MAX_PLAYERS);
	}

	public int getPlayers() {
		return get(PLAYERS);
	}

	public double getCheckingTime() {
		return (System.currentTimeMillis() - (long) get("check-start")) / 1000d;
	}

	public boolean isConnected() {
		return get("connected");
	}

	void setConnected(boolean val) {
		set("connected", val);
	}

	public abstract CheckResults connect();

	public Set<String> keySet() {
		return values.keys();
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		if (values.containsKey(key))
			return (T) values.get(key);
		return null;
	}

	public <T> void set(String key, T value) {
		values.set(key, value);
	}
}
