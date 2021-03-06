package me.ranol.serverisalive.utils;

import java.util.Arrays;

public class ByteUtils {
	public static int indexOf(byte[] target, byte[] key) {
		return indexOf(target, key, 0);
	}

	public static int indexOf(byte[] target, byte[] key, int from) {
		int result = -1;
		for (int i = from; i + key.length < target.length; i++) {
			if (Arrays.equals(Arrays.copyOfRange(target, i, i + key.length),
					key)) {
				result = i;
				break;
			}
		}
		return result;
	}

	public static int indexOf(byte[] target, byte key) {
		return indexOf(target, new byte[] { key });
	}

	public static int indexOf(byte[] target, byte key, int from) {
		return indexOf(target, new byte[] { key }, from);
	}

	public static byte[] cut(byte[] real, int from, int to) {
		return Arrays.copyOfRange(real, from, to);
	}

	public static byte[] removeLast(byte[] target, int end) {
		return Arrays.copyOfRange(target, 0, end);
	}

	public static byte[] removeFirst(byte[] target, int start) {
		return Arrays.copyOfRange(target, start, target.length);
	}

	public static byte[] append(byte[] real, byte... data) {
		byte[] result = new byte[real.length + data.length];
		System.arraycopy(real, 0, result, 0, real.length);
		System.arraycopy(data, 0, result, real.length, data.length);
		return result;
	}

	public static byte[] insert(byte[] real, int index, byte... data) {
		return append(append(cut(real, 0, index), data),
				cut(real, index, real.length));
	}

	public static byte convert(int i) {
		return (byte) (i & 0xff);
	}

	public static byte[] convert(int... i) {
		byte[] data = new byte[i.length];
		for (int j = 0; j < i.length; data[j] = convert(i[j++]))
			;
		return data;
	}
}
