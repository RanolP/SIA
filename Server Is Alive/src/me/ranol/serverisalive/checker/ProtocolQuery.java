package me.ranol.serverisalive.checker;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.channels.IllegalBlockingModeException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import me.ranol.serverisalive.Options;
import me.ranol.serverisalive.utils.ByteUtils;

public class ProtocolQuery extends Query {
	public static final String VERSION = "mcversion";
	public static final String GAME_TYPE = "gametype";
	public static final String GAME_ID = "gameid";
	public static final String SERVER_PORT = "mcport";
	public static final String SERVER_IP = "mcip";
	public static final String MAP_NAME = "map";
	public static final String PLUGINS = "plugins";
	public static final String BUKKIT_NAME = "bukkit";

	public ProtocolQuery(String ip, int port) {
		super(ip, port);
	}

	@Override
	public CheckResults connect() {
		try (DatagramSocket udp = new DatagramSocket()) {
			InetSocketAddress address = new InetSocketAddress(
					InetAddress.getByName(getIPAddress()), getPort());
			udp.setSoTimeout(Options.get(Options.TIMEOUT));
			sendPacket(udp, address, 0xFE, 0xFD, 0x09, 0x01, 0x01, 0x01, 0x01);
			byte[] recieve = new byte[10240];
			int challenge;
			{
				recievePacket(udp, recieve);
				byte[] buffer = new byte[11];
				System.arraycopy(recieve, 5, buffer, 0, buffer.length);
				challenge = Integer.parseInt(new String(buffer).trim());
			}
			sendPacket(udp, address, 0xFE, 0xFD, 0x00, 0x01, 0x01, 0x01, 0x01,
					challenge >> 24, challenge >> 16, challenge >> 8,
					challenge, 0x00, 0x00, 0x00, 0x00);
			recievePacket(udp, recieve);
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
					values.put(key, new String(temp, Charset.forName("UTF-8")));
					key = null;
					continue;
				}
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
			setConnected(true);
			return CheckResults.CONNECTED;

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
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

	void sendPacket(DatagramSocket soc, InetSocketAddress address, byte... data)
			throws IOException {
		DatagramPacket packet = new DatagramPacket(data, data.length,
				address.getAddress(), address.getPort());
		soc.send(packet);
	}

	void sendPacket(DatagramSocket soc, InetSocketAddress address, int... data)
			throws IOException {
		byte[] d = new byte[data.length];
		for (int i = 0; i < data.length; i++) {
			d[i] = (byte) (data[i] & 0xff);
		}
		sendPacket(soc, address, d);
	}

	DatagramPacket recievePacket(DatagramSocket soc, byte[] buffer)
			throws IOException {
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		soc.receive(packet);
		return packet;
	}

}
