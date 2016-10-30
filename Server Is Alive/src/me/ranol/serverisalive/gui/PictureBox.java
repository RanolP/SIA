package me.ranol.serverisalive.gui;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

public class PictureBox extends Canvas {
	private static final long serialVersionUID = 1L;
	Image buffer = null;

	public PictureBox() {
	}

	public void setImage(BufferedImage img) {
		if (img != null) {
			buffer = toImage(img);
			repaint();
		} else {
			buffer = null;
			repaint();
		}
	}

	@Override
	public void update(Graphics g) {
		paint(g);
	}

	public void paint(Graphics g) {
		if (buffer != null) {
			g.drawImage(buffer, 0, 0, getWidth(), getHeight(), this);
		} else {
			g.setColor(getParent().getBackground());
			g.fillRect(0, 0, getWidth(), getHeight());
			g.drawImage(null, 0, 0, getWidth(), getHeight(), this);
		}
	}

	public Image toImage(BufferedImage img) {
		return Toolkit.getDefaultToolkit().createImage(img.getSource());
	}
}