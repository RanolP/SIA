package me.ranol.serverisalive.checker;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

import me.ranol.serverisalive.Options;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PingQuery extends Query {
	public static final String SERVER_ICON = "icon";

	public PingQuery(String ip, int port) {
		super(ip, port);
	}

	int readVarInt(DataInputStream in) throws IOException {
		int i = 0, j = 0;
		while (true) {
			int k = in.readByte();
			i |= (k & 0x7F) << j++ * 7;
			if (j > 5)
				throw new RuntimeException("VarInt의 값이 너무 큽니다.");
			if ((k & 0x80) != 128)
				break;
		}
		return i;
	}

	void writeVarInt(DataOutputStream out, int data) throws IOException {
		while (true) {
			if ((data & 0xFFFFFF80) == 0) {
				out.writeByte(data);
				return;
			}
			out.writeByte(data & 0x7F | 0x80);
			data >>>= 7;
		}
	}

	@Override
	public CheckResults connect() {
		InputStream is = null;
		DataOutputStream dos = null;
		DataInputStream dis = null;
		InputStreamReader reader = null;

		try (Socket socket = new Socket()) {
			socket.setSoTimeout(Options.get(Options.TIMEOUT));
			InetSocketAddress address = new InetSocketAddress(
					InetAddress.getByName(getIPAddress()), getPort());
			socket.connect(address, Options.get(Options.TIMEOUT));
			dos = new DataOutputStream(socket.getOutputStream());
			is = socket.getInputStream();
			reader = new InputStreamReader(is);

			// HandShake Packet.
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream handshake = new DataOutputStream(baos);
			handshake.writeByte(0x00);
			writeVarInt(handshake, 4);
			writeVarInt(handshake, getIPAddress().length());
			handshake.write(getIPAddress().getBytes(StandardCharsets.UTF_8));
			handshake.writeShort(getPort());
			writeVarInt(handshake, 1);
			writeVarInt(dos, baos.size());
			dos.write(baos.toByteArray());

			dos.writeByte(0x01);
			dos.writeByte(0x00);
			dis = new DataInputStream(is);

			readVarInt(dis); // Packet Size Reading.
			int id = readVarInt(dis);
			if (id == -1) {
				throw new IOException("Premature end of stream.");
			}
			if (id != 0x00) {
				throw new IOException("받은 패킷의 ID가 올바르지 않습니다.");
			}
			int length = readVarInt(dis);
			if (length == -1) {
				throw new IOException("Premature end of stream.");
			}
			if (length == 0) {
				throw new IOException("올바르지 않은 문자열의 길이를 받았습니다.");
			}
			byte[] data = new byte[length];
			dis.readFully(data);

			String raw = new String(data);
			System.out.println(raw);
			JsonObject json = new JsonParser().parse(raw).getAsJsonObject();
			JsonElement desc = json.get("description");
			StringBuilder description = new StringBuilder("");
			if (desc.isJsonObject()) {
				JsonObject obj = desc.getAsJsonObject();
				parse(description, obj.get("text"));
				parse(description, obj.get("extra"));
			} else if (desc.isJsonPrimitive()) {
				parse(description, desc);
			}
			set(MOTD, description.toString());
			return CheckResults.CONNECTED;
		} catch (SocketTimeoutException e) {
			return CheckResults.TIMEOUT;
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close(dos);
			close(dis);
			close(is);
			close(reader);
		}
		return CheckResults.OTHER;
	}

	void parse(StringBuilder b, JsonElement e) {
		if (e == null || e.isJsonNull())
			return;
		if (e.isJsonArray()) {
			e.getAsJsonArray().forEach(e2 -> parse(b, e2));
		}
		if (e.isJsonObject()) {
			e.getAsJsonObject()
					.entrySet()
					.forEach(
							entry -> {
								if (entry.getKey().equals("text")
										|| entry.getKey().equals("extra"))
									parse(b, entry.getValue());
								else if (entry.getKey().equals("color")) {
									parseColor(b, entry.getValue()
											.getAsString());
								} else if (entry.getKey().equals("bold")) {
									b.append("§l");
								} else if (entry.getKey().equals("italic")) {
									b.append("§o");
								} else if (entry.getKey().equals("bold")) {
									b.append("§l");
								}
							});
		}
		if (e.isJsonPrimitive()) {
			System.out.println(e);
			b.append(e.getAsString());
		}
	}

	void parseColor(StringBuilder b, String c) {
		switch (c.toLowerCase()) {
		case "black":
			b.append("§0");
			break;
		case "dark_blue":
			b.append("§1");
			break;
		case "dark_green":
			b.append("§2");
			break;
		case "dark_aqua":
			b.append("§3");
			break;
		case "dark_red":
			b.append("§4");
			break;
		case "dark_purple":
			b.append("§5");
			break;
		case "gold":
			b.append("§6");
			break;
		case "gray":
			b.append("§7");
			break;
		case "dark_gray":
			b.append("§8");
			break;
		case "blue":
			b.append("§9");
			break;
		case "green":
			b.append("§a");
			break;
		case "aqua":
			b.append("§b");
			break;
		case "red":
			b.append("§c");
			break;
		case "light_purple":
			b.append("§d");
			break;
		case "yellow":
			b.append("§e");
			break;
		case "white":
			b.append("§f");
			break;
		default:
			break;
		}
	}

	void close(Closeable closeable) {
		if (closeable != null)
			try {
				closeable.close();
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
	}
}
