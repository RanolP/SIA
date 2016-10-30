package me.ranol.serverisalive.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageDecoder {
	public static BufferedImage read(byte[] s) {
		BufferedImage result = null;
		ByteArrayInputStream bais = new ByteArrayInputStream(s);
		try {
			result = ImageIO.read(bais);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return result;
	}
}
