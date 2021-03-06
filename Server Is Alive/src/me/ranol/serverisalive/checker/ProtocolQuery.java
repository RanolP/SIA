package me.ranol.serverisalive.checker;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.channels.IllegalBlockingModeException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import me.ranol.serverisalive.gui.PlayerObject;
import me.ranol.serverisalive.utils.ByteUtils;
import me.ranol.serverisalive.utils.UDPUtils;

public class ProtocolQuery extends Query {

	public ProtocolQuery(String ip, int queryport) {
		super(ip, queryport);
	}

	public String getGameId() {
		return get(GAME_ID);
	}

	public String getGameVersion() {
		return get(VERSION);
	}

	public String getGameType() {
		return get(GAME_TYPE);
	}

	public int getServerPort() {
		return get(SERVER_PORT);
	}

	public String getServerIP() {
		return get(SERVER_IP);
	}

	public String getMapName() {
		return get(MAP_NAME);
	}

	public List<String> getPlugins() {
		return get(PLUGINS);
	}

	public String getBukkitName() {
		return get(BUKKIT_NAME);
	}

	@Override
	public CheckResults connect() {
		try (DatagramSocket udp = UDPUtils.createDefaultSocket()) {
			InetSocketAddress address = new InetSocketAddress(
					InetAddress.getByName(getIPAddress()), getPort());
			UDPUtils.sendPacket(udp, address, 0xFE, 0xFD, 0x09, 0x01, 0x01,
					0x01, 0x01);
			byte[] recieve = new byte[10240];
			int challenge;
			{
				UDPUtils.recievePacket(udp, recieve);
				byte[] buffer = new byte[11];
				System.arraycopy(recieve, 5, buffer, 0, buffer.length);
				challenge = Integer.parseInt(new String(buffer).trim());
			}
			UDPUtils.sendPacket(udp, address, 0xFE, 0xFD, 0x00, 0x01, 0x01,
					0x01, 0x01, challenge >> 24, challenge >> 16,
					challenge >> 8, challenge, 0x00, 0x00, 0x00, 0x00);
			UDPUtils.recievePacket(udp, recieve);
			recieve = ByteUtils.removeFirst(recieve, 5);
			String key = null;
			int last = 0;
			int index = -1;
			HashMap<String, String> values = new HashMap<>();
			while ((index = ByteUtils.indexOf(recieve, (byte) 0x00, index + 1)) != -1) {
				byte[] temp = ByteUtils.cut(recieve, last + 1, index);
				last = index;
				if (temp.length == 0)
					break;
				if (key == null) {
					key = new String(temp);
					continue;
				} else {
					values.put(key, new String(temp));
					key = null;
					continue;
				}
			}
			last = 0;
			List<PlayerObject> users = new ArrayList<>();
			while ((index = ByteUtils.indexOf(recieve,
					new byte[] { 0x00, 0x00 }, index + 1)) != -1) {
				if (last == 0) {
					last = index;
					continue;
				}
				byte[] temp = ByteUtils.cut(recieve, last + 1, index);
				if (temp.length == 0)
					break;
				temp = ByteUtils.cut(temp, 1, temp.length);
				last = index;
				users.add(new PlayerObject(new String(temp), ""));

			}
			set(MOTD, values.get("hostname"));
			set(GAME_TYPE, values.get("gametype"));
			set(GAME_ID, values.get("game_id"));
			set(VERSION, values.get("version"));
			set(PLAYERS, Integer.parseInt(values.get("numplayers")));
			set(MAX_PLAYERS, Integer.parseInt(values.get("maxplayers")));
			set(SERVER_PORT, Integer.parseInt(values.get("hostport")));
			set(SERVER_IP, values.get("hostip"));
			set(MAP_NAME, values.get("map"));
			set(BUKKIT_NAME, values.get("plugins").split(":")[0]);
			List<String> plugins = new ArrayList<>(Arrays.asList(values.get(
					"plugins").split(":")[1].split(";"))).stream()
					.map(s -> s.trim()).collect(Collectors.toList());
			set(PLUGINS, plugins);
			set(ONLINE_PLAYERS, users);
			setConnected(true);
			return CheckResults.CONNECTED;

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			return CheckResults.UNKNOWN_HOST;
		} catch (IllegalBlockingModeException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SocketTimeoutException e) {
			return CheckResults.TIMEOUT;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return CheckResults.OTHER;
	}

	@Override
	public String toString() {
		return super.toString() + ", version=" + getGameVersion() + ", gameid="
				+ getGameId() + ", gametype=" + getGameType() + ", gameport="
				+ getServerPort() + ", gameip=" + getServerIP() + ", map="
				+ getMapName() + ", plugins=" + getPlugins() + ", bukkit="
				+ getBukkitName();
	}
}
