package me.ranol.serverisalive.checker;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

import me.ranol.serverisalive.Options;

public class PingQuery_1_5 extends PingQuery {

	public PingQuery_1_5(String ip, int port) {
		super(ip, port);
	}

	@Override
	public CheckResults connect() {
		InputStream is = null;
		DataOutputStream dos = null;
		InputStreamReader reader = null;

		try (Socket socket = new Socket()) {
			socket.setSoTimeout(Options.get(Options.TIMEOUT));
			InetSocketAddress address = new InetSocketAddress(getIPAddress(),
					getPort());
			socket.connect(address, Options.get(Options.TIMEOUT));
			dos = new DataOutputStream(socket.getOutputStream());
			is = socket.getInputStream();

			// HandShake Packet.
			dos.write(new byte[] { (byte) 0xFE, (byte) 0x01 });
			reader = new InputStreamReader(is, StandardCharsets.UTF_16BE);
			int id = is.read();
			if (id == -1) {
				throw new IOException("Premature end of stream");
			}
			if (id != 0xFF) {
				throw new IOException("패킷 ID가 올바르지 않습니다.");
			}
			int len = reader.read();
			if (len == -1) {
				throw new IOException("스트림이 조기 종료되었습니다.");
			}
			if (len == 0) {
				throw new IOException("올바르지 않은 문자열의 길이를 받았습니다.");
			}
			char[] chars = new char[len];
			if (reader.read(chars, 0, len) != len) {
				throw new IOException("스트림이 조기 종료되었습니다.");
			}
			String s = new String(chars);
			if (s.startsWith("§")) {
				String[] arr = s.substring(1).split("\0");
				set(PING_VERSION, Integer.parseInt(arr[0]));
				set(PROTOCOL_VERSION, Integer.parseInt(arr[1]));
				set(VERSION, arr[2]);
				set(MOTD, arr[3]);
				set(PLAYERS, Integer.parseInt(arr[4]));
				set(MAX_PLAYERS, Integer.parseInt(arr[5]));
			} else {
				System.out.println(s);
			}
			return CheckResults.CONNECTED;
		} catch (SocketTimeoutException e) {
			return CheckResults.TIMEOUT;
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
			case "올바르지 않은 문자열의 길이를 받았습니다.":
			case "스트림이 조기 종료되었습니다.":
				return CheckResults.CANT_CONNECT;
			default:
				break;
			}
			e.printStackTrace();
		} finally {
			close(dos);
			close(reader);
			close(is);
		}
		return CheckResults.OTHER;
	}

	@Override
	public String toString() {
		return super.toString();
	}
}
