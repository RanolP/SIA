package me.ranol.serverisalive.utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import me.ranol.serverisalive.Options;

public class UDPUtils {

	public static DatagramSocket createDefaultSocket() throws SocketException {
		DatagramSocket socket = new DatagramSocket();
		socket.setSoTimeout(Options.get(Options.TIMEOUT));
		return socket;
	}

	public static void sendPacket(DatagramSocket socket,
			InetSocketAddress address, byte... data) throws IOException {
		DatagramPacket packet = new DatagramPacket(data, data.length,
				address.getAddress(), address.getPort());
		socket.send(packet);
	}

	public static void sendPacket(DatagramSocket soc,
			InetSocketAddress address, int... data) throws IOException {
		byte[] d = new byte[data.length];
		for (int i = 0; i < data.length; i++) {
			d[i] = convert(data[i]);
		}
		sendPacket(soc, address, d);
	}

	public static byte convert(int i) {
		return (byte) (i & 0xff);
	}

	public static DatagramPacket recievePacket(DatagramSocket soc, byte[] buffer)
			throws IOException {
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		soc.receive(packet);
		return packet;
	}
}
