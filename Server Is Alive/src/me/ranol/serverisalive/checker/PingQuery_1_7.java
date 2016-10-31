package me.ranol.serverisalive.checker;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import me.ranol.serverisalive.Options;
import me.ranol.serverisalive.gui.PlayerObject;
import me.ranol.serverisalive.utils.ImageDecoder;
import me.ranol.serverisalive.utils.MotdParser;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PingQuery_1_7 extends PingQuery {

	public PingQuery_1_7(String ip, int port) {
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
			JsonObject json = new JsonParser().parse(raw).getAsJsonObject();
			JsonElement desc = json.get("description");
			StringBuilder description = new StringBuilder("");
			if (desc.isJsonObject()) {
				JsonObject obj = desc.getAsJsonObject();
				MotdParser.parse(description, obj.get("text"));
				MotdParser.parse(description, obj.get("extra"));
			} else if (desc.isJsonPrimitive()) {
				MotdParser.parse(description, desc);
			}
			JsonElement player = json.get("players");
			if (player.isJsonObject()) {
				JsonObject obj = player.getAsJsonObject();
				set(MAX_PLAYERS, obj.get("max").getAsInt());
				set(PLAYERS, obj.get("online").getAsInt());
				JsonElement sample = obj.get("sample");
				set(ONLINE_PLAYERS, Collections.emptyList());
				if (sample != null && !sample.isJsonNull()) {
					JsonArray array = sample.getAsJsonArray();
					List<PlayerObject> players = new ArrayList<>();
					array.forEach(e -> {
						if (e.isJsonObject()) {
							players.add(new PlayerObject(e.getAsJsonObject()
									.get("name").getAsString(), e
									.getAsJsonObject().get("id").getAsString()));
						}
					});
					set(ONLINE_PLAYERS, players);
				}
			}
			JsonElement favicon = json.get("favicon");
			if (favicon.isJsonPrimitive()) {
				BufferedImage img = ImageDecoder.read(Base64.getDecoder()
						.decode(favicon.getAsString().split(",")[1].replace(
								"\n", "").replace("\r", "")));
				set(SERVER_ICON, img);
			}
			JsonElement version = json.get("version");
			if (version.isJsonObject()) {
				JsonObject obj = version.getAsJsonObject();
				JsonElement name = obj.get("name");
				if (!name.isJsonNull())
					set(VERSION, name.getAsString());
				JsonElement protocol = obj.get("protocol");
				if (!protocol.isJsonNull())
					set(VERSION, name.getAsInt());
			}
			set(MOTD, description.toString());
			return CheckResults.CONNECTED;
		} catch (SocketTimeoutException e) {
			return CheckResults.TIMEOUT;
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			return CheckResults.UNKNOWN_HOST;
		} catch (EOFException e) {
			return CheckResults.CANT_CONNECT;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close(dos);
			close(dis);
			close(is);
		}
		return CheckResults.OTHER;
	}

	public BufferedImage getServerIcon() {
		return get(SERVER_ICON);
	}

	public List<PlayerObject> getOnlineUsers() {
		return get(ONLINE_PLAYERS);
	}
}
