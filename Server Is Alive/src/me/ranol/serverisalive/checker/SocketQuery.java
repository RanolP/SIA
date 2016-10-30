package me.ranol.serverisalive.checker;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.channels.IllegalBlockingModeException;
import java.util.function.Consumer;

import me.ranol.serverisalive.Options;

public class SocketQuery extends Query {

	public SocketQuery(String ip, int port) {
		super(ip, port);
	}

	@Override
	public CheckResults connect() {
		try (Socket socket = new Socket()) {
			socket.connect(new InetSocketAddress(getIPAddress(), getPort()),
					Options.get(Options.TIMEOUT));
			DataOutputStream dos = new DataOutputStream(
					socket.getOutputStream());
			dos.writeByte(254);
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			new Thread(() -> {
				try {
					Thread.sleep(((Integer) Options.get(Options.TIMEOUT))
							.longValue());
					dis.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}).start();
			dis.read();
			short size = dis.readShort();
			StringBuilder builder = new StringBuilder();
			while ((size--) > 0)
				builder.append(dis.readChar());
			setConnected(true);
			String result = builder.toString();
			int temp;
			int max = Integer.parseInt(result.substring(temp = result
					.lastIndexOf("§") + 1));
			result = result.substring(0, temp - 1);
			int con = Integer.parseInt(result.substring(temp = result
					.lastIndexOf("§") + 1));
			result = result.substring(0, temp - 1);
			set(MOTD, result);
			set(PLAYERS, con);
			set(MAX_PLAYERS, max);
			return CheckResults.CONNECTED;
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			return CheckResults.UNKNOWN_HOST;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (SocketTimeoutException e) {
			return CheckResults.TIMEOUT;
		} catch (IllegalBlockingModeException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			if (e.getMessage().equals("Socket closed")) {
				set(MOTD, "소켓 방식 연결을 지원하지 않는 서버입니다.");
				set(PLAYERS, 0);
				set(MAX_PLAYERS, 0);
				return CheckResults.CANT_CONNECT;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (!isAlive(getIPAddress(), getPort(), 1500)) {
			set(MOTD, "현재 서버는 오프라인입니다.");
			set(PLAYERS, 0);
			set(MAX_PLAYERS, 0);
			return CheckResults.SERVER_OFFLINE;
		}
		return CheckResults.OTHER;
	}

	public void forEach(Consumer<? super String> consumer) {
		keySet().forEach(consumer);
	}

}
