package me.ranol.serverisalive.utils;

import javax.swing.JPanel;

public class ComponentUtils {
	public static JPanel newPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(null);
		return panel;
	}

	public static int percent(int w, double perc) {
		return (int) (w * perc / 100);
	}
}
