package me.ranol.serverisalive.gui;

import java.awt.Component;
import java.awt.Image;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;

public class PlayerRenderer extends DefaultListCellRenderer {
	private static final long serialVersionUID = 3408506997618493946L;

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		// TODO Auto-generated method stub
		JLabel label = (JLabel) super.getListCellRendererComponent(list, value,
				index, isSelected, cellHasFocus);
		PlayerObject o = (PlayerObject) value;
		try {
			label.setIcon(new ImageIcon(ImageIO.read(new URL(o.getUrl()))
					.getScaledInstance(32, 32, Image.SCALE_SMOOTH)));
		} catch (Exception e) {
		}
		label.setIconTextGap(10);
		label.setHorizontalTextPosition(JLabel.RIGHT);
		label.setText(o.getName() + "\n" + o.getUUID());
		return label;
	}
}
