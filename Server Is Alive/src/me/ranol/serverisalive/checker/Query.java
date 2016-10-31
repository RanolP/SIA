package me.ranol.serverisalive.checker;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Set;

import me.ranol.serverisalive.utils.ValueMap;

public abstract class Query extends QueryKeys{
	private ValueMap values = ValueMap.empty();

	public Query(String ip, int port) {
		set(IP, ip);
		set(PORT, port);
		set("check-start", System.currentTimeMillis());
		setConnected(false);
		set(MAX_PLAYERS, 0);
		set(PLAYERS, 0);
		set(MOTD, "Not connected");
	}

	public String getIPAddress() {
		return get(IP);
	}

	public static boolean isAlive(String ip, int port, int checkDuration) {
		try (Socket s = new Socket()) {
			s.connect(new InetSocketAddress(ip, port), checkDuration);
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

	public <T> T get(String key) {
		if (values.containsKey(key))
			return (T) values.get(key);
		return null;
	}

	public <T> void set(String key, T value) {
		values.set(key, value);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " ip=" + getIPAddress() + ", port="
				+ getPort() + ", connected=" + isConnected() + ", motd="
				+ getMotd() + ", players=" + getPlayers() + "/"
				+ getMaxPlayers();
	}
}
