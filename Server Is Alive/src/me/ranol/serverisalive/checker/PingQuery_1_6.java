package me.ranol.serverisalive.checker;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

import me.ranol.serverisalive.Options;

public class PingQuery_1_6 extends PingQuery {

	public PingQuery_1_6(String ip, int port) {
		super(ip, port);
	}

	@Override
	public CheckResults connect() {
		InputStream is = null;
		DataOutputStream dos = null;
		DataInputStream dis = null;

		try (Socket socket = new Socket()) {
			socket.setSoTimeout(Options.get(Options.TIMEOUT));
			InetSocketAddress address = new InetSocketAddress(getIPAddress(),
					getPort());
			socket.connect(address, Options.get(Options.TIMEOUT));
			dos = new DataOutputStream(socket.getOutputStream());
			is = socket.getInputStream();

			// HandShake Packet.
			dos.write(0xFE);
			dos.write(0x01);
			dos.write(0xFA);
			writeLegacyString(dos, "MC|PingHost");
			dos.writeShort(getIPAddress().length() * 2 + 7);
			dos.write(74);
			writeLegacyString(dos, getIPAddress());
			dos.writeInt(getPort());
			dos.flush();

			dis = new DataInputStream(is);
			int packet = dis.readUnsignedByte();
			if (packet != 0xFF) {
				throw new IOException("패킷 ID가 올바르지 않습니다.");
			}
			String s = readLegacyString(dis);
			if (s.matches("§[a-f0-9]{1}.+")) {
			} else if (s.startsWith("§")) {
				String[] arr = s.substring(1).split("\0");
				try {
					set(PING_VERSION, Integer.parseInt(arr[0]));
					set(PROTOCOL_VERSION, Integer.parseInt(arr[1]));
					set(VERSION, arr[2]);
					set(MOTD, arr[3]);
					set(PLAYERS, Integer.parseInt(arr[arr.length - 2]));
					set(MAX_PLAYERS, Integer.parseInt(arr[arr.length - 1]));
				} catch (NumberFormatException e) {
					String[] array = s.split("§");
					set(MOTD,
							s.substring(
									0,
									s.length()
											- array[array.length - 2].length()
											+ array[array.length - 1].length()));
					set(PLAYERS, Integer.parseInt(array[array.length - 2]));
					set(MAX_PLAYERS, Integer.parseInt(array[array.length - 1]));
					set(PING_VERSION, -1);
					set(PROTOCOL_VERSION, -1);
					set(VERSION, "???");
					System.out.println(getMotd());
					System.out.println(getPlayers());
					System.out.println(getMaxPlayers());
				}
			} else {
				System.out.println("PING_1_6: " + s);
			}
			set(PROTOCOL_VERSION, 0);
			return CheckResults.CONNECTED;
		} catch (ConnectException | SocketTimeoutException e) {
			return CheckResults.TIMEOUT;
		} catch (EOFException e) {
			return CheckResults.CANT_CONNECT;
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			return CheckResults.UNKNOWN_HOST;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			switch (e.getMessage()) {
			case "패킷 ID가 올바르지 않습니다.":
				return CheckResults.INVALID_PACKET;
			default:
				break;
			}
			e.printStackTrace();
		} finally {
			close(dos);
			close(dis);
			close(is);
		}
		return CheckResults.OTHER;
	}

	String[] parse(String s) {
		String[] split = s.split("§");
		String[] data = { split[0], split[1], split[2], null,
				split[split.length - 2], split[split.length - 1] };
		data[4] = "";
		for (int i = 3; i < split.length - 2; i++) {
			data[4] += "§" + split[i];
		}
		return data;
	}

	private String readLegacyString(DataInputStream dis) throws IOException {
		int len = dis.readShort();
		if (len <= 0)
			throw new IOException("문자열의 길이가 올바르지 않습니다.");
		byte[] data = new byte[len * 2];
		dis.readFully(data);
		return new String(data, StandardCharsets.UTF_16BE);
	}

	void writeLegacyString(DataOutputStream dos, String s) throws IOException {
		dos.writeShort(s.length());
		dos.write(s.getBytes(StandardCharsets.UTF_16BE));
	}

}
