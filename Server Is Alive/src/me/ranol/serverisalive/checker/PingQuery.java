package me.ranol.serverisalive.checker;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;

public abstract class PingQuery extends Query {

	public int getProtocolVersion() {
		return get(PROTOCOL_VERSION);
	}

	public PingQuery(String ip, int port) {
		super(ip, port);
	}

	public int readVarInt(DataInputStream in) throws IOException {
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

	public void writeVarInt(DataOutputStream out, int data) throws IOException {
		while (true) {
			if ((data & 0xFFFFFF80) == 0) {
				out.writeByte(data);
				return;
			}
			out.writeByte(data & 0x7F | 0x80);
			data >>>= 7;
		}
	}

	public void close(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
	}

	@Override
	public String toString() {
		return super.toString();
	}
}
