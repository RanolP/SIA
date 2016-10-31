package me.ranol.serverisalive.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class ColoredPane extends JTextPane {
	private static final long serialVersionUID = 993373930416032212L;

	public void appendText(SimpleAttributeSet sas, boolean[] bis, Color color,
			String text, Font f) {
		StyleConstants.setFontFamily(sas, f.getFamily());
		StyleConstants.setFontSize(sas, f.getSize());
		StyleConstants.setBold(sas, bis[0]);
		StyleConstants.setItalic(sas, bis[1]);
		StyleConstants.setStrikeThrough(sas, bis[2]);
		StyleConstants.setForeground(sas, color);
		try {
			getStyledDocument().insertString(getText().length(), text, sas);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void paint(Graphics g) {
		setBackground(Color.BLACK);
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		super.paint(g);
	}
}
